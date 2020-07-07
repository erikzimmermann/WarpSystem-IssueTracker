package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.api.events.PlayerTeleportAcceptEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EmptyAdapter extends DestinationAdapter {

    @Override
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<Result> callback) {
        if(callback != null) callback.accept(Result.SUCCESS);
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> Bukkit.getPluginManager().callEvent(new PlayerTeleportAcceptEvent(player)), 1L);
        return true;
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        return new SimulatedTeleportResult(null, Result.SUCCESS);
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
