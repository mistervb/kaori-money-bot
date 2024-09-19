package br.com.victorbarberino.kaori.core;

import br.com.victorbarberino.kaori.api.BinanceAPI;
import br.com.victorbarberino.kaori.api.KrakenCustomAPI;

public class KaoriArbitrageEngine {
    private BinanceAPI binanceAPI;
    private KrakenCustomAPI krakenAPI;

    public KaoriArbitrageEngine(BinanceAPI binanceAPI, KrakenCustomAPI krakenAPI) {
        this.binanceAPI = binanceAPI;
        this.krakenAPI = krakenAPI;
    }

    /**
     * Perform arbitration if an opportunity exists.
     * */
    public void executeArbitrage() {
        try {
            //double binancePrice = binanceAPI.getPrice("BTCUSDT");
            //double krakenPrice = krakenAPI.getPrice("BTCUSD");

            if (isArbitrageOpportunity(0, 0)) {
                // Execute buy and sell orders
                System.out.println("Arbitrage opportunity found!");
            }
        } catch (Exception e) {
            System.err.println("Deu erro");
        }
    }

    /**
     * Checks whether there is an arbitrage opportunity based on the buy and sell price.
     * <br>
     * The formula used in this method is: <code>(sell  price - buy price) > 0</code>.
     *
     * @param buyPrice
     * @param sellPrice
     * @return <b>true</b> if the opportunity was found or <b>false</b> if it was not found.
     * */
    private boolean isArbitrageOpportunity(double buyPrice, double sellPrice) {
        return (sellPrice - buyPrice) > 0;
    }
}
