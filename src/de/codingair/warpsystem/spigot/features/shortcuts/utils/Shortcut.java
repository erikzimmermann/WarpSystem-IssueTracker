package de.codingair.warpsystem.spigot.features.shortcuts.utils;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.ActionIcon;
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
            if(warp instanceof Warp) WarpSystem.getInstance().getTeleportManager().tryToTeleport(player, (Warp) warp);
            else if(warp instanceof GlobalWarp) WarpSystem.getInstance().getTeleportManager().tryToTeleport(player, (GlobalWarp) warp);
        } else if(globalWarp != null) {
            WarpSystem.getInstance().getTeleportManager().tryToTeleport(player, globalWarp, displayName, 0);
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
