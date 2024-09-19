package br.com.victorbarberino.kaori.api;

import br.com.victorbarberino.kaori.model.PropertiesData;
import br.com.victorbarberino.kaori.model.VolumeData;
import br.com.victorbarberino.kaori.util.HttpUtil;
import br.com.victorbarberino.kaori.util.JsonUtil;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.spot.Trade;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BinanceAPI {
    private Logger log = LoggerFactory.getLogger(BinanceAPI.class);
    private PropertiesData pd;
    private SpotClient spotClient;

    public BinanceAPI(PropertiesData pd) {
        this.pd = pd;
        this.spotClient = new SpotClientImpl(pd.getBinanceAPIKey(), pd.getBinanceSecretKey());
    }

    /* Logs */

    public void logAccountBalance() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        String balance = this.spotClient.createTrade().account(parameters);
        log.info("Account Balance: {}", balance);
    }

    public void logPing() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        String pong = spotClient.createMarket().ping();
        log.info("Ping Info: {}", pong);
    }

    public void logUserInfo() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        String userInfo = spotClient.createWallet().accountStatus(parameters);
        JSONObject json = new JSONObject(userInfo);
        log.info("Account Status: {}", json.getString("data"));
    }

    public void logMakertInfo() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", pd.getBinanceTargetSymbol());

        String result = spotClient.createMarket().ticker(parameters);
        log.info("Market Info: {}", result);
    }

    public VolumeData getVolume(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);

        String response = spotClient.createMarket().ticker24H(parameters);
        // Parse o JSON para obter o campo "volume"
        JSONObject jsonResponse = new JSONObject(response);
        return new VolumeData(
                jsonResponse.getDouble("volume"),
                jsonResponse.getDouble("weightedAvgPrice"),
                jsonResponse.getDouble("highPrice"),
                jsonResponse.getDouble("lowPrice"),
                jsonResponse.getDouble("priceChange"),
                jsonResponse.getInt("count")
        );
    }

    public Map<String, Double> getTradeFees(String symbol) {
        Map<String, Double> tradeFees = new HashMap<>();
        try {
            String url = "https://api.binance.com/sapi/v1/asset/tradeFee?symbol=" + symbol;

            String response = HttpUtil.sendGetRequest(url, pd.getBinanceAPIKey(), pd.getBinanceSecretKey());
            JsonNode jsonResponse = JsonUtil.parse(response);

            if (jsonResponse.isArray() && jsonResponse.size() > 0) {
                JsonNode feesNode = jsonResponse.get(0);
                double makerFee = feesNode.get("makerCommission").asDouble();
                double takerFee = feesNode.get("takerCommission").asDouble();

                tradeFees.put("makerFee", makerFee);
                tradeFees.put("takerFee", takerFee);
            }
        } catch (IOException e) {
            log.error("Error fetching trade fees from Binance: {}", e.getMessage());
        }

        return tradeFees;
    }

    /**
     * Coloca uma ordem de compra (buy) na Binance.
     * @param symbol O símbolo da criptomoeda, por exemplo, "BTCUSDT"
     * @param amount A quantidade a ser comprada
     * @param price O preço de compra (use null para ordens de mercado)
     * @return true se a ordem foi colocada com sucesso, false caso contrário
     */
    public boolean placeBuyOrder(String symbol, double amount, Double price) {
        // Mapa de parâmetros para a ordem
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", "BUY");
        parameters.put("quantity", amount);

        // Se o preço for null, é uma ordem de mercado, caso contrário é limitada
        if (price != null) {
            parameters.put("type", "LIMIT");
            parameters.put("price", price);
            parameters.put("timeInForce", "GTC");  // GTC = Good Till Cancelled
        } else {
            parameters.put("type", "MARKET");
        }

        try {
            // Acessa a API de Trade para realizar a ordem
            Trade tradeClient = spotClient.createTrade();
            String response = tradeClient.newOrder(parameters);
            JSONObject jsonResponse = new JSONObject(response);

            // Verifica o status da resposta
            String status = jsonResponse.getString("status");
            if (status.equals("FILLED") || status.equals("NEW")) {
                System.out.println("Buy order placed successfully: " + jsonResponse.toString(2));
                return true;
            } else {
                System.out.println("Failed to place buy order: " + jsonResponse.toString(2));
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error placing buy order: " + e.getMessage());
            return false;
        }
    }
    /**
     * Realiza uma solicitação de saque de ativos da Binance.
     * @param asset O ativo a ser sacado, por exemplo, "BTC"
     * @param amount A quantidade a ser sacada
     * @param address O endereço de saque para onde o ativo será enviado
     * @param addressTag (Opcional) Uma tag para o endereço, se necessário
     * @return true se o saque foi solicitado com sucesso, false caso contrário
     */
    public boolean withdraw(String asset, double amount, String address, String addressTag) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("asset", asset);
        parameters.put("amount", amount);
        parameters.put("address", address);

        if (addressTag != null && !addressTag.isEmpty()) {
            parameters.put("addressTag", addressTag);
        }

        try {
            // Faz a solicitação de saque
            String response = spotClient.createWallet().withdraw(parameters);
            JSONObject jsonResponse = new JSONObject(response);

            // Verifica o status da resposta
            if (jsonResponse.has("code") && jsonResponse.getInt("code") == 200) {
                System.out.println("Withdraw request placed successfully: " + jsonResponse.toString(2));
                return true;
            } else {
                System.out.println("Failed to place withdraw request: " + jsonResponse.toString(2));
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error placing withdraw request: " + e.getMessage());
            return false;
        }
    }
}
