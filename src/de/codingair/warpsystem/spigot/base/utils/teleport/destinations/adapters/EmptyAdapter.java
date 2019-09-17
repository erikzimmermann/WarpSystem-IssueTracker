package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EmptyAdapter implements DestinationAdapter {

    @Override
    public boolean teleport(Player player, String id, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        if(callback != null) callback.accept(TeleportResult.TELEPORTED);
        return false;
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        return new SimulatedTeleportResult(null, TeleportResult.TELEPORTED);
    }

    @Override
    public double getCosts(String id) {
        return 0;
    }

    @Override
    public Location buildLocation(String id) {
        return null;
    }
}
