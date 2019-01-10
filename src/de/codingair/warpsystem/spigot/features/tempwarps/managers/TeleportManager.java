package de.codingair.warpsystem.spigot.features.tempwarps.managers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
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
        boolean isOwner;

        if(!(isOwner = warp.isOwner(player))) {
            if(AdapterType.getActive().getMoney(player) < costs) {
                //not enough money
                player.sendMessage(Lang.getPrefix() + Lang.get("Not_Enough_Money").replace("%AMOUNT%", warp.getTeleportCosts() + ""));
                return false;
            }

            AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) - costs);
        }

        WarpSystem.getInstance().getTeleportManager().teleport(player, warp.getLocation(), warp.getName(), warp.getTeleportCosts(), false, false, warp.getMessage(), false, isOwner ? null : new Callback<Boolean>() {
            @Override
            public void accept(Boolean teleported) {
                if(teleported) {
                    Player owner = warp.getOnlineOwner();
                    if(owner == null) warp.setInactiveSales(warp.getInactiveSales() + costs);
                    else AdapterType.getActive().setMoney(owner, AdapterType.getActive().getMoney(owner) + costs);
                } else {
                    AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) + costs);
                }
            }
        });
        return true;
    }

}
