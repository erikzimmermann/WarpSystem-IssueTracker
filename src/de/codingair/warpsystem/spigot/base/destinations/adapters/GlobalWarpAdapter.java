package de.codingair.warpsystem.spigot.base.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import org.bukkit.entity.Player;

public class GlobalWarpAdapter implements DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, String displayName, String message, boolean silent, double costs, Callback<Boolean> callback) {
        GlobalWarpManager.getInstance().teleport(player, displayName, id, costs, new Callback<PrepareTeleportPacket.Result>() {
            @Override
            public void accept(PrepareTeleportPacket.Result result) {
                switch(result) {
                    case TELEPORTED:
                        if(callback != null) callback.accept(true);
                        break;

                    case WARP_NOT_EXISTS:
                        player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", id));
                        break;

                    case SERVER_NOT_AVAILABLE:
                        player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Server_Is_Not_Online"));
                        break;

                    case PLAYER_ALREADY_ON_SERVER:
                        player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Player_Is_Already_On_Target_Server"));
                        break;
                }

                if(result != PrepareTeleportPacket.Result.TELEPORTED) {
                    if(AdapterType.getActive() != null && costs != 0) {
                        AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) + costs);
                    }

                    if(callback != null) callback.accept(true);
                }
            }
        });

        return false;
    }

    @Override
    public String simulate(Player player, String id) {
        return null;
    }
}
