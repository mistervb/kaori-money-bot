package br.com.victorbarberino.kaori.monitor;

import java.util.HashMap;
import java.util.Map;

public class MonitorManager {
    private static Map<String, Monitor> monitors = new HashMap<>();

    public static void registerMonitor(String name, Monitor monitor) {
        monitors.put(name, monitor);
    }

    public static Monitor getMonitor(String name) {
        return monitors.get(name);
    }
}
