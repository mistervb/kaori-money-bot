package br.com.victorbarberino.kaori.api;

import br.com.victorbarberino.kaori.model.PropertiesData;
import br.com.victorbarberino.kaori.model.VolumeData;
import com.fasterxml.jackson.databind.JsonNode;
import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.market.response.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KrakenCustomAPI {
    private static final Logger log = LoggerFactory.getLogger(KrakenCustomAPI.class);

    private PropertiesData pd;
    private KrakenAPI krakenApi;

    public KrakenCustomAPI(PropertiesData pd) {
        this.pd = pd;
        this.krakenApi = new KrakenAPI(pd.getKrakenAPIKey(), pd.getKrakenSecretKey());
    }

    /* Logs */

    public void logPing() {
        JsonNode pong = this.krakenApi.query(KrakenAPI.Public.SYSTEM_STATUS);
        log.info("Ping Info: {}", pong);
    }

    public void logAccountBalance() {
        JsonNode balance = this.krakenApi.query(KrakenAPI.Private.BALANCE);
        log.info("Account Balance: {}", balance);
    }

    public void logMarketInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("pair", pd.getKrakenTargetSymbol());
        JsonNode marketInfo = this.krakenApi.query(KrakenAPI.Public.TICKER, params);
        log.info("Makert Info: {}", marketInfo);
    }

    public VolumeData getVolume(String pair) {
        Map<String, Ticker> tickerResponse = krakenApi.ticker(List.of(pair));

        // Verificação se o par existe no mapa
        if (tickerResponse == null || !tickerResponse.containsKey(pair)) {
            log.error("[KRAKEN API] Pair '{}' not found in API return.", pair);
            throw new IllegalArgumentException("Pair not found: " + pair);
        }

        Ticker ticker = tickerResponse.get(pair);
        if (ticker == null || ticker.volume() == null) {
            log.error("[KRAKEN API] Volume not available for pair '{}'.", pair);
            throw new IllegalArgumentException("Volume not available for pair: " + pair);
        }

        // Extraindo os dados relevantes
        double volume24h = Double.parseDouble(ticker.volume().last24Hours().toString());
        double weightedAvgPrice = Double.parseDouble(ticker.volumeWeightedAveragePrice().last24Hours().toString());
        double highPrice = Double.parseDouble(ticker.high().last24Hours().toString());
        double lowPrice = Double.parseDouble(ticker.low().last24Hours().toString());
        double priceChange = Double.parseDouble(ticker.bid().price().toString());
        int tradesCount = ticker.tradeCount().last24Hours();

        return new VolumeData(volume24h, weightedAvgPrice, highPrice, lowPrice, priceChange, tradesCount);
    }

    public void logUserInfo() {
        String nonce = String.valueOf(System.currentTimeMillis());
        Map<String, String> params = new HashMap<>();
        params.put("nonce", nonce);
        JsonNode tradeHistory = this.krakenApi.query(KrakenAPI.Private.TRADES_HISTORY, params);
        log.info("User Trade History: {}", tradeHistory);
    }

    public void getTransactionFees(String symbol) {
        Map<String, String> params = new HashMap<>();
        params.put("pair", symbol);
        JsonNode jsonResponse = this.krakenApi.query(KrakenAPI.Private.TRADE_VOLUME, params);
        log.info("[KRAKEN] trades fee: {}", jsonResponse);
    }

    /**
     * Coloca uma ordem de compra (buy) na Kraken.
     * @param symbol O símbolo da criptomoeda, por exemplo, "XXBTZUSD" (Bitcoin/USD)
     * @param amount A quantidade a ser comprada
     * @param price O preço de compra (null para ordens de mercado)
     * @return true se a ordem foi colocada com sucesso, false caso contrário
     */
    public boolean placeBuyOrder(String symbol, double amount, Double price) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("pair", symbol); // Par de negociação
        parameters.put("type", "buy");  // Tipo de ordem (buy)
        parameters.put("volume", String.valueOf(amount)); // Quantidade

        // Verifica se é uma ordem limitada ou de mercado
        if (price != null) {
            parameters.put("ordertype", "limit"); // Ordem limitada
            parameters.put("price", price.toString()); // Define o preço limite
        } else {
            parameters.put("ordertype", "market"); // Ordem de mercado
        }

        try {
            // Fazer a requisição à API privada de AddOrder
            JsonNode orderResponse = krakenApi.query(KrakenAPI.Private.ADD_ORDER, parameters);

            // Verifica se há erros
            if (orderResponse.has("error") && orderResponse.get("error").size() > 0) {
                log.error("Failed to place buy order: {}", orderResponse.get("error"));
                return false;
            }

            // Verifica o status da ordem
            if (orderResponse.has("result")) {
                log.info("Buy order placed successfully: {}", orderResponse.get("result").toString());
                return true;
            } else {
                log.error("Unexpected response format: {}", orderResponse.toString());
                return false;
            }
        } catch (Exception e) {
            log.error("Error placing buy order on Kraken: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Coloca uma ordem de venda (sell) na Kraken.
     * @param symbol O símbolo da criptomoeda, por exemplo, "XXBTZUSD" (Bitcoin/USD)
     * @param amount A quantidade a ser vendida
     * @param price O preço de venda (use null para ordens de mercado)
     * @return true se a ordem foi colocada com sucesso, false caso contrário
     */
    public boolean placeSellOrder(String symbol, double amount, Double price) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("pair", symbol);  // Par de negociação, como "XXBTZUSD"
        parameters.put("type", "sell");  // Define que é uma ordem de venda
        parameters.put("volume", String.valueOf(amount));  // Quantidade a ser vendida

        // Verifica se a ordem é limitada ou de mercado
        if (price != null) {
            parameters.put("ordertype", "limit");  // Ordem limitada
            parameters.put("price", price.toString());  // Define o preço limite
        } else {
            parameters.put("ordertype", "market");  // Ordem de mercado
        }

        try {
            // Fazer a requisição à API privada de AddOrder
            JsonNode orderResponse = krakenApi.query(KrakenAPI.Private.ADD_ORDER, parameters);

            // Verifica se há erros
            if (orderResponse.has("error") && orderResponse.get("error").size() > 0) {
                log.error("Failed to place sell order: {}", orderResponse.get("error"));
                return false;
            }

            // Verifica o status da ordem
            if (orderResponse.has("result")) {
                log.info("Sell order placed successfully: {}", orderResponse.get("result").toString());
                return true;
            } else {
                log.error("Unexpected response format: {}", orderResponse.toString());
                return false;
            }
        } catch (Exception e) {
            log.error("Error placing sell order on Kraken: {}", e.getMessage());
            return false;
        }
    }
}
