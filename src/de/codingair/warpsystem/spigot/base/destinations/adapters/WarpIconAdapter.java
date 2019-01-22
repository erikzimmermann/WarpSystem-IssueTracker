package de.codingair.warpsystem.spigot.base.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.SpigotAPI;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WarpIconAdapter implements DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, String displayName, String message, boolean silent, double costs, Callback<Boolean> callback) {
        Warp warp = IconManager.getInstance().getWarp(id);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            if(callback != null) callback.accept(false);
            return false;
        }

        if(warp.getLocation().getWorld() == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
            if(callback != null) callback.accept(false);
            return false;
        } else {
            if(warp.getCategory() != null && warp.getCategory().hasPermission() && !player.hasPermission(warp.getCategory().getPermission())) {
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category"));
                if(callback != null) callback.accept(false);
                return false;
            }

            if(warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"));
                if(callback != null) callback.accept(false);
                return false;
            }

            //Already paid in TeleportManager
            warp.perform(player, false, Action.PAY_MONEY, Action.RUN_COMMAND, Action.TELEPORT_TO_WARP);

            if(silent) SpigotAPI.getInstance().silentTeleport(player, warp.getLocation());
            else player.teleport(warp.getLocation());

            if(message != null)
                player.sendMessage((message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName)));

            if(callback != null) callback.accept(true);
            return true;
        }
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
