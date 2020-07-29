package de.codingair.warpsystem.api;

import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import org.bukkit.Location;

public class TeleportOptions extends de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions {
    public TeleportOptions(Location location, String displayName) {
        super(location, displayName);
    }

    public TeleportOptions(Destination destination, String displayName) {
        super(destination, displayName);
    }
}
