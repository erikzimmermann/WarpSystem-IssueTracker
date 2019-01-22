package de.codingair.warpsystem.spigot.base.destinations;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.destinations.adapters.DestinationAdapter;
import org.bukkit.entity.Player;

public class Destination {
    private String id;
    private DestinationType type;
    private DestinationAdapter adapter;

    public Destination(String id, DestinationType type) {
        this.id = id;
        this.type = type;
        this.adapter = type.getInstance();
    }

    public Destination(DestinationAdapter adapter) {
        this.id = null;
        this.type = DestinationType.UNKNOWN;
        this.adapter = adapter;
    }

    public boolean teleport(Player player, String message, String displayName, boolean silent, double costs, Callback<Boolean> callback) {
        player.setFallDistance(0F);
        return adapter.teleport(player, id, displayName, message, silent, costs, callback);
    }

    public String simulate(Player player) {
        return adapter.simulate(player, this.id);
    }

    public String getId() {
        return id;
    }

    public DestinationType getType() {
        return type;
    }
}
