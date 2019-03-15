package de.codingair.warpsystem.spigot.features.nativeportals;

import de.codingair.codingapi.server.blocks.utils.Axis;
import de.codingair.codingapi.tools.Area;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalBlock;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalListener;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalType;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Portal {
    private PortalType type;
    private List<PortalBlock> blocks;
    private boolean visible = false;
    private List<PortalListener> listeners = new ArrayList<>();

    private Location[] cachedEdges = null;
    private Axis cachedAxis = null;

    private Destination destination;
    private String displayName;

    public Portal() {
        this.type = null;
        this.blocks = new ArrayList<>();
    }

    public Portal(Portal portal) {
        this.type = portal.getType();
        this.blocks = new ArrayList<>(portal.getBlocks());
        this.listeners.addAll(portal.getListeners());
        this.destination = new Destination(portal.getDestination().getId(), portal.getDestination().getType());
    }

    public Portal(PortalType type, List<PortalBlock> blocks) {
        this.type = type;
        this.blocks = blocks;
    }

    public Portal(PortalType type) {
        this.type = type;
        this.blocks = new ArrayList<>();
    }

    public Portal(PortalType type, Destination destination, String displayName, List<PortalBlock> blocks) {
        this.type = type;
        this.destination = destination;
        this.displayName = displayName;
        this.blocks = blocks;
    }

    public void apply(Portal portal) {
        boolean visible = isVisible();
        if(visible) setVisible(false);

        this.cachedEdges = null;
        this.cachedAxis = null;
        this.blocks.clear();
        this.blocks.addAll(portal.getBlocks());
        this.type = portal.getType();
        this.listeners.clear();
        this.listeners.addAll(portal.getListeners());

        if(visible) setVisible(true);
    }

    public boolean isInPortal(LivingEntity entity) {
        return isInPortal(entity, entity.getLocation());
    }

    public boolean isInPortal(LivingEntity entity, Location target) {
        if(entity == null || target == null) return false;

        Location[] edges = getCachedEdges();
        if(Area.isInArea(entity, target, edges[0], edges[1])) {
            for(PortalBlock block : getBlocks()) {
                if(block.touches(entity, target)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isAround(Location location, double distance, boolean isExact) {
        if(Area.isInArea(location, getCachedEdges()[0], getCachedEdges()[1], true, distance)) {
            for(PortalBlock block : blocks) {
                if(isExact && block.getLocation().distance(location) == distance) return true;
                else if(block.getLocation().distance(location) <= distance) return true;
            }
        }

        return false;
    }

    public Axis getCachedAxis() {
        if(cachedAxis == null) {
            int x, z;

            Location[] edges = getCachedEdges();

            x = Math.abs(edges[0].getBlockX() - edges[1].getBlockX());
            z = Math.abs(edges[0].getBlockZ() - edges[1].getBlockZ());

            this.cachedAxis = x > z ? Axis.X : Axis.Z;
        }

        return this.cachedAxis;
    }

    public Location[] getCachedEdges() {
        if(cachedEdges != null) return cachedEdges;

        int x0 = 0, y0 = 0, z0 = 0, x1 = 0, y1 = 0, z1 = 0;
        World world = null;

        boolean first = true;
        for(PortalBlock block : this.blocks) {
            if(first) {
                first = false;

                world = block.getLocation().getWorld();

                x0 = block.getLocation().getBlockX();
                y0 = block.getLocation().getBlockY();
                z0 = block.getLocation().getBlockZ();

                x1 = block.getLocation().getBlockX();
                y1 = block.getLocation().getBlockY();
                z1 = block.getLocation().getBlockZ();

                continue;
            }

            if(x0 > block.getLocation().getBlockX()) x0 = block.getLocation().getBlockX();
            else if(x1 < block.getLocation().getBlockX()) x1 = block.getLocation().getBlockX();
            if(y0 > block.getLocation().getBlockY()) y0 = block.getLocation().getBlockY();
            else if(y1 < block.getLocation().getBlockY()) y1 = block.getLocation().getBlockY();
            if(z0 > block.getLocation().getBlockZ()) z0 = block.getLocation().getBlockZ();
            else if(z1 < block.getLocation().getBlockZ()) z1 = block.getLocation().getBlockZ();
        }

        double diff = 0.0;
        return cachedEdges = new Location[] {new Location(world, x0 - diff, y0 - diff, z0 - diff), new Location(world, x1 + 0.999999 + diff, y1 + 0.999999 + diff, z1 + 0.999999 + diff)};
    }

    public boolean isVertically() {
        Vector v = getCachedEdges()[0].toVector().subtract(getCachedEdges()[1].toVector().subtract(new Vector(0.999999, 0.999999, 0.999999)));
        return Math.abs(v.getY()) >= 1;
    }

    public void update() {
        if(getType() == null) return;

        for(PortalBlock block : this.blocks) {
            block.updateBlock(this);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if(visible != this.visible) {
            this.visible = visible;
            update();
        }
    }

    public PortalType getType() {
        return type;
    }

    public void setType(PortalType type) {
        if(this.type != type) {
            if(isVisible()) {
                setVisible(false);
                this.type = type;
                setVisible(true);
            } else {
                this.type = type;
            }
        }
    }

    public void addPortalBlock(PortalBlock block) {
        this.blocks.add(block);
        this.cachedEdges = null;
        this.cachedAxis = null;
    }

    public void removePortalBlock(PortalBlock block) {
        this.blocks.remove(block);
        this.cachedEdges = null;
        this.cachedAxis = null;
    }

    public void clear() {
        setVisible(false);
        this.blocks.clear();
        this.listeners.clear();
        this.cachedEdges = null;
        this.cachedAxis = null;
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();

        json.put("Type", type == null ? null : type.name());
        json.put("Destination", this.destination.toJSONString());
        json.put("Name", displayName);

        JSONArray jsonArray = new JSONArray();

        for(PortalBlock block : this.blocks) {
            jsonArray.add(block.getLocation().toJSONString(4));
        }

        json.put("Blocks", jsonArray.toJSONString());

        return json.toJSONString();
    }

    public static Portal fromJSONString(String s) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(s);

            PortalType type = json.get("Type") == null ? null : PortalType.valueOf((String) json.get("Type"));
            Destination destination;

            if(json.get("Destination") != null) {
                //new pattern
                destination = new Destination((String) json.get("Destination"));
            } else {
                //old pattern
                SimpleWarp warp = json.get("Warp") == null ? null : SimpleWarpManager.getInstance().getWarp((String) json.get("Warp"));
                String globalWarp = json.get("GlobalWarp") == null ? null : (String) json.get("GlobalWarp");

                if(warp != null) {
                    destination = new Destination(warp.getName(), DestinationType.SimpleWarp);
                } else {
                    destination = new Destination(globalWarp, DestinationType.GlobalWarp);
                }
            }

            String displayName = (String) json.get("Name");

            JSONArray jsonArray = (JSONArray) new JSONParser().parse((String) json.get("Blocks"));
            List<PortalBlock> blocks = new ArrayList<>();
            for(Object o : jsonArray) {
                String data = (String) o;
                Location loc = de.codingair.codingapi.tools.Location.getByJSONString(data);

                if(loc == null || loc.getWorld() == null) return null;

                blocks.add(new PortalBlock(loc));
            }

            for(PortalBlock block : blocks) {
                if(block.getLocation().getWorld() == null) return null;
            }

            return new Portal(type, destination, displayName, blocks);
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PortalBlock> getBlocks() {
        return Collections.unmodifiableList(this.blocks);
    }

    public List<PortalListener> getListeners() {
        return listeners;
    }

    public Portal clone() {
        return new Portal(this);
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
