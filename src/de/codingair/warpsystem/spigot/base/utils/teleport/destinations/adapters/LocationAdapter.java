package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class LocationAdapter implements DestinationAdapter, CloneableAdapter {
    protected Location location;

    public LocationAdapter() {
    }

    public LocationAdapter(Location location) {
        this.location = location;
    }

    public LocationAdapter(org.bukkit.Location location) {
        this.location = new Location(location);
    }

    @Override
    public LocationAdapter clone() {
        return new LocationAdapter(location.clone());
    }

    @Override
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<TeleportResult> callback) {
        Location location = buildLocation(id);

        if(location == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            if(callback != null) callback.accept(TeleportResult.DESTINATION_DOES_NOT_EXIST);
            return false;
        }

        if(location.getWorld() == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
            if(callback != null) callback.accept(TeleportResult.WORLD_DOES_NOT_EXIST);
            return false;
        } else {
            Location finalLoc = location.clone().add(randomOffset);
            if(silent) TeleportListener.TELEPORTS.put(player, finalLoc);
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            if(callback != null) callback.accept(TeleportResult.TELEPORTED);
            return true;
        }
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        Location location = buildLocation(id);

        if(location == null) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"), TeleportResult.DESTINATION_DOES_NOT_EXIST);
        }

        if(location.getWorld() == null) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("World_Not_Exists"), TeleportResult.WORLD_DOES_NOT_EXIST);
        } else return new SimulatedTeleportResult(null, TeleportResult.TELEPORTED);
    }

    @Override
    public double getCosts(String id) {
        return 0;
    }

    @Override
    public Location buildLocation(String id) {
        return this.location == null ? id == null ? null : de.codingair.codingapi.tools.Location.getByJSONString(id) : this.location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
