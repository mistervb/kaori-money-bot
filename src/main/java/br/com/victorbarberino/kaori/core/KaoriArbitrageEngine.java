package br.com.victorbarberino.kaori.core;

import br.com.victorbarberino.kaori.core.arbitrage.ArbitrageExecutor;
import br.com.victorbarberino.kaori.model.PropertiesData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KaoriArbitrageEngine {
    private ArbitrageExecutor arbitrageExecutor;
    private ExecutorService executorService;

    public KaoriArbitrageEngine(PropertiesData pd) {
        this.arbitrageExecutor = new ArbitrageExecutor(pd);
        this.executorService = Executors.newFixedThreadPool(8);
    }

    public Future<Boolean> execute(double buyPrice, double sellPrice, double amount) {
        return executorService.submit(() -> {
            try {
                arbitrageExecutor.executeArbitrage(buyPrice, sellPrice, amount);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
