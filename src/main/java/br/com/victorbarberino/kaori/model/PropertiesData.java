package br.com.victorbarberino.kaori.model;

public class PropertiesData {
    private String whatsappAuthToken;
    private String whatsappBotIdNumber;
    private String whatsappBotNumber;
    private String whatsappMyNumber;

    private Double minimumProfitMargin;
    private Double transactionFees;

    private Integer monitorIntervalPeriod;
    private Double volumeGrowthThreshold;
    private Double slippage;

    private String BinanceAPIKey;
    private String BinanceSecretKey;
    private String BinanceTargetSymbol;

    private String KrakenAPIKey;
    private String KrakenSecretKey;
    private String KrakenTargetSymbol;

    public PropertiesData() {

    }

    public String getWhatsappAuthToken() {
        return this.whatsappAuthToken;
    }

    public String getWhatsappBotIdNumber() {
        return this.whatsappBotIdNumber;
    }

    public String getWhatsappBotNumber() {
        return this.whatsappBotNumber;
    }

    public String getWhatsappMyNumber() {
        return this.whatsappMyNumber;
    }

    public Double getMinimumProfitMargin() {
        return this.minimumProfitMargin;
    }

    public Double getTransactionFees() {
        return this.transactionFees;
    }

    public Integer getMonitorIntervalPeriod() {
        return this.monitorIntervalPeriod;
    }

    public Double getVolumeGrowthThreshold() {
        return this.volumeGrowthThreshold;
    }

    public Double getSlippage() {
        return this.slippage;
    }

    public String getBinanceAPIKey() {
        return this.BinanceAPIKey;
    }

    public String getBinanceSecretKey() {
        return this.BinanceSecretKey;
    }

    public String getBinanceTargetSymbol() {
        return this.BinanceTargetSymbol;
    }

    public String getKrakenAPIKey() {
        return this.KrakenAPIKey;
    }

    public String getKrakenSecretKey() {
        return this.KrakenSecretKey;
    }

    public String getKrakenTargetSymbol() {
        return this.KrakenTargetSymbol;
    }

    public void setWhatsappAuthToken(String whatsappAuthToken) {
        this.whatsappAuthToken = whatsappAuthToken;
    }

    public void setWhatsappBotIdNumber(String whatsappBotIdNumber) {
        this.whatsappBotIdNumber = whatsappBotIdNumber;
    }

    public void setWhatsappBotNumber(String whatsappBotNumber) {
        this.whatsappBotNumber = whatsappBotNumber;
    }

    public void setWhatsappMyNumber(String whatsappMyNumber) {
        this.whatsappMyNumber = whatsappMyNumber;
    }

    public void setMinimumProfitMargin(Double minimumProfitMargin) {
        this.minimumProfitMargin = minimumProfitMargin;
    }

    public void setTransactionFees(Double transactionFees) {
        this.transactionFees = transactionFees;
    }

    public void setMonitorIntervalPeriod(Integer monitorIntervalPeriod) {
        this.monitorIntervalPeriod = monitorIntervalPeriod;
    }

    public void setVolumeGrowthThreshold(Double volumeGrowthThreshold) {
        this.volumeGrowthThreshold = volumeGrowthThreshold;
    }

    public void setSlippage(Double slippage) {
        this.slippage = slippage;
    }

    public void setBinanceAPIKey(String binanceAPIKey) {
        this.BinanceAPIKey = binanceAPIKey;
    }

    public void setBinanceSecretKey(String secretKey) {
        this.BinanceSecretKey = secretKey;
    }

    public void setBinanceTargetSymbol(String symbol) {
        this.BinanceTargetSymbol = symbol;
    }

    public void setKrakenAPIKey(String krakenAPIKey) {
        this.KrakenAPIKey = krakenAPIKey;
    }

    public void setKrakenSecretKey(String secretKey) {
        this.KrakenSecretKey = secretKey;
    }

    public void setKrakenTargetSymbol(String symbol) {
        this.KrakenTargetSymbol = symbol;
    }

}
