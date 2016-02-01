package com.subgraph.orchid.worldproxy;

public class InitTorStatus {

    private int port;
    private String iso;

    public InitTorStatus(int port, String iso) {
        this.port = port;
        this.iso = iso;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }
}
