package br.com.victorbarberino.kaori.monitor.volume_monitor;

import br.com.victorbarberino.kaori.api.BinanceAPI;
import br.com.victorbarberino.kaori.api.KrakenCustomAPI;
import br.com.victorbarberino.kaori.core.KaoriArbitrageEngine;
import br.com.victorbarberino.kaori.model.PropertiesData;
import br.com.victorbarberino.kaori.model.VolumeData;
import br.com.victorbarberino.kaori.monitor.Monitor;
import br.com.victorbarberino.kaori.notify.WhatsappNotifier;
import br.com.victorbarberino.kaori.service.CalcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VolumeMonitor implements Monitor {
    private static final Logger log = LoggerFactory.getLogger(VolumeMonitor.class);

    private Queue<Double> shortTermVolumes;
    private Queue<Double> longTermVolumes;
    private int shortTermPeriod;
    private int longTermPeriod;
    private double shortTermSum;
    private double longTermSum;
    private BinanceAPI binanceAPI;
    private KrakenCustomAPI krakenAPI;
    private KaoriArbitrageEngine kaoriArbitrageEngine;
    private PropertiesData pd;
    private double transactionFees; // Taxas de transação

    public VolumeMonitor(PropertiesData pd) {
        this.pd = pd;
        init(10, 60);
    }

    public void init(int shortTermPeriod, int longTermPeriod) {
        this.shortTermVolumes     = new LinkedList<>();
        this.longTermVolumes      = new LinkedList<>();
        this.shortTermPeriod      = shortTermPeriod;
        this.longTermPeriod       = longTermPeriod;
        this.binanceAPI           = new BinanceAPI(this.pd);
        this.krakenAPI            = new KrakenCustomAPI(this.pd);
        this.transactionFees      = pd.getTransactionFees();
        this.kaoriArbitrageEngine = new KaoriArbitrageEngine(this.pd);
    }

    private void addVolume(double volume) {
        shortTermVolumes.add(volume);
        shortTermSum += volume;
        if(shortTermVolumes.size() > shortTermPeriod)
            shortTermSum -= shortTermVolumes.poll();

        longTermVolumes.add(volume);
        longTermSum += volume;
        if(longTermVolumes.size() > longTermPeriod)
            longTermSum -= longTermVolumes.poll();
    }

    private double getShortTermAverage() {
        if (shortTermVolumes.isEmpty()) {
            return 0;
        }
        return shortTermSum / shortTermVolumes.size();
    }

    private double getLongTermAverage() {
        if (longTermVolumes.isEmpty()) {
            return 0;
        }
        return longTermSum / longTermVolumes.size();
    }

    private boolean isVolumeIncreasing() {
        return getShortTermAverage() > getLongTermAverage();
    }

    private double getVolumeGrowthPercentage() {
        double longTermAverage = getLongTermAverage();
        if (longTermAverage == 0) return 0;
        return ((getShortTermAverage() - longTermAverage) / longTermAverage) * 100;
    }

    private boolean isVolumeIncreasingSignificantly(double threshold) {
        double growth = getVolumeGrowthPercentage();
        log.info("Volume growth percentage: {}%", growth);
        return growth > threshold || growth > (threshold * 0.5); // Exemplo de relaxamento
    }

    private void monitorVolume() throws Exception {
        log.info("Updating monitoring......");
        updateVolumes();

        double volumeGrowth = getVolumeGrowthPercentage();
        log.info("Volume growth: {}%", volumeGrowth);

        if (isVolumeIncreasingSignificantly(pd.getVolumeGrowthThreshold())) {
            log.info("Volume is increasing significantly.");
            triggerVolumeIncreaseEvent();
        }

        VolumeData binanceVolume = this.binanceAPI.getVolume(pd.getBinanceTargetSymbol());
        VolumeData krakenVolume = this.krakenAPI.getVolume(pd.getKrakenTargetSymbol());

        double binancePrice = binanceVolume.getWeightedAvgPrice();
        double krakenPrice = krakenVolume.getWeightedAvgPrice();

        // Ajuste no cálculo de lucro, aumentando a tolerância
        double profitMargin = CalcService.calculateProfitMarginWithSlippage(binancePrice, krakenPrice, transactionFees, pd.getSlippage());

        log.info("[VOLUME MONITOR] Profit Margin with slippage: {}%", profitMargin);

        // Considerar margens de lucro menores como aceitáveis
        if (profitMargin > pd.getMinimumProfitMargin()) {
            log.info("Opportunity found with a profit margin of {}%", profitMargin);
            triggerVolumeIncreaseEvent();
        } else {
            log.info("No profitable opportunity. Profit margin: {}%", profitMargin);
        }
        logAdditionalData(binanceVolume, krakenVolume);
    }

    private void updateVolumes() throws Exception {
        VolumeData binanceVolume = this.binanceAPI.getVolume(pd.getBinanceTargetSymbol());
        VolumeData krakenVolume = this.krakenAPI.getVolume(pd.getKrakenTargetSymbol());

        double binancePrice = binanceVolume.getWeightedAvgPrice();
        double krakenPrice = krakenVolume.getWeightedAvgPrice();

        double profitMargin = CalcService.calculateProfitMargin(binancePrice, krakenPrice, transactionFees);

        // Log da margem de lucro calculada
        log.info("[VOLUME MONITOR] Profit Margin: {}%", profitMargin);

        if (profitMargin > pd.getMinimumProfitMargin()) {
            log.info("Opportunity found with a profit margin of {}%", profitMargin);
            triggerVolumeIncreaseEvent();
        } else {
            log.info("No profitable opportunity. Profit margin: {}%", profitMargin);
        }

        // Atualização adicional: Média ponderada de preço, maior/menor preço, variação de preço, e número de transações
        logAdditionalData(binanceVolume, krakenVolume);
    }

    private void logAdditionalData(VolumeData binanceVolume, VolumeData krakenVolume) {
        double binanceHighPrice = binanceVolume.getHighPrice();
        double binanceLowPrice = binanceVolume.getLowPrice();
        double binancePriceChange = binanceVolume.getPriceChange();
        int binanceTradesCount = binanceVolume.getTradesCount();

        double krakenHighPrice = krakenVolume.getHighPrice();
        double krakenLowPrice = krakenVolume.getLowPrice();
        double krakenPriceChange = krakenVolume.getPriceChange();
        int krakenTradesCount = krakenVolume.getTradesCount();

        // Exemplo de log dos novos dados monitorados
        log.info("[VOLUME MONITOR] Dados adicionais:");
        log.info("Binance - Weighted average price: {}, Highest price: {}, Lowest price: {}, Price change: {}, Number of transactions: {}",
                binanceVolume.getWeightedAvgPrice(), binanceHighPrice, binanceLowPrice, binancePriceChange, binanceTradesCount);
        log.info("Kraken - Weighted average price: {}, Highest price: {}, Lowest price: {}, Price change: {}, Number of transactions: {}",
                krakenVolume.getWeightedAvgPrice(), krakenHighPrice, krakenLowPrice, krakenPriceChange, krakenTradesCount);
    }

    private void triggerVolumeIncreaseEvent() throws Exception {
        System.out.println("****************************************");
        System.out.println("**         OPPORTUNITY FOUND!         **");
        System.out.println("****************************************");
        log.info("Volume surge event triggered! The market may be in a good time to trade.");

        // Obter preços das exchanges
        VolumeData binanceVolume = this.binanceAPI.getVolume(pd.getBinanceTargetSymbol());
        VolumeData krakenVolume = this.krakenAPI.getVolume(pd.getKrakenTargetSymbol());

        double buyPrice = binanceVolume.getLowPrice(); // Preço da exchange mais barata (exemplo: Binance)
        double sellPrice = krakenVolume.getHighPrice(); // Preço da exchange mais cara (exemplo: Kraken)
        double amount = 0.3; // Quantidade de criptomoedas a comprar e vender

        // Criar um ArbitrageExecutor e tentar executar a arbitragem
        // Chama o método execute e obtém o Future<Boolean>
        Future<Boolean> future = this.kaoriArbitrageEngine.execute(buyPrice, sellPrice, amount);

        // Bloqueia até obter o resultado (true ou false)
        Boolean success = future.get();

        if (success) {
            log.info("Arbitrage executed successfully.");
        } else {
            log.warn("Arbitrage execution failed or was not profitable.");
        }

        // Notificação via WhatsApp
        String message = "🚀 *Oportunidade de Arbitragem Detectada!* 🚀\n\n"
                + "📊 Um aumento significativo no volume foi identificado no mercado.\n"
                + "💼 Isso pode indicar uma boa janela para realizar operações de arbitragem.\n\n"
                + "👉 Fique atento e avalie as melhores oportunidades!\n\n"
                + "_Esta é uma mensagem automática, fique à vontade para responder se precisar de suporte._";

        WhatsappNotifier.sendMessage(pd, message);
    }

    @Override
    public CompletableFuture<Void> startMonitoringAsync(ScheduledExecutorService scheduler) {
        return CompletableFuture.runAsync(() -> {
            log.info("[VOLUME MONITOR] Initializing volume monitoring asynchronously with interval: {} minute(s)", pd.getMonitorIntervalPeriod());
            scheduler.scheduleAtFixedRate(() -> {
                log.info("[VOLUME MONITOR] Task triggered");
                try {
                    monitorVolume();
                } catch (Exception e) {
                    log.error("it was not possible to perform monitoring.");
                }
            }, 0, pd.getMonitorIntervalPeriod(), TimeUnit.MINUTES);
        });
    }

    @Override
    public void stopMonitoring(ScheduledExecutorService scheduler) {
        log.info("[VOMLUME MONITOR] Closing volume monitoring...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                log.warn("[VOMLUME MONITOR] Forcing volume monitoring to stop...");
                scheduler.shutdownNow();
                this.kaoriArbitrageEngine.shutdown();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    @Override
    public void monitor() {
        try {
            log.info("[VOMLUME MONITOR] Performing volume monitoring...");
            monitorVolume();
        } catch (Exception e) {
            log.error("[VOMLUME MONITOR] Error during volume monitoring: {}" , e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "Monitoring trading volumes";
    }

    @Override
    public String getMonitorId() {
        return "VolumeMonitor";
    }
}
