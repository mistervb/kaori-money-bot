package br.com.victorbarberino.kaori.monitor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public interface Monitor {
    CompletableFuture<Void> startMonitoringAsync(ScheduledExecutorService scheduler);
    void stopMonitoring(ScheduledExecutorService scheduler);
    void monitor();
    String getName();
    String getMonitorId();
}
