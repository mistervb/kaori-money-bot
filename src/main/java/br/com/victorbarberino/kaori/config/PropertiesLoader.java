package br.com.victorbarberino.kaori.config;

import br.com.victorbarberino.kaori.enums.KaoriPropertiesKeys;
import br.com.victorbarberino.kaori.enums.KaoriSystemPropertiesKeys;
import br.com.victorbarberino.kaori.enums.PropertiesKey;
import br.com.victorbarberino.kaori.model.PropertiesData;
import br.com.victorbarberino.kaori.model.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class PropertiesLoader {
    private static final String PROPERTIES_FILE_NAME = "kaori.properties";
    private static final String SYSTEM_PROPERTIES_FILE_NAME = "kaori-system.properties";

    private static Properties properties;
    private static Logger log = LoggerFactory.getLogger(PropertiesLoader.class);

    private static SystemProperties sysPd;

    protected static PropertiesData loadPropertiesData() {
        PropertiesData pd = new PropertiesData();
        try {
            loadPropertiesObject(PROPERTIES_FILE_NAME);
            pd.setWhatsappAuthToken(getPropValue(KaoriPropertiesKeys.KAORI_NOTIFY_WHATSAPP_AUTH_TOKEN));
            pd.setWhatsappBotIdNumber(getPropValue(KaoriPropertiesKeys.KAORI_NOTIFY_WHATSAPP_NUMBER_ID));
            pd.setWhatsappBotNumber(getPropValue(KaoriPropertiesKeys.KAORI_NOTIFY_WHATSAPP_BOT_NUMBER));
            pd.setWhatsappMyNumber(getPropValue(KaoriPropertiesKeys.KAORI_NOTIFY_WHATSAPP_MY_NUMBER));
            pd.setMinimumProfitMargin(Double.valueOf(getPropValue(KaoriPropertiesKeys.KAORI_EXCHANGE_PROFIT_MINIMUM_MARGIN)));
            pd.setTransactionFees(Double.valueOf(getPropValue(KaoriPropertiesKeys.KAORI_EXCHANGE_TRANSACTION_FEES)));
            pd.setMonitorIntervalPeriod(Integer.valueOf(getPropValue(KaoriPropertiesKeys.KAORI_MONITOR_INTERVAL_PERIOD)));
            pd.setVolumeGrowthThreshold(Double.valueOf(getPropValue(KaoriPropertiesKeys.KAORI_MONITOR_VOLUME_GROWTH_THRESHOLD)));
            pd.setSlippage(Double.valueOf(getPropValue(KaoriPropertiesKeys.KAORI_MONITOR_SLIPPAGE)));
            pd.setBinanceAPIKey(getPropValue(KaoriPropertiesKeys.BINANCE_API_KEY));
            pd.setBinanceSecretKey(getPropValue(KaoriPropertiesKeys.BINANCE_API_SECRET));
            pd.setBinanceTargetSymbol(getPropValue(KaoriPropertiesKeys.BINANCE_API_TARGET_SYMBOL));
            pd.setKrakenAPIKey(getPropValue(KaoriPropertiesKeys.KRAKEN_API_KEY));
            pd.setKrakenSecretKey(getPropValue(KaoriPropertiesKeys.KRAKEN_API_SECRET));
            pd.setKrakenTargetSymbol(getPropValue(KaoriPropertiesKeys.KRAKEN_API_TARGET_SYMBOL));
        } catch (IOException e) {
            log.error("Error loading properties file [kaori.properties]: {}", e.getMessage());
        }
        return pd;
    }

    protected static SystemProperties loadSystemPropertes() {
        SystemProperties sysPd = new SystemProperties();
        try {
            loadPropertiesObject(SYSTEM_PROPERTIES_FILE_NAME);
            sysPd.setKaoriVersion(getPropValue(KaoriSystemPropertiesKeys.KAORI_VERSION));
        } catch(IOException e) {
            log.error("Error loading properties file [kaori-system.properties]: {}", e.getMessage());
        }
        return sysPd;
    }

    private static String getPropValue(PropertiesKey key) throws IOException {
        return properties.getProperty(key.getKey());
    }

    private static void loadPropertiesObject(String fileName) throws IOException {
        Properties propertiesBase = new Properties();
        FileInputStream file = new FileInputStream(
                System.getProperty("user.dir") + File.separator +
                     "properties" + File.separator + fileName
        );
        propertiesBase.load(file);
        properties = propertiesBase;
    }

}
