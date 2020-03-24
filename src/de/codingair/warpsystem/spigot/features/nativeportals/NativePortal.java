package de.codingair.warpsystem.spigot.features.nativeportals;

import de.codingair.codingapi.server.blocks.utils.Axis;
import de.codingair.codingapi.tools.Area;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalBlock;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalListener;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalType;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import de.codingair.codingapi.tools.io.lib.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NativePortal extends FeatureObject {
    private PortalType type;
    private boolean editMode = false;

    private List<PortalBlock> blocks = new ArrayList<>();
    private boolean visible = false;
    private List<PortalListener> listeners = new ArrayList<>();

    private Location[] cachedEdges = null;
    private Axis cachedAxis = null;

    private String displayName;

    public NativePortal() {
        this.type = null;
        this.blocks = new ArrayList<>();
    }

    public NativePortal(NativePortal nativePortal) {
        super(nativePortal);
        this.type = nativePortal.getType();
        this.blocks = new ArrayList<>(nativePortal.getBlocks());

        this.listeners.clear();
        this.listeners.addAll(nativePortal.getListeners());

        this.displayName = nativePortal.getDisplayName();
    }

    public NativePortal(PortalType type, List<PortalBlock> blocks) {
        this.type = type;
        this.blocks = blocks;
    }

    public NativePortal(PortalType type) {
        this.type = type;
        this.blocks = new ArrayList<>();
    }

    public NativePortal(PortalType type, Destination destination, String displayName, List<PortalBlock> blocks) {
        super(null, false, new WarpAction(destination));
        this.type = type;
        this.displayName = displayName;
        this.blocks = blocks;
        setSkip(true);
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
            this.displayName = d.get("Name");
        } else if(d.get("name") != null) {
            this.displayName = d.get("name");
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
                    de.codingair.codingapi.tools.Location loc = new de.codingair.codingapi.tools.Location();
                    loc.read(json);

                    if(loc.getWorld() == null) {
                        destroy();
                        return false;
                    }

                    blocks.add(new PortalBlock(loc));
                } else if(o instanceof String) {
                    String data = (String) o;
                    Location loc = de.codingair.codingapi.tools.Location.getByJSONString(data);

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
        super.write(d);

        d.put("type", type == null ? null : type.name());
        d.put("name", displayName);

        List<JSON> data = new ArrayList<>();

        for(PortalBlock block : this.blocks) {
            JSON json = new JSON();
            block.getLocation().write(json);
            data.add(json);
        }

        d.put("blocks", data);
    }

    @Override
    public void destroy() {
        super.destroy();

        this.type = null;
        this.cachedEdges = null;
        this.cachedAxis = null;
        if(this.blocks != null) this.blocks.clear();
        if(this.listeners != null) this.listeners.clear();
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        NativePortal nativePortal = (NativePortal) object;
        boolean visible = isVisible();
        if(visible) setVisible(false);

        this.cachedEdges = null;
        this.cachedAxis = null;
        this.blocks.clear();
        this.blocks.addAll(nativePortal.getBlocks());
        this.type = nativePortal.getType();
        this.listeners.clear();
        this.listeners.addAll(nativePortal.getListeners());
        this.displayName = nativePortal.getDisplayName();

        if(visible) setVisible(true);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof NativePortal)) return false;
        NativePortal nativePortal = (NativePortal) o;

        return super.equals(o) &&
                blocks.equals(nativePortal.blocks) &&
                listeners.equals(nativePortal.listeners) &&
                type == nativePortal.type;
    }

    @Override
    public FeatureObject perform(Player player) {
        TeleportOptions options = new TeleportOptions();

        options.setCanMove(true);

        return perform(player, options);
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
            if(isVisible() && !editMode) {
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

    public List<PortalBlock> getBlocks() {
        return Collections.unmodifiableList(this.blocks);
    }

    public List<PortalListener> getListeners() {
        return listeners;
    }

    public NativePortal clone() {
        return new NativePortal(this);
    }

    public Destination getDestination() {
        return hasAction(Action.WARP) ? ((WarpAction) getAction(Action.WARP)).getValue() : null;
    }

    public void setDestination(Destination destination) {
        if(destination == null) removeAction(Action.WARP);
        else addAction(new WarpAction(destination));
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        update();
    }
}
