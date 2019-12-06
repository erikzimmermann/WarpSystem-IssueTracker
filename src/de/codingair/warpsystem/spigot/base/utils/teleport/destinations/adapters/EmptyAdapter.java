package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.events.PlayerTeleportAcceptEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EmptyAdapter implements DestinationAdapter {

    @Override
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        if(callback != null) callback.accept(TeleportResult.TELEPORTED);
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> Bukkit.getPluginManager().callEvent(new PlayerTeleportAcceptEvent(player)), 1L);
        return true;
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
