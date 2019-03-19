package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GlobalWarpAdapter implements DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        GlobalWarpManager.getInstance().teleport(player, displayName, id, costs, new Callback<PrepareTeleportPacket.Result>() {
            @Override
            public void accept(PrepareTeleportPacket.Result result) {
                switch(result) {
                    case TELEPORTED:
                        if(callback != null) callback.accept(TeleportResult.TELEPORTED);
                        break;

                    case WARP_NOT_EXISTS:
                        player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", id));

                        if(AdapterType.getActive() != null && costs != 0) {
                            AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) + costs);
                        }

                        if(callback != null) callback.accept(TeleportResult.DESTINATION_DOES_NOT_EXIST);
                        break;

                    case SERVER_NOT_AVAILABLE:
                        player.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));

                        if(AdapterType.getActive() != null && costs != 0) {
                            AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) + costs);
                        }

                        if(callback != null) callback.accept(TeleportResult.SERVER_NOT_AVAILABLE);
                        break;
                }
            }
        });

        return false;
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        return new SimulatedTeleportResult(null, TeleportResult.TELEPORTED);
    }

    @Override
    public double getCosts(String id) {
        return 0;
    }

    @Override
    public Location buildLocation(String id) {
        return null;
    }
}
