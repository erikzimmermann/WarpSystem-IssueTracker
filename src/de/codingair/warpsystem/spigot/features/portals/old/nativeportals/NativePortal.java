package de.codingair.warpsystem.spigot.features.portals.old.nativeportals;

import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NativePortal extends FeatureObject {
    private PortalType type;

    private List<PortalBlock> blocks;

    private String displayName;

    public NativePortal() {
        this.type = null;
        this.blocks = new ArrayList<>();
    }

    public Portal convert() {
        Portal portal = new Portal();
        portal.apply(this);

        for(PortalBlock block : blocks) {
            portal.addPortalBlock(new de.codingair.warpsystem.spigot.features.portals.utils.PortalBlock(block.getLocation(), type.getType()));
        }

        portal.setDisplayName(displayName);

        return portal;
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        super.read(d);

        if(d.get("Type") != null) {
            this.type = PortalType.valueOf(d.get("Type"));
        } else if(d.get("type") != null) {
            this.type = PortalType.valueOf(d.get("type"));
        }

        Destination destination = null;

        if(d.get("Destination") != null) {
            //new pattern
            destination = new Destination((String) d.get("Destination"));
        } else if(d.get("Warp") != null || d.get("GlobalWarp") != null) {
            //old pattern
            SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(d.get("Warp"));
            String globalWarp = d.get("GlobalWarp");

            if(warp != null) {
                destination = new Destination(warp.getName(), DestinationType.SimpleWarp);
            } else {
                destination = new Destination(globalWarp, DestinationType.GlobalWarp);
            }
        }

        if(destination != null) addAction(new WarpAction(destination));

        if(d.get("Name") != null) {
            this.displayName = d.getString("Name");
        } else if(d.get("name") != null) {
            this.displayName = d.getString("name");
        }

        JSONArray jsonArray = null;
        if(d.get("Blocks") != null) {
            jsonArray = d.getList("Blocks");
        } else if(d.get("blocks") != null) {
            jsonArray = d.getList("blocks");
        }

        this.blocks = new ArrayList<>();

        if(jsonArray != null) {
            for(Object o : jsonArray) {
                if(o instanceof Map) {
                    JSON json = new JSON((Map<?, ?>) o);
                    Location loc = new Location();
                    loc.read(json);

                    if(loc.getWorld() == null) {
                        destroy();
                        return false;
                    }

                    blocks.add(new PortalBlock(loc));
                } else if(o instanceof String) {
                    String data = (String) o;
                    Location loc = Location.getByJSONString(data);

                    if(loc == null || loc.getWorld() == null) {
                        destroy();
                        return false;
                    }

                    blocks.add(new PortalBlock(loc));
                }
            }

            for(PortalBlock block : blocks) {
                if(block.getLocation().getWorld() == null) {
                    destroy();
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void write(DataWriter d) {
    }

    @Override
    public void destroy() {
        super.destroy();

        this.type = null;
        if(this.blocks != null) this.blocks.clear();
    }
}
