package de.codingair.warpsystem.spigot.features.portals.utils;

import de.codingair.codingapi.server.blocks.utils.Axis;
import de.codingair.codingapi.tools.Area;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.HitBox;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.TeleportSoundPage;
import de.codingair.warpsystem.spigot.base.managers.PostWorldManager;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.TeleportSoundAction;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Portal extends FeatureObject {
    private boolean editMode = false;
    private Portal editing = null;

    private byte trigger;
    private Location spawn;
    private List<PortalBlock> blocks;
    private List<Animation> animations;
    private boolean visible = false;
    private final List<PortalListener> listeners = new ArrayList<>();

    private Hologram hologram = new Hologram();

    private final List<BlockHierarchy> merged = new ArrayList<>();
    private Location[] cachedEdges = null;
    private Axis cachedAxis = null;

    private String displayName;
    private String teleportName;
    private boolean waitingForWorld = false;

    public Portal() {
        this.blocks = new ArrayList<>();
        this.animations = new ArrayList<>();
    }

    public Portal(String displayName) {
        this(new Destination(), displayName, new ArrayList<>(), new ArrayList<>());
        getDestination().getCustomOptions().setDelay(0);
    }

    public Portal(Portal portal) {
        super(portal);

        this.blocks = new ArrayList<>();
        this.animations = new ArrayList<>();

        apply(portal);
    }

    public Portal(List<PortalBlock> blocks, List<Animation> animations) {
        this.blocks = blocks;
        this.animations = animations;
    }

    private Portal(Destination destination, String displayName, List<PortalBlock> blocks, List<Animation> animations) {
        super(null, false, new WarpAction(destination));
        this.displayName = displayName;
        this.blocks = blocks;
        this.animations = animations;
    }

    @Override
    public FeatureObject perform(Player player, TeleportOptions options) {
        options.addCallback(new Callback<Result>() {
            @Override
            public void accept(Result result) {
                if(result == Result.REMAINING_COOLDOWN) {
                    de.codingair.warpsystem.spigot.features.portals.listeners.PortalListener.waiting(player, Portal.this);
                } else de.codingair.warpsystem.spigot.features.portals.listeners.PortalListener.done(player);
            }
        });
        return super.perform(player, options);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        super.read(d);

        if(d.getBoolean("skip")) {
            Destination dest = getDestination();
            if(dest != null) dest.getCustomOptions().setDelay(0);
            setSkip(false);
        }

        this.displayName = d.getString("name");
        this.teleportName = d.getString("displayname");

        String world = d.getString("world");

        this.blocks = new ArrayList<>();
        JSONArray jsonArray = d.getList("blocks");
        if(jsonArray != null) {
            for(Object o : jsonArray) {
                if(o instanceof Map) {
                    JSON json = new JSON((Map<?, ?>) o);

                    PortalBlock block = new PortalBlock();
                    block.read(json);
                    if(block.getType() != BlockType.CUSTOM) blocks.add(block);
                }
            }
        }

        if(!blocks.isEmpty()) world = blocks.get(0).getLocation().getWorldName();

        jsonArray = d.getList("packedblocks");
        if(jsonArray != null) {
            for(Object o : jsonArray) {
                if(o instanceof Map) {
                    JSON json = new JSON((Map<?, ?>) o);

                    BlockHierarchy b = new BlockHierarchy(world);
                    b.read(json);
                    blocks.addAll(b.getBlocks());
                }
            }
        }

        this.animations = new ArrayList<>();
        jsonArray = d.getList("animations");
        if(jsonArray != null) {
            for(Object o : jsonArray) {
                if(o instanceof Map) {
                    JSON json = new JSON((Map<?, ?>) o);

                    Animation animation = new Animation();
                    animation.read(json);
                    animations.add(animation);
                }
            }
        }
        this.hologram = d.getSerializable("hologram", this.hologram);
        this.trigger = d.getByte("trigger");

        if(this.spawn != null) this.spawn.destroy();
        else this.spawn = new Location();
        this.spawn = d.getSerializable("spawn", this.spawn);
        if(this.spawn.isEmpty()) this.spawn = null;

        mergeBlocks();
        return true;
    }

    @Override
    public void write(DataWriter d) {
        super.write(d);

        d.put("name", displayName);
        d.put("displayname", teleportName);
        d.put("world", blocks.isEmpty() ? null : blocks.get(0).getLocation().getWorldName());

        List<JSON> data = new ArrayList<>();

        List<BlockHierarchy> hierarchies = new ArrayList<>();
        MergeAlgorithm.mergeByType(hierarchies, this.blocks);

        for(BlockHierarchy b : hierarchies) {
            JSON json = new JSON();
            b.write(json);
            data.add(json);
        }
        d.put("packedblocks", data);

        data = new ArrayList<>();

        for(Animation animation : this.animations) {
            JSON json = new JSON();
            animation.write(json);
            data.add(json);
        }

        d.put("animations", data);
        d.put("hologram", hologram);
        d.put("trigger", this.trigger);
        d.put("spawn", this.spawn);
    }

    @Override
    public void destroy() {
        super.destroy();
        setVisible(false, true);
        this.cachedEdges = null;
        this.cachedAxis = null;
        this.merged.clear();
        if(this.blocks != null) this.blocks.clear();
        if(this.animations != null) {
            for(Animation animation : this.animations) {
                if(animation != null) animation.getAnimation().setRunning(false);
            }
            this.animations.clear();
        }
        if(this.listeners != null) this.listeners.clear();

        if(this.spawn != null) this.spawn.destroy();

        trigger = 0;
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);
        if(!(object instanceof Portal)) return;

        Portal portal = (Portal) object;
        boolean visible = isVisible();
        if(visible) setVisible(false);

        this.cachedEdges = null;
        this.cachedAxis = null;
        this.merged.clear();

        this.blocks.clear();
        this.blocks.addAll(portal.getBlocks());

        this.animations.clear();
        for(Animation animation : portal.getAnimations()) {
            this.animations.add(new Animation(animation));
        }

        this.listeners.clear();
        this.listeners.addAll(portal.getListeners());
        this.displayName = portal.getDisplayName();
        this.teleportName = portal.getTeleportName();

        this.hologram.destroy();
        this.hologram.apply(((Portal) object).hologram);
        if(this.hologram.getText() == null) this.hologram.setText(this.displayName);

        this.trigger = portal.trigger;
        setSpawn(portal.spawn);

        if(visible) setVisible(true);
    }

    public void mergeBlocks() {
        if(merged.isEmpty()) MergeAlgorithm.merge(this.merged, this.blocks);
    }

    public List<BlockHierarchy> getMergedBlocks() {
        mergeBlocks();
        return merged;
    }

    public void updatePlayer(Player player) {
        this.hologram.updatePlayer(player);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Portal)) return false;
        Portal portal = (Portal) o;

        return super.equals(o) &&
                hologram.equals(portal.hologram) &&
                blocks.equals(portal.blocks) &&
                animations.equals(portal.animations) &&
                Objects.equals(this.spawn, portal.spawn) &&
                Objects.equals(this.displayName, portal.displayName) &&
                Objects.equals(this.teleportName, portal.teleportName) &&
                listeners.equals(portal.listeners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName);
    }

    @Override
    public void prepareTeleportOptions(String player, TeleportOptions options) {
        super.prepareTeleportOptions(player, options);

        if(!this.animations.isEmpty()) options.setTeleportAnimation(false);
        options.setCanMove(true);
        if(this.teleportName != null) options.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.teleportName));
    }

    public int enteredPortal(LivingEntity entity, org.bukkit.Location from) {
        return enteredPortal(entity, from, null);
    }

    /**
     * @param entity LivingEntity which should be checked
     * @param from   Location
     * @param to     Location
     * @return 0 if entity does not interact with this portal. It returns 1 if the given entiry enters the portal and
     * -1 if the portal has been left by this entity.
     */
    public int enteredPortal(LivingEntity entity, org.bukkit.Location from, org.bukkit.Location to) {
        if(entity == null || from == null) return 0;

        Location[] edges = getCachedEdges();
        int blockResult = 0, animationResult = 0;
        boolean inBlock = false, inAnimation = false;

        World w;
        if(edges != null && (w = edges[0].getWorld()) != null) {
            if(trigger == 0 || trigger == 1 || to == null || this.animations.isEmpty()) {
                boolean blockTest0 = false, blockTest1 = false;

                if(Area.isInArea(entity, from, edges[0], edges[1])) {
                    for(BlockHierarchy mergedBlock : getMergedBlocks()) {
                        if(Area.isInArea(entity, from, mergedBlock.getMin().toLocation(w), mergedBlock.getMax().toLocation(w).add(0.999999, 0.999999, 0.999999))) {
                            blockTest0 = true;
                            break;
                        }
                    }
                }

                if(to == null) {
                    if(blockTest0) return 1;
                } else {
                    if(Area.isInArea(entity, to, edges[0], edges[1])) {
                        for(BlockHierarchy mergedBlock : getMergedBlocks()) {
                            if(Area.isInArea(entity, to, mergedBlock.getMin().toLocation(w), mergedBlock.getMax().toLocation(w).add(0.999999, 0.999999, 0.999999))) {
                                blockTest1 = true;
                                break;
                            }
                        }
                    }

                    if(!blockTest0 && blockTest1) blockResult = 1;
                    else if(blockTest0 && !blockTest1) blockResult = -1;
                    else if(blockTest0 && blockTest1) inBlock = true;
                }
            }
        }

        if(trigger == 0 || trigger == 2 || this.blocks.isEmpty()) {
            double height = entity instanceof LivingEntity ? entity.getEyeHeight() : 0.7;
            HitBox hFrom = new HitBox(from, 0.1, height);

            boolean animationTest0 = touchesAnimation(entity.getWorld(), hFrom);

            if(to == null) {
                if(animationTest0) return 1;
            } else {
                HitBox move = new HitBox(to, 0.1, height);
                boolean animationTest1 = touchesAnimation(entity.getWorld(), move);

                if(!animationTest0 && animationTest1) animationResult = 1;
                else if(animationTest0 && !animationTest1) animationResult = -1;
                else if(animationTest0 && animationTest1) inAnimation = true;
                else if(!animationTest0 && !animationTest1) {
                    move = new HitBox(to, 0.1, height);
                    move.addProperty(new HitBox(from, 0.1, height));
                    if(touchesAnimation(entity.getWorld(), move)) return 2;
                }
            }
        }

        if(inBlock || inAnimation) return 0;
        return Math.max(Math.min(blockResult + animationResult, 1), -1);
    }

    public boolean isAround(org.bukkit.Location location, double distance) {
        if(getCachedEdges() == null) return false;
        Location l = new Location(location);
        if(Area.isInArea(location, getCachedEdges()[0].clone().subtract(distance, distance, distance), getCachedEdges()[1].clone().add(distance, distance, distance), true, distance)) {
            for(BlockHierarchy mergedBlock : getMergedBlocks()) {
                if(Area.isInArea(l, mergedBlock.getMin().toLocation(location.getWorld()), mergedBlock.getMax().toLocation(location.getWorld()).add(0.999999, 0.999999, 0.999999), true, distance)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean touchesAnimation(World world, HitBox entity) {
        for(Animation animation : this.animations) {
            if(!world.equals(animation.getLocation().getWorld())) continue;

            HitBox box = animation.getHitBox();
            if(box == null) return false;

            if(box.collides(entity)) return true;
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
        if(blocks.isEmpty()) return null;
        mergeBlocks();

        World world = blocks.get(0).getLocation().getWorld();
        BlockHierarchy.Bounds bounds = merged.get(0).getBounds().clone();

        for(int i = 1; i < merged.size(); i++) {
            BlockHierarchy b = merged.get(i);
            bounds.merge(b.getBounds());
        }

        return cachedEdges = new Location[] {bounds.getMin().toLocation(world), bounds.getMax().toLocation(world).add(0.999999, 0.999999, 0.999999)};
    }

    public boolean isVertically() {
        Vector v = getCachedEdges()[0].toVector().subtract(getCachedEdges()[1].toVector().subtract(new Vector(0.999999, 0.999999, 0.999999)));
        return Math.abs(v.getY()) >= 1;
    }

    public void update() {
        if(waitingForWorld) return;
        String waitForWorld = null;

        for(PortalBlock block : this.blocks) {
            if(block.getLocation().getWorld() == null) {
                waitForWorld = block.getLocation().getWorldName();
                break;
            }
            block.updateBlock(this);
        }

        if(waitForWorld == null)
            for(Animation animation : this.animations) {
                if(animation.getLocation().getWorld() == null) {
                    waitForWorld = animation.getLocation().getWorldName();
                    break;
                }
                animation.setVisible(this.visible);
            }

        if(waitForWorld != null) {
            waitingForWorld = true;
            PostWorldManager.callback(waitForWorld, new Callback<World>() {
                @Override
                public void accept(World object) {
                    waitingForWorld = false;
                    update();
                }
            });
            return;
        }

        if(visible) this.hologram.update();
        else this.hologram.hide();
    }

    public boolean isVisible() {
        return visible;
    }

    public Portal setVisible(boolean visible) {
        setVisible(visible, false);
        return this;
    }

    public void setVisible(boolean visible, boolean force) {
        if(visible != this.visible || force) {
            this.visible = visible;
            update();
        }
    }

    public void addPortalBlock(PortalBlock block) {
        this.blocks.add(block);
        this.merged.clear();
        this.cachedEdges = null;
        this.cachedAxis = null;
    }

    public void removePortalBlock(PortalBlock block) {
        this.blocks.remove(block);
        this.merged.clear();
        this.cachedEdges = null;
        this.cachedAxis = null;
    }

    public void removePortalBlock(org.bukkit.Location l) {
        PortalBlock block = getBlock(l);

        if(block != null) {
            this.blocks.remove(block);
            this.merged.clear();
        }
    }

    public PortalBlock getBlock(org.bukkit.Location l) {
        l = l.getBlock().getLocation();

        for(PortalBlock block : this.blocks) {
            if(block.getLocation().getBlock().getLocation().equals(l)) return block;
        }

        return null;
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
        return hasAction(Action.WARP) ? ((WarpAction) getAction(Action.WARP)).getValue() : null;
    }

    public Portal setDestination(Destination destination) {
        if(destination == null) removeAction(Action.WARP);
        else addAction(new WarpAction(destination));
        return this;
    }

    public Portal createDestinationIfAbsent() {
        if(getDestination() == null) setDestination(new Destination());
        return this;
    }

    public Portal createTeleportSoundIfAbsent() {
        if(!hasAction(Action.TELEPORT_SOUND)) addAction(new TeleportSoundAction(TeleportSoundPage.createStandard()));
        return this;
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

    public Portal setEditMode(boolean editMode) {
        this.editMode = editMode;
        if(!editMode) this.editing = null;
        update();
        return this;
    }

    public List<Animation> getAnimations() {
        return animations;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public byte getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = (byte) trigger;
        if(this.trigger < 0 || this.trigger > 2) this.trigger = 0;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        if(this.spawn != null && spawn != null) this.spawn.apply(spawn);
        else if(this.spawn == null && spawn != null) this.spawn = spawn.clone();
        else if(this.spawn != null && spawn == null) {
            this.spawn.destroy();
            this.spawn = null;
        }
    }

    public Portal getEditing() {
        return editing;
    }

    public void setEditing(Portal editing) {
        this.editing = editing;
    }

    public String getTeleportName() {
        return teleportName;
    }

    public void setTeleportName(String teleportName) {
        this.teleportName = teleportName;
        if(this.displayName.equals(this.teleportName)) {
            this.teleportName = null;
        }
    }
}
