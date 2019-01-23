package de.codingair.warpsystem.spigot.base.utils.teleport.destinations;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.DestinationAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

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

    public Destination(String data) {
        try {
            JSONArray json = (JSONArray) new JSONParser().parse(data);

            this.type = DestinationType.valueOf((String) json.get(0));
            this.id = (String) json.get(1);
            this.adapter = type.getInstance();
        } catch(Exception ex) {
            throw new IllegalArgumentException("Wrong serialized data!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return id.equals(that.id) &&
                type == that.type;
    }

    public boolean teleport(Player player, String message, String displayName, boolean silent, double costs, Callback<TeleportResult> callback) {
        player.setFallDistance(0F);
        return adapter.teleport(player, id, displayName, message, silent, costs, callback);
    }

    public Location buildLocation() {
        return adapter.buildLocation(id);
    }

    public double getCosts() {
        return adapter.getCosts(id);
    }

    public SimulatedTeleportResult simulate(Player player) {
        return adapter.simulate(player, this.id);
    }

    public String getId() {
        return id;
    }

    public DestinationType getType() {
        return type;
    }

    public String toJSONString() {
        if(this.type == DestinationType.UNKNOWN) throw new IllegalArgumentException("Cannot serialize unknown destination!");

        JSONArray json = new JSONArray();
        json.add(type.name());
        json.add(id);

        return json.toJSONString();
    }
}
