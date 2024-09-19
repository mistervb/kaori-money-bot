package br.com.victorbarberino.kaori.enums;

public enum KaoriSystemPropertiesKeys implements PropertiesKey {
    KAORI_VERSION("kaori.version");
    private String key;

    KaoriSystemPropertiesKeys(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
