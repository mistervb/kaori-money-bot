package br.com.victorbarberino.kaori.core.arbitrage;

import br.com.victorbarberino.kaori.api.BinanceAPI;
import br.com.victorbarberino.kaori.api.KrakenCustomAPI;
import br.com.victorbarberino.kaori.model.PropertiesData;
import br.com.victorbarberino.kaori.service.CalcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArbitrageExecutor {

    private static final Logger log = LoggerFactory.getLogger(ArbitrageExecutor.class);

    private BinanceAPI binanceAPI;
    private KrakenCustomAPI krakenAPI;
    private PropertiesData pd;

    public ArbitrageExecutor(PropertiesData pd) {
        this.pd = pd;
        this.binanceAPI = new BinanceAPI(pd);
        this.krakenAPI = new KrakenCustomAPI(pd);
    }

    /**
     * Executa a arbitragem entre Binance e Kraken.
     * @param buyPrice Preço de compra na exchange mais barata
     * @param sellPrice Preço de venda na exchange mais cara
     * @param amount Quantidade de criptomoedas a comprar e vender
     * @return true se a arbitragem foi bem-sucedida, false caso contrário
     */
    public boolean executeArbitrage(double buyPrice, double sellPrice, double amount) {
        // Taxas de transação
        double fees = pd.getTransactionFees();
        double slippage = pd.getSlippage();

        // Calcular margem de lucro com slippage
        double profitMargin = CalcService.calculateProfitMarginWithSlippage(buyPrice, sellPrice, fees, slippage);
        log.info("Profit margin with slippage: {}%", profitMargin);

        if (profitMargin <= pd.getMinimumProfitMargin()) {
            log.info("Arbitrage opportunity not profitable. Skipping execution.");
            return false;
        }

        // Etapas de execução
        boolean success = executeBuySell(buyPrice, sellPrice, amount);
        if (success) {
            log.info("Arbitrage successfully executed.");
            return true;
        } else {
            log.error("Arbitrage execution failed.");
            return false;
        }
    }

    /**
     * Executa as ordens de compra e venda nas exchanges.
     * @param buyPrice Preço de compra
     * @param sellPrice Preço de venda
     * @param amount Quantidade de criptomoedas
     * @return true se as operações de compra e venda forem bem-sucedidas
     */
    private boolean executeBuySell(double buyPrice, double sellPrice, double amount) {
        try {
            // Comprar na exchange mais barata (exemplo: Binance)
            log.info("Placing buy order on Binance for {} units at ${}", amount, buyPrice);
            boolean buySuccess = binanceAPI.placeBuyOrder(pd.getBinanceTargetSymbol(), amount, buyPrice);

            if (!buySuccess) {
                log.error("Failed to place buy order on Binance.");
                return false;
            }

            String krakenDepositAddress = krakenAPI.getDepositAddress(pd.getKrakenTargetSymbol());
            if(krakenDepositAddress == null) {
                log.error("Failed to get kraken deposit address.");
                return false;
            }

            // Transferir o ativo comprado para a carteira da kraken
            log.info("Withdrawing asset from Binance to Kraken address.");
            boolean withdrawSuccess = binanceAPI.withdraw(pd.getBinanceTargetSymbol(), amount, krakenDepositAddress, null);
            if(!withdrawSuccess) {
                log.error("Failed to withdraw asset from Binance.");
                return false;
            }

            // Esperar até que o ativo seja confirmado na rede e esteja disponível na Kraken
            log.info("Waiting for deposit to appear on Kraken.");
            boolean depositSuccess = krakenAPI.waitForDepositToAppearOnKraken(pd.getKrakenTargetSymbol(), amount);
            if (!depositSuccess) {
                System.err.println("Failed to deposit asset into Kraken.");
                return false;
            }

            // Vender na exchange mais cara (exemplo: Kraken)
            log.info("Placing sell order on Kraken for {} units at ${}", amount, sellPrice);
            boolean sellSuccess = krakenAPI.placeSellOrder(pd.getKrakenTargetSymbol(), amount, sellPrice);
            if (!sellSuccess) {
                log.error("Failed to place sell order on Kraken.");
                return false;
            }

            log.info("Arbitrage transaction completed successfully.");
            return true;
        } catch (Exception e) {
            log.error("Error during arbitrage execution: {}", e.getMessage());
            return false;
        }
    }
}