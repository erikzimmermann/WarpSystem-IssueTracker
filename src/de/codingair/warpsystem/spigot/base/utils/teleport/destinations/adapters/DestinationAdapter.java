package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface DestinationAdapter {
    boolean teleport(Player player, String id, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback);

    SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission);

    double getCosts(String id);

    Location buildLocation(String id);
}
