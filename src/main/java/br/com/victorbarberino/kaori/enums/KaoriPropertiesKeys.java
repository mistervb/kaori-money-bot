package br.com.victorbarberino.kaori.enums;

public enum KaoriPropertiesKeys implements PropertiesKey{
    KAORI_NOTIFY_WHATSAPP_AUTH_TOKEN("kaori.notify.whatsapp.auth-token"),
    KAORI_NOTIFY_WHATSAPP_NUMBER_ID("kaori.notify.whatsapp.number-id"),
    KAORI_NOTIFY_WHATSAPP_BOT_NUMBER("kaori.notify.whatsapp.bot-number"),
    KAORI_NOTIFY_WHATSAPP_MY_NUMBER("kaori.notify.whatsapp.my-number"),

    // ###########################
    // ##    Exchange Config    ##
    // ###########################
    KAORI_EXCHANGE_PROFIT_MINIMUM_MARGIN("kaori.exchange.profit.minimum-margin"),
    KAORI_EXCHANGE_TRANSACTION_FEES("kaori.exchange.transaction.fees"),

    // ##########################
    // ##    Monitor Config    ##
    // ##########################
    KAORI_MONITOR_INTERVAL_PERIOD("kaori.monitor.interval.period"),
    KAORI_MONITOR_VOLUME_GROWTH_THRESHOLD("kaori.monitor.volume.growth-threshold"),
    KAORI_MONITOR_SLIPPAGE("kaori.monitor.slippage.default"),

    // ##########################
    // ##     Binance Keys     ##
    // ##########################
    BINANCE_API_KEY("binance.api.key"),
    BINANCE_API_SECRET("binance.api.secret"),
    BINANCE_API_TARGET_SYMBOL("binance.api.target-symbol"),

    // ##########################
    // ##      Kraken Keys     ##
    // ##########################
    KRAKEN_API_KEY("kraken.api.key"),
    KRAKEN_API_SECRET("kraken.api.secret"),
    KRAKEN_API_TARGET_SYMBOL("kraken.api.target-symbol");

    private String key;

    KaoriPropertiesKeys(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
