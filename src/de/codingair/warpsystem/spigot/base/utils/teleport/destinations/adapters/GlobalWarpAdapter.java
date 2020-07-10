package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationAdapter;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.spigot.GlobalWarpTeleportPacket;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GlobalWarpAdapter extends DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<Result> callback) {
        GlobalWarpManager.getInstance().teleport(player, id, randomOffset, displayName, message, costs, new Callback<GlobalWarpTeleportPacket.Result>() {
            @Override
            public void accept(GlobalWarpTeleportPacket.Result result) {
                switch(result) {
                    case TELEPORTED:
                        if(callback != null) callback.accept(Result.SUCCESS);
                        break;

                    case WARP_NOT_EXISTS:
                        player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", id));

                        if(Bank.adapter() != null && costs != 0) {
                            Bank.adapter().deposit(player, costs);
                        }

                        if(callback != null) callback.accept(Result.DESTINATION_DOES_NOT_EXIST);
                        break;

                    case SERVER_NOT_AVAILABLE:
                        if(Bank.adapter() != null && costs != 0) {
                            Bank.adapter().deposit(player, costs);
                        }

                        if(callback != null) callback.accept(Result.SERVER_NOT_AVAILABLE);
                        break;
                }
            }
        });

        return false;
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        return new SimulatedTeleportResult(null, Result.SUCCESS);
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
