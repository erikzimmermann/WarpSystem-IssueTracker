package de.codingair.warpsystem.spigot.api.events.utils;

import org.bukkit.Location;

public class Warp {
    private Location location;
    private String id;
    private Type type;

    public Warp(Location location, String id, Type type) {
        this.location = location;
        this.id = id;
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        TempWarp,
        SimpleWarp,
        GUIWarp,
        ;
    }
}
