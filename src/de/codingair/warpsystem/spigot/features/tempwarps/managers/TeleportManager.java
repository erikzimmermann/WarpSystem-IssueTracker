package de.codingair.warpsystem.spigot.features.tempwarps.managers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeleportManager {

    public boolean tryToTeleport(Player player, String id) {
        TempWarp warp = TempWarpManager.getManager().getWarp(id);

        if(warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            return false;
        }

        if(!warp.isAvailable()) {
            player.sendMessage(TempWarpManager.ERROR_NOT_AVAILABLE(warp.getIdentifier()));
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

        int costs = warp.getTeleportCosts();
        boolean isOwner = warp.isOwner(player);

        Callback<TeleportResult> callback = new Callback<TeleportResult>() {
            @Override
            public void accept(TeleportResult teleported) {
                if(teleported == TeleportResult.TELEPORTED) {
                    Player owner = warp.getOnlineOwner();
                    if(owner == null) warp.setInactiveSales(warp.getInactiveSales() + costs);
                    else AdapterType.getActive().setMoney(owner, AdapterType.getActive().getMoney(owner) + costs);
                }
            }
        };

        if(warp.getMessage() != null) {
            WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.TempWarp, new Destination(new LocationAdapter(warp.getLocation())), warp.getName(), isOwner ? 0 : warp.getTeleportCosts(), false, ChatColor.translateAlternateColorCodes('&', warp.getMessage()), false, isOwner ? null : callback);
        } else {
            WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.TempWarp, new Destination(new LocationAdapter(warp.getLocation())), warp.getName(), isOwner ? 0 : warp.getTeleportCosts(), false,
                    WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.TempWarps", true), false, isOwner ? null : callback);
        }
        return true;
    }

}
