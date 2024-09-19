package br.com.victorbarberino.kaori.model;

public class VolumeData {
    private double volume;
    private double weightedAvgPrice;
    private double highPrice;
    private double lowPrice;
    private double priceChange;
    private int tradesCount;

    public VolumeData() {

    }

    public VolumeData(double volume, double weightedAvgPrice, double highPrice, double lowPrice, double priceChange, int tradesCount) {
        this.volume = volume;
        this.weightedAvgPrice = weightedAvgPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.priceChange = priceChange;
        this.tradesCount = tradesCount;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getWeightedAvgPrice() {
        return weightedAvgPrice;
    }

    public void setWeightedAvgPrice(double weightedAvgPrice) {
        this.weightedAvgPrice = weightedAvgPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public int getTradesCount() {
        return tradesCount;
    }

    public void setTradesCount(int tradesCount) {
        this.tradesCount = tradesCount;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }
}
