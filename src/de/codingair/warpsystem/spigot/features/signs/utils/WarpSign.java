package de.codingair.warpsystem.spigot.features.signs.utils;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.destinations.Destination;
import de.codingair.warpsystem.spigot.base.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WarpSign {
    private Location location;
    private Destination destination;

    public WarpSign(Location location, Destination destination) {
        this.location = location;
        this.destination = destination;
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
        json.put("Destination", this.destination.toJSONString());

        return json.toJSONString();
    }

    public static WarpSign fromJSONString(String s) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        try {
            JSONObject json = (JSONObject) new JSONParser().parse(s);

            Location loc = Location.getByJSONString((String) json.get("Loc"));
            Destination destination;
            if(json.get("Destination") != null) {
                //New pattern
                 destination = new Destination((String) json.get("Destination"));
            } else if(json.get("Warp") != null) {
                //Old pattern
                Warp warp = manager.getWarp((String) json.get("Warp"), json.get("Category") == null ? null : manager.getCategory((String) json.get("Category")));
                destination = new Destination(warp.getIdentifier(), DestinationType.WarpIcon);
            } else throw new IllegalStateException("Couldn't find a pattern to recreate a WarpSign!");

            return new WarpSign(loc, destination);
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
