package br.com.victorbarberino.kaori.monitor;

import br.com.victorbarberino.kaori.model.PropertiesData;
import br.com.victorbarberino.kaori.monitor.volume_monitor.VolumeMonitor;
import br.com.victorbarberino.kaori.monitor.volume_monitor.VolumeMonitorJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GeneralMonitor {
    private static final Logger log = LoggerFactory.getLogger(GeneralMonitor.class.getName());
    private final Scheduler scheduler;
    private final List<Monitor> monitors;
    private static PropertiesData pd;

    public GeneralMonitor(PropertiesData pd) throws SchedulerException {
        GeneralMonitor.pd = pd;
        this.monitors = getMonitors();
        this.scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
    }

    public void startMonitors() {
        log.info("[GENERAL MONITOR] Initializing system monitoring...");
        logAllSystemMonitors();
        for (Monitor monitor : monitors) {
            MonitorManager.registerMonitor(monitor.getName(), monitor); // Registra o monitor
            try {
                scheduleMonitor(monitor, pd.getMonitorIntervalPeriod());
            } catch (SchedulerException e) {
                log.error("[GENERAL MONITOR] Error scheduling monitor: {}", monitor.getName(), e);
            }
        }
    }

    public void stopMonitors() {
        try {
            scheduler.shutdown();
            log.info("[GENERAL MONITOR] Scheduler shut down.");
        } catch (SchedulerException e) {
            log.error("[GENERAL MONITOR] Error shutting down the scheduler.", e);
        }
    }

    private void scheduleMonitor(Monitor monitor, int intervalInMinutes) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(VolumeMonitorJob.class)
                .withIdentity(monitor.getName(), "monitorGroup")
                .usingJobData("monitorName", monitor.getName()) // Passa o nome do monitor
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(monitor.getName() + "Trigger", "monitorGroup")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(intervalInMinutes)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
        log.info("[GENERAL MONITOR] {} agendado com intervalo de {} minutos.", monitor.getName(), intervalInMinutes);
    }

    private static List<Monitor> getMonitors() {
        return List.of(
                new VolumeMonitor(pd)
        );
    }

    private static void logAllSystemMonitors() {
        log.info("[GENERAL MONITOR] System monitoring:");
        for (Monitor monitor : getMonitors()) {
            log.info("- " + monitor.getName());
        }
    }
}
