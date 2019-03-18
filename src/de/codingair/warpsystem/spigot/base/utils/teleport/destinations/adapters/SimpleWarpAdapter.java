package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.SpigotAPI;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.utils.actions.Action;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SimpleWarpAdapter implements DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(id);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            if(callback != null) callback.accept(TeleportResult.DESTINATION_DOES_NOT_EXIST);
            return false;
        }

        if(warp.getLocation().getWorld() == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
            if(callback != null) callback.accept(TeleportResult.WORLD_DOES_NOT_EXIST);
            return false;
        } else {
            if(checkPermission && warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"));
                if(callback != null) callback.accept(TeleportResult.NO_PERMISSION);
                return false;
            }

            for(Action action : warp.getActionList()) {
                action.onRun(player);
            }

            if(silent) SpigotAPI.getInstance().silentTeleport(player, warp.getLocation());
            else player.teleport(warp.getLocation());

            if(message != null)
                player.sendMessage((message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName)));

            if(callback != null) callback.accept(TeleportResult.TELEPORTED);
            return true;
        }
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(id);

        if(warp == null) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"), TeleportResult.DESTINATION_DOES_NOT_EXIST);
        }

        if(warp.getLocation().getWorld() == null) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("World_Not_Exists"), TeleportResult.WORLD_DOES_NOT_EXIST);
        } else {
            if(checkPermission && warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
                return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"), TeleportResult.NO_PERMISSION);
            }

            return new SimulatedTeleportResult(null, TeleportResult.TELEPORTED);
        }
    }

    @Override
    public double getCosts(String id) {
        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(id);
        if(warp == null) return 0;
        else return warp.getCosts();
    }

    @Override
    public Location buildLocation(String id) {
        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(id);
        return warp == null ? null : warp.getLocation().clone();
    }
}
