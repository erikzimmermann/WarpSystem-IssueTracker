package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareGlobalWarpTeleportPacket;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GlobalWarpAdapter implements DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        GlobalWarpManager.getInstance().teleport(player, displayName, id, costs, new Callback<PrepareGlobalWarpTeleportPacket.Result>() {
            @Override
            public void accept(PrepareGlobalWarpTeleportPacket.Result result) {
                switch(result) {
                    case TELEPORTED:
                        if(callback != null) callback.accept(TeleportResult.TELEPORTED);
                        break;

                    case WARP_NOT_EXISTS:
                        player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", id));

                        if(MoneyAdapterType.getActive() != null && costs != 0) {
                            MoneyAdapterType.getActive().deposit(player, costs);
                        }

                        if(callback != null) callback.accept(TeleportResult.DESTINATION_DOES_NOT_EXIST);
                        break;

                    case SERVER_NOT_AVAILABLE:
                        player.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));

                        if(MoneyAdapterType.getActive() != null && costs != 0) {
                            MoneyAdapterType.getActive().deposit(player, costs);
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
