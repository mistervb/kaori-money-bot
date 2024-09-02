package br.com.victorbarberino.kaori.config;

import br.com.victorbarberino.kaori.model.PropertiesData;
import br.com.victorbarberino.kaori.model.SystemProperties;

public class KaoriSystem {
    private static KaoriSystem instance;
    private static PropertiesData pd;
    private static SystemProperties sysPd;

    private KaoriSystem() {

    }

    public static KaoriSystem getInstance() {
        if(instance == null)
           return new KaoriSystem();
        return instance;
    }

    public PropertiesData getPropertiesDataInstance() {
        if(pd == null)
            return PropertiesLoader.loadPropertiesData();
        return pd;
    }

    public SystemProperties getSystemPropertiesInstance() {
        if(sysPd == null)
            return PropertiesLoader.loadSystemPropertes();
        return sysPd;
    }

    public void cleanAllInstances() {
        pd = null;
        sysPd = null;
    }
}
