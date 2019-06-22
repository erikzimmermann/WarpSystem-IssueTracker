package de.codingair.warpsystem.spigot.api.events.utils;

import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import org.bukkit.Location;

public class Warp {
    private Location location;
    private String id;
    private DestinationType type;

    public Warp(Location location, String id, DestinationType type) {
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

    public DestinationType getType() {
        return type;
    }
}
