package br.com.victorbarberino.kaori.model;

public class SystemProperties {
    private String kaoriVersion;

    public SystemProperties() {

    }

    public void setKaoriVersion(String version) {
        this.kaoriVersion = version;
    }

    public String getKaoriVersion() {
        return this.kaoriVersion;
    }
}
