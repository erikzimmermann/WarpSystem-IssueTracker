package de.codingair.warpsystem.spigot.api.events.utils;

public class GlobalWarp {
    private String name;
    private String server;

    public GlobalWarp(String name, String server) {
        this.name = name;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }
}
