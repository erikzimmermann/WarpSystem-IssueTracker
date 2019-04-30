package de.codingair.warpsystem.spigot.features.signs.utils;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WarpSign {
    private Location location;
    private Destination destination;
    private String permission;

    public WarpSign(Location location) {
        this.location = location;
    }

    public WarpSign(Location location, Destination destination) {
        this.location = location;
        this.destination = destination;
    }

    public WarpSign(Location location, Destination destination, String permission) {
        this.location = location;
        this.destination = destination;
        this.permission = permission;
    }

    public Location getLocation() {
        return location;
    }

    public Destination getDestination() {
        return destination;
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();

        json.put("Loc", this.location.toJSONString(0));
        json.put("Destination", this.destination == null || this.destination.getType() == DestinationType.UNKNOWN || this.destination.getType() == null ? null : this.destination.toJSONString());
        json.put("Permissions", this.permission);

        return json.toJSONString();
    }

    public static WarpSign fromJSONString(String s) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        try {
            JSONObject json = (JSONObject) new JSONParser().parse(s);

            Location loc = Location.getByJSONString((String) json.get("Loc"));
            Destination destination = null;
            if(json.get("Destination") != null) {
                //New pattern
                 destination = new Destination((String) json.get("Destination"));
            } else if(json.get("Warp") != null) {
                //Old pattern
                Icon warp = manager.getIcon((String) json.get("Warp"));
                destination = new Destination(warp.getName(), DestinationType.SimpleWarp);
            }

            String permissions = json.get("Permissions") == null ? null : (String) json.get("Permissions");

            return new WarpSign(loc, destination, permissions);
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public WarpSign clone() {
        return new WarpSign(this.location.clone(), this.destination != null ? this.destination.clone() : null, this.permission);
    }
}
