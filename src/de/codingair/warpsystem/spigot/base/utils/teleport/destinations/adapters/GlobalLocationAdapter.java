package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.transfer.packets.general.PrepareCoordinationTeleportPacket;
import org.bukkit.entity.Player;
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
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        if(location == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            if(callback != null) callback.accept(TeleportResult.DESTINATION_DOES_NOT_EXIST);
            return false;
        }

        if(server == null || server.equals(WarpSystem.getInstance().getCurrentServer())) {
            if(location.getWorld() == null) {
                player.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
                if(callback != null) callback.accept(TeleportResult.WORLD_DOES_NOT_EXIST);
                return false;
            } else {
                Location finalLoc = location.clone().add(randomOffset);
                if(silent) TeleportListener.TELEPORTS.put(player, finalLoc);
                player.teleport(finalLoc);

                if(callback != null) callback.accept(TeleportResult.TELEPORTED);
                return true;
            }
        } else {
            PrepareCoordinationTeleportPacket packet = new PrepareCoordinationTeleportPacket(player.getName(), server, location.getWorldName(), displayName, message, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), costs, new Callback<Integer>() {
                @Override
                public void accept(Integer result) {
                    if(callback == null) return;
                    switch(result) {
                        case 0:
                            callback.accept(TeleportResult.TELEPORTED);
                            break;
                        case 1:
                            callback.accept(TeleportResult.SERVER_NOT_AVAILABLE);
                            break;
                        case 2:
                            callback.accept(TeleportResult.WORLD_DOES_NOT_EXIST);
                            break;
                        default:
                            callback.accept(TeleportResult.CANCELLED);
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
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"), TeleportResult.DESTINATION_DOES_NOT_EXIST);
        }

        if(server == null || server.equals(WarpSystem.getInstance().getCurrentServer())) {
            if(location.getWorld() == null) {
                return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("World_Not_Exists"), TeleportResult.WORLD_DOES_NOT_EXIST);
            } else return new SimulatedTeleportResult(null, TeleportResult.TELEPORTED);
        } else {
            return new SimulatedTeleportResult(null, TeleportResult.TELEPORTED);
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
