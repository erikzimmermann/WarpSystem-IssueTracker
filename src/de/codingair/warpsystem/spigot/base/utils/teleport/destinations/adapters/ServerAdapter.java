package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareServerSwitchPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ServerAdapter implements DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        if(!WarpSystem.getInstance().isOnBungeeCord()) {
            if(callback != null) callback.accept(TeleportResult.NOT_ON_BUNGEE_CORD);
            return false;
        }

        WarpSystem.getInstance().getDataHandler().send(new PrepareServerSwitchPacket(player.getName(), id, message, new Callback<Integer>() {
            @Override
            public void accept(Integer result) {
                if(callback != null) {
                    if(result == 0) callback.accept(TeleportResult.TELEPORTED);
                    else if(result == 1) callback.accept(TeleportResult.SERVER_NOT_AVAILABLE);
                    else if(result == 2) callback.accept(TeleportResult.ALREADY_ON_TARGET_SERVER);
                    else if(result == 3) callback.accept(TeleportResult.SERVER_NOT_AVAILABLE);
                    else if(result == 4) callback.accept(TeleportResult.ERROR);
                }

                if(result == 2)
                    player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_On_Target_Server"));
                else if(result == 1 || result == 3)
                    player.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
            }
        }));

        return false;
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        if(!WarpSystem.getInstance().isOnBungeeCord())
            return new SimulatedTeleportResult(null, TeleportResult.NOT_ON_BUNGEE_CORD);

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
