package br.com.victorbarberino.kaori.core;

import br.com.victorbarberino.kaori.api.BinanceAPI;
import br.com.victorbarberino.kaori.api.KrakenCustomAPI;
import br.com.victorbarberino.kaori.config.KaoriSystem;
import br.com.victorbarberino.kaori.log.KaoriLog;
import br.com.victorbarberino.kaori.model.PropertiesData;
import br.com.victorbarberino.kaori.model.SystemProperties;
import br.com.victorbarberino.kaori.monitor.GeneralMonitor;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KaoriBootstrap {
    private static final Logger log = LoggerFactory.getLogger(KaoriBootstrap.class);

    private static PropertiesData pd;
    private static SystemProperties sysPd;
    private static GeneralMonitor generalMonitor;

    public static void init() {
        initializeSystemInstances();
        KaoriLog.logBanner();

        initializeAPIs();
        initializeMonitors();

        // Adiciona um loop principal para manter a aplicação em execução
        keepApplicationRunning();
    }

    private static void initializeSystemInstances() {
        KaoriSystem system = KaoriSystem.getInstance();
        system.cleanAllInstances();

        pd = system.getPropertiesDataInstance();
        sysPd = system.getSystemPropertiesInstance();
    }

    private static void initializeAPIs() {
        initializeBinanceAPI();
        initializeKrakenAPI();
    }

    private static void initializeMonitors() {
        try{
            generalMonitor = new GeneralMonitor(pd);
            generalMonitor.startMonitors();
        } catch (SchedulerException e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private static void initializeBinanceAPI() {
        BinanceAPI binanceAPI = new BinanceAPI(pd);
        log.info("[BINANCE] Starting connection to API...");

        System.out.println("***********************************");
        System.out.println("**         BINANCE LOGS          **");
        System.out.println("***********************************");

        binanceAPI.logPing();
        //m binanceAPI.logAccountBalance();
        binanceAPI.logUserInfo();
        binanceAPI.logMakertInfo();

        log.info("[BINANCE] Connection established!");
    }

    private static void initializeKrakenAPI() {
        KrakenCustomAPI krakenAPI = new KrakenCustomAPI(pd);
        log.info("[KRAKEN] Starting connection to API...");

        System.out.println("**********************************");
        System.out.println("**         KRAKEN LOGS          **");
        System.out.println("**********************************");

        krakenAPI.logPing();
        //krakenAPI.logAccountBalance();
        krakenAPI.logUserInfo();
        krakenAPI.logMarketInfo();
        krakenAPI.getTransactionFees(pd.getKrakenTargetSymbol());

        log.info("[KRAKEN] Connection established!");
    }

    private static void keepApplicationRunning() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down gracefully...");
            // Encerrar monitores e APIs aqui se necessário
            generalMonitor.stopMonitors();
        }));

        try {
            // Mantém a aplicação em execução
            log.info("Kaori is running. Press Ctrl+C to exit.");
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.error("Application interrupted: ", e);
            Thread.currentThread().interrupt();
        }
    }
}
