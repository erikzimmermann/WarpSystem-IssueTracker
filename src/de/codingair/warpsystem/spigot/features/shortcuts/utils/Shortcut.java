package de.codingair.warpsystem.spigot.features.shortcuts.utils;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.destinations.Destination;
import de.codingair.warpsystem.spigot.base.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.ActionIcon;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.entity.Player;

public class Shortcut {
    private ActionIcon warp;
    private String globalWarp;
    private String displayName;

    public Shortcut(Warp warp, String displayName) {
        this.warp = warp;
        this.displayName = displayName;
    }

    public Shortcut(GlobalWarp warp, String displayName) {
        this.warp = warp;
        this.displayName = displayName;
    }

    public Shortcut(String globalWarp, String displayName) {
        this.globalWarp = globalWarp;
        this.displayName = displayName;
    }

    public void run(Player player) {
        if(this.warp != null) {
            if(warp instanceof Warp) {
                if(!player.hasPermission(WarpSystem.PERMISSION_USE_Warps)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                }

                WarpSystem.getInstance().getTeleportManager().teleport(player, new Destination(((Warp) warp).getIdentifier(), DestinationType.WarpIcon), warp.getName(), IconManager.getCosts(warp),
                        WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.Warps", true));
            } else if(warp instanceof GlobalWarp) {
                if(!player.hasPermission(WarpSystem.PERMISSION_USE_GlobalWarps)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                }

                WarpSystem.getInstance().getTeleportManager().teleport(player, new Destination(warp.getName(), DestinationType.GlobalWarpIcon), warp.getName(), IconManager.getCosts(warp),
                        WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.GlobalWarps", true));
            }
        } else if(globalWarp != null) {
            if(!player.hasPermission(WarpSystem.PERMISSION_USE_GlobalWarps)) {
                player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                return;
            }

            WarpSystem.getInstance().getTeleportManager().teleport(player, new Destination(globalWarp, DestinationType.GlobalWarp), displayName, 0,
                    WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.GlobalWarps", true));
        }
    }

    public boolean isActive() {
        if(this.warp instanceof GlobalWarp || this.globalWarp != null) {
            return WarpSystem.getInstance().isOnBungeeCord();
        }

        return true;
    }

    public ActionIcon getWarp() {
        return warp;
    }

    public String getGlobalWarp() {
        return globalWarp;
    }

    public String getDisplayName() {
        return displayName;
    }
}
