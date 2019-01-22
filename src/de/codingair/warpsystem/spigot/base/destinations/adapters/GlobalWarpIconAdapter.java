package de.codingair.warpsystem.spigot.base.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.destinations.Destination;
import de.codingair.warpsystem.spigot.base.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.entity.Player;

public class GlobalWarpIconAdapter implements DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, String displayName, String message, boolean silent, double costs, Callback<Boolean> callback) {
        GlobalWarp warp = IconManager.getInstance().getGlobalWarp(id);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            return false;
        }

        if(warp.getCategory() != null && warp.getCategory().hasPermission() && !player.hasPermission(warp.getCategory().getPermission())) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category"));
            return false;
        }

        if(warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"));
            return false;
        }

        TeleportManager man = null;
        man.teleport(player, new Destination(warp.getAction(Action.SWITCH_SERVER).getValue(), DestinationType.GlobalWarp), displayName, costs, true, true, message != null, false, callback);
        return false;
    }

    @Override
    public String simulate(Player player, String id) {
        Warp warp = IconManager.getInstance().getWarp(id);

        if(warp == null) {
            return Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS");
        }

        if(warp.getLocation().getWorld() == null) {
            return Lang.getPrefix() + Lang.get("World_Not_Exists");
        } else {
            if(warp.getCategory() != null && warp.getCategory().hasPermission() && !player.hasPermission(warp.getCategory().getPermission())) {
                return Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category");
            }

            if(warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
                return Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp");
            }

            return null;
        }
    }
}
