package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GlobalWarpIconAdapter implements DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, String displayName, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        GlobalWarp warp = IconManager.getInstance().getGlobalWarp(id);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            if(callback != null) callback.accept(TeleportResult.DESTINATION_DOES_NOT_EXIST);
            return false;
        }

        if(warp.getCategory() != null && warp.getCategory().hasPermission() && !player.hasPermission(warp.getCategory().getPermission())) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category"));
            if(callback != null) callback.accept(TeleportResult.NO_PERMISSION);
            return false;
        }

        if(warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"));
            if(callback != null) callback.accept(TeleportResult.NO_PERMISSION);
            return false;
        }

        TeleportManager man = null;
        man.teleport(player, Origin.GlobalWarpIcon, new Destination(warp.getAction(Action.SWITCH_SERVER).getValue(), DestinationType.GlobalWarp), displayName, costs, true, true, message != null, false, callback);
        return false;
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id) {
        GlobalWarp warp = IconManager.getInstance().getGlobalWarp(id);

        if(warp == null) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"), TeleportResult.DESTINATION_DOES_NOT_EXIST);
        }

        if(warp.getCategory() != null && warp.getCategory().hasPermission() && !player.hasPermission(warp.getCategory().getPermission())) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category"), TeleportResult.NO_PERMISSION);
        }

        if(warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"), TeleportResult.NO_PERMISSION);
        }

        return new SimulatedTeleportResult(null, TeleportResult.TELEPORTED);
    }

    @Override
    public double getCosts(String id) {
        GlobalWarp warp = IconManager.getInstance().getGlobalWarp(id);
        if(warp == null) {
            return 0;
        } else {
            return IconManager.getCosts(warp);
        }
    }

    @Override
    public Location buildLocation(String id) {
        return null;
    }
}
