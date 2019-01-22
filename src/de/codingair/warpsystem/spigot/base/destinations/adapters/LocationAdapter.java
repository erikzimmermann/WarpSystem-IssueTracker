package de.codingair.warpsystem.spigot.base.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.SpigotAPI;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationAdapter implements DestinationAdapter {
    private Location location;

    public LocationAdapter(Location location) {
        this.location = location;
    }

    @Override
    public boolean teleport(Player player, String id, String displayName, String message, boolean silent, double costs, Callback<Boolean> callback) {
        if(this.location == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            if(callback != null) callback.accept(false);
            return false;
        }

        if(this.location.getWorld() == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
            if(callback != null) callback.accept(false);
            return false;
        } else {
            if(silent) SpigotAPI.getInstance().silentTeleport(player, location);
            else player.teleport(location);

            if(message != null)
                player.sendMessage((message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName)));

            if(callback != null) callback.accept(true);
            return true;
        }
    }

    @Override
    public String simulate(Player player, String id) {
        if(this.location == null) {
            return Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS");
        }

        if(this.location.getWorld() == null) {
            return Lang.getPrefix() + Lang.get("World_Not_Exists");
        } else return null;
    }

    public Location getLocation() {
        return location;
    }
}
