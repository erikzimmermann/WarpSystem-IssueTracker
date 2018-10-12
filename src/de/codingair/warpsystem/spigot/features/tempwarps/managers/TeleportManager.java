package de.codingair.warpsystem.spigot.features.tempwarps.managers;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.entity.Player;

public class TeleportManager {

    public boolean tryToTeleport(Player player, String id) {
        TempWarp warp = TempWarpManager.getManager().getWarp(id);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            return false;
        }

        return tryToTeleport(player, warp);
    }
    
    public boolean tryToTeleport(Player player, TempWarp warp) {
        if(!warp.isPublic() && !warp.isOwner(player)) {
            //not reachable
            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_is_private"));
            return false;
        }

        if(warp.isExpired()) {
            //not active
            player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_is_expired"));
            return false;
        }

        WarpSystem.getInstance().getTeleportManager().teleport(player, warp.getLocation(), warp.getName());
        return true;
    }
    
}
