package de.codingair.warpsystem.spigot.base.utils.teleport.destinations;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.DestinationAdapter;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class Destination implements Serializable {
    private String id;
    private DestinationType type;
    private DestinationAdapter adapter;
    private double offsetX, offsetY, offsetZ;
    private int signedX, signedY = 1, signedZ;

    public Destination() {
        id = null;
        type = DestinationType.UNKNOWN;
        adapter = null;
    }

    public Destination(String id, DestinationType type) {
        this.id = id;
        this.type = type;
        this.adapter = type.getInstance();
    }

    public Destination(String id, DestinationAdapter adapter) {
        this(adapter);
        this.id = id;
    }

    public Destination(DestinationAdapter adapter) {
        this.id = null;
        this.type = DestinationType.getByAdapter(adapter);
        this.adapter = adapter;
    }

    public Destination(String data) {
        try {
            JSONArray json = (JSONArray) new JSONParser().parse(data);

            this.type = json.get(0) == null ? null : DestinationType.valueOf((String) json.get(0));
            this.id = json.get(1) == null ? null : (String) json.get(1);
            this.adapter = type == null ? null : type.getInstance();
            if(json.size() > 2) {
                offsetX = Double.parseDouble(json.get(2) + "");
                offsetY = Double.parseDouble(json.get(3) + "");
                offsetZ = Double.parseDouble(json.get(4) + "");
                signedX = Integer.parseInt(json.get(5) + "");
                signedY = Integer.parseInt(json.get(6) + "");
                signedZ = Integer.parseInt(json.get(7) + "");
            }
        } catch(Exception ex) {
            throw new IllegalArgumentException("Wrong serialized data!", ex);
        }
    }

    public Destination apply(Destination destination) {
        if(destination == null) {
            this.id = null;
            this.adapter = null;
            this.type = null;
            this.offsetX = 0;
            this.offsetY = 0;
            this.offsetZ = 0;
            this.signedX = 0;
            this.signedY = 0;
            this.signedZ = 0;
            return this;
        }

        this.id = destination.id;
        this.adapter = destination.adapter;
        this.type = destination.type;
        this.offsetX = destination.offsetX;
        this.offsetY = destination.offsetY;
        this.offsetZ = destination.offsetZ;
        this.signedX = destination.signedX;
        this.signedY = destination.signedY;
        this.signedZ = destination.signedZ;
        return this;
    }

    public boolean teleport(Player player, String message, String displayName, boolean checkPermission, boolean silent, double costs, Callback<TeleportResult> callback) {
        if(adapter == null) return false;
        player.setFallDistance(0F);
        return adapter.teleport(player, id, buildRandomOffset(), displayName, checkPermission, message, silent, costs, callback);
    }

    public void sendMessage(Player player, String message, String displayName, double costs) {
        if(adapter == null || message == null) return;
        player.sendMessage((message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName)));
    }

    public Location buildLocation() {
        if(offsetX != 0 || offsetY != 0 || offsetZ != 0) {
            double offsetX = signedX == 1 ? Math.random() * this.offsetX : signedX == -1 ? Math.random() * -this.offsetX : Math.random() * 2 * this.offsetX - this.offsetX;
            double offsetY = signedY == 1 ? Math.random() * this.offsetY : signedY == -1 ? Math.random() * -this.offsetY : Math.random() * 2 * this.offsetY - this.offsetY;
            double offsetZ = signedZ == 1 ? Math.random() * this.offsetZ : signedZ == -1 ? Math.random() * -this.offsetZ : Math.random() * 2 * this.offsetZ - this.offsetZ;

            return adapter.buildLocation(id).add(offsetX, offsetY, offsetZ);
        } else return adapter.buildLocation(id);
    }

    public Vector buildRandomOffset() {
        double offsetX = signedX == 1 ? Math.random() * this.offsetX : signedX == -1 ? Math.random() * -this.offsetX : Math.random() * 2 * this.offsetX - this.offsetX;
        double offsetY = signedY == 1 ? Math.random() * this.offsetY : signedY == -1 ? Math.random() * -this.offsetY : Math.random() * 2 * this.offsetY - this.offsetY;
        double offsetZ = signedZ == 1 ? Math.random() * this.offsetZ : signedZ == -1 ? Math.random() * -this.offsetZ : Math.random() * 2 * this.offsetZ - this.offsetZ;

        return new Vector(offsetX, offsetY, offsetZ);
    }

    public double getCosts() {
        return adapter == null ? 0 : adapter.getCosts(id);
    }

    public SimulatedTeleportResult simulate(Player player, boolean checkPermission) {
        if(adapter == null) return new SimulatedTeleportResult(null, TeleportResult.NO_ADAPTER);
        return adapter.simulate(player, this.id, checkPermission);
    }

    public String getId() {
        if(this.adapter instanceof LocationAdapter && ((LocationAdapter) this.adapter).getLocation() != null) {
            return new de.codingair.codingapi.tools.Location(((LocationAdapter) this.adapter).getLocation()).toJSONString(2);
        }
        return id;
    }

    public void setId(String id) {
        if(this.adapter instanceof LocationAdapter && ((LocationAdapter) this.adapter).getLocation() != null) {
            ((LocationAdapter) this.adapter).setLocation(null);
        }
        this.id = id;
    }

    public DestinationType getType() {
        return type;
    }

    public void setType(DestinationType type) {
        this.type = type;
    }

    public DestinationAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(DestinationAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.type = DestinationType.getById(d.getInteger("type"));
        this.adapter = type.getInstance();

        if(adapter != null && adapter instanceof Serializable) {
            ((Serializable) adapter).read(d);
        } else if(type == DestinationType.Location) {
            de.codingair.codingapi.tools.Location loc = new de.codingair.codingapi.tools.Location();
            d.getSerializable("id", loc);
            ((LocationAdapter) this.adapter).setLocation(loc);
        } else id = d.getRaw("id");

        this.offsetX = d.getDouble("oX");
        this.offsetY = d.getDouble("oY");
        this.offsetZ = d.getDouble("oZ");
        this.signedX = d.getInteger("sX");
        this.signedY = d.getInteger("sY");
        this.signedZ = d.getInteger("sZ");
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("type", type.getId());

        if(adapter != null && adapter instanceof Serializable) {
            ((Serializable) adapter).write(d);
        } else {
            Object id;
            if(type == DestinationType.Location) {
                id = new de.codingair.codingapi.tools.Location(buildLocation());
            } else id = this.id;

            d.put("id", id);
        }
        d.put("oX", offsetX);
        d.put("oY", offsetY);
        d.put("oZ", offsetZ);
        d.put("sX", signedX);
        d.put("sY", signedY);
        d.put("sZ", signedZ);
    }

    @Override
    public void destroy() {
        this.type = null;
        this.id = null;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.signedX = 0;
        this.signedY = 0;
        this.signedZ = 0;
    }

    public JSONArray toJSONArray() {
        if(this.type == DestinationType.UNKNOWN) throw new IllegalArgumentException("Cannot serialize unknown destination!");

        JSONArray json = new JSONArray();
        json.add(type == null ? null : type.name());
        json.add(id);
        if(offsetX != 0 || offsetY != 0 || offsetZ != 0) {
            json.add(offsetX);
            json.add(offsetY);
            json.add(offsetZ);
            json.add(signedX);
            json.add(signedY);
            json.add(signedZ);
        }

        return json;
    }

    public String toJSONString() {
        return toJSONArray().toJSONString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        if(getId() == null) return that.getId() == null && type == that.type;
        return getId().equals(that.getId())
                && type == that.type
                && offsetX == that.offsetX
                && offsetY == that.offsetY
                && offsetZ == that.offsetZ
                && signedX == that.signedX
                && signedY == that.signedY
                && signedZ == that.signedZ;
    }

    public Destination clone() {
        Destination destination = new Destination();
        destination.id = getId();
        destination.type = type;
        destination.adapter = adapter == null ? null : type.getInstance();
        destination.offsetX = offsetX;
        destination.offsetY = offsetY;
        destination.offsetZ = offsetZ;
        destination.signedX = signedX;
        destination.signedY = signedY;
        destination.signedZ = signedZ;
        return destination;
    }

    @Override
    public String toString() {
        return "Destination{id=" + getId() + ", " + this.type + "}";
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public int getSignedX() {
        return signedX;
    }

    public void setSignedX(int signedX) {
        this.signedX = signedX;
    }

    public int getSignedY() {
        return signedY;
    }

    public void setSignedY(int signedY) {
        this.signedY = signedY;
    }

    public int getSignedZ() {
        return signedZ;
    }

    public void setSignedZ(int signedZ) {
        this.signedZ = signedZ;
    }
}
