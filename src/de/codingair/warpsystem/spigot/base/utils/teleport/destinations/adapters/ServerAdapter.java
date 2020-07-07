package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationAdapter;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareServerSwitchPacket;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ServerAdapter extends DestinationAdapter {
    @Override
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<Result> callback) {
        if(!WarpSystem.getInstance().isOnBungeeCord()) {
            if(callback != null) callback.accept(Result.NOT_ON_BUNGEE_CORD);
            return false;
        }

        WarpSystem.getInstance().getDataHandler().send(new PrepareServerSwitchPacket(player.getName(), id, message, new Callback<Integer>() {
            @Override
            public void accept(Integer result) {
                if(callback != null) {
                    if(result == 0) callback.accept(Result.SUCCESS);
                    else if(result == 1) callback.accept(Result.SERVER_NOT_AVAILABLE);
                    else if(result == 2) callback.accept(Result.ALREADY_ON_TARGET_SERVER);
                    else if(result == 3) callback.accept(Result.SERVER_NOT_AVAILABLE);
                    else if(result == 4) callback.accept(Result.ERROR);
                }

                if(result == 2)
                    player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_On_Target_Server"));
            }
        }));

        return false;
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        if(!WarpSystem.getInstance().isOnBungeeCord())
            return new SimulatedTeleportResult(null, Result.NOT_ON_BUNGEE_CORD);

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
