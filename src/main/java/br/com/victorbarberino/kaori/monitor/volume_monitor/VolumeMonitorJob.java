package br.com.victorbarberino.kaori.monitor.volume_monitor;

import br.com.victorbarberino.kaori.monitor.Monitor;
import br.com.victorbarberino.kaori.monitor.MonitorManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VolumeMonitorJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(VolumeMonitorJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String monitorName = context.getJobDetail().getJobDataMap().getString("monitorName");
        Monitor monitor = MonitorManager.getMonitor(monitorName);

        if (monitor == null) {
            throw new JobExecutionException("Monitor not found.");
        }

        System.out.print("\n");
        System.out.println("************************************");
        System.out.println("**         VOLUME MONITOR         **");
        System.out.println("************************************");

        log.info("[VOLUME MONITOR JOB] Executing monitor: {}", monitor.getName());
        monitor.monitor();
    }
}
