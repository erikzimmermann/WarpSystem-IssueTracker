package de.codingair.warpsystem.spigot.features.shortcuts.utils;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.ActionIcon;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.entity.Player;

public class Shortcut {
    private Destination destination;
    private String displayName;

    public Shortcut(Destination destination, String displayName) {
        this.destination = destination;
        this.displayName = displayName;
    }

    public void run(Player player) {
        if(this.destination != null) {
            if(destination.getType() == DestinationType.SimpleWarp) {
                if(!player.hasPermission(WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                }

                WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.ShortCut, destination, destination.getId(), 0,
                        WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.Warps", true));
            } else if(destination.getType() == DestinationType.GlobalWarp) {
                if(!player.hasPermission(WarpSystem.PERMISSION_USE_GLOBAL_WARPS)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                }

                WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.ShortCut, destination, destination.getId(), 0,
                        WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.GlobalWarps", true));
            }
        }
    }

    public boolean isActive() {
        if(this.destination == null || this.destination.getAdapter() == null) return false;

        if(this.destination.getType() == DestinationType.GlobalWarp) {
            return WarpSystem.getInstance().isOnBungeeCord();
        }

        return true;
    }

    public Destination getDestination() {
        return destination;
    }

    public String getDisplayName() {
        return displayName;
    }
}
