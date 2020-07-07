package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.transfer.packets.general.PrepareCoordinationTeleportPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class GlobalLocationAdapter extends LocationAdapter implements Serializable {
    private String server;

    public GlobalLocationAdapter() {
    }

    public GlobalLocationAdapter(String server, Location location) {
        super(location);
        this.server = server;
    }

    @Override
    public GlobalLocationAdapter clone() {
        return new GlobalLocationAdapter(server, location.clone());
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        d.put("server", server);
        location = new Location();
        d.getSerializable("id", location);
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("server", server);
        d.put("id", location);
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<Result> callback) {
        if(location == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            if(callback != null) callback.accept(Result.DESTINATION_DOES_NOT_EXIST);
            return false;
        }

        if(server == null || server.equals(WarpSystem.getInstance().getCurrentServer())) {
            if(location.getWorld() == null) {
                player.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
                if(callback != null) callback.accept(Result.WORLD_DOES_NOT_EXIST);
                return false;
            } else {
                org.bukkit.Location finalLoc = prepare(player, location.clone());

                if(silent) TeleportListener.TELEPORTS.put(player, finalLoc);
                player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                if(callback != null) callback.accept(Result.SUCCESS);
                return true;
            }
        } else {
            PrepareCoordinationTeleportPacket packet = new PrepareCoordinationTeleportPacket(player.getName(), server, location.getWorldName(), displayName, message, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), costs, new Callback<Integer>() {
                @Override
                public void accept(Integer result) {
                    if(callback == null) return;
                    switch(result) {
                        case 0:
                            callback.accept(Result.SUCCESS);
                            break;
                        case 1:
                            callback.accept(Result.SERVER_NOT_AVAILABLE);
                            break;
                        case 2:
                            callback.accept(Result.WORLD_DOES_NOT_EXIST);
                            break;
                        default:
                            callback.accept(Result.CANCELLED);
                    }
                }
            });

            WarpSystem.getInstance().getDataHandler().send(packet);
            return true;
        }
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        Location location = buildLocation(id);

        if(location == null) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"), Result.DESTINATION_DOES_NOT_EXIST);
        }

        if(server == null || server.equals(WarpSystem.getInstance().getCurrentServer())) {
            if(location.getWorld() == null) {
                return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("World_Not_Exists"), Result.WORLD_DOES_NOT_EXIST);
            } else return new SimulatedTeleportResult(null, Result.SUCCESS);
        } else {
            return new SimulatedTeleportResult(null, Result.SUCCESS);
        }
    }

    @Override
    public double getCosts(String id) {
        return 0;
    }

    @Override
    public Location buildLocation(String id) {
        return this.location == null ? id == null ? null : de.codingair.codingapi.tools.Location.getByJSONString(id) : this.location;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
