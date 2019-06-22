package de.codingair.warpsystem.spigot.features.effectportals.utils;


import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.standalone.AnimationType;
import de.codingair.codingapi.player.Hologram;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.animations.utils.AnimationPlayer;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import de.codingair.warpsystem.utils.JSONObject;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class EffectPortal extends FeatureObject implements Removable {
    private final UUID uniqueId = UUID.randomUUID();
    private Location start;

    private Animation animation;
    private AnimationPlayer startAnim;
    private AnimationPlayer destinationAnim;
    private boolean showAnimation = true;

    private Hologram startHolo;
    private Hologram destinationHolo;
    private boolean startHoloStatus;
    private boolean destinationHoloStatus;

    private double hologramHeight;

    private boolean running = false;

    private String startName;
    private String destinationName;

    public EffectPortal() {
    }

    public EffectPortal(EffectPortal effectPortal) {
        apply(effectPortal);
    }

    public EffectPortal(Location start, Destination destination, Animation animation, String startName, String destinationName, double
            hologramHeight, boolean startHoloStatus, boolean destinationHoloStatus, String permission) {
        super(permission, false, new WarpAction(destination));

        this.start = start;
        this.animation = animation;
        this.startName = startName;
        this.destinationName = destinationName;
        this.hologramHeight = hologramHeight;

        this.startHoloStatus = startHoloStatus;
        this.destinationHoloStatus = destinationHoloStatus;
    }

    public boolean entered(Entity entity) {
        return false;
    }

    @Override
    public boolean read(JSONObject json) throws Exception {
        super.read(json);

        if(json.get("Destination") != null) {
            Destination destination;
            try {
                destination = new Destination((String) json.get("Destination"));
            } catch(Throwable ex) {
                destination = new Destination(json.get("Destination"), DestinationType.EffectPortal);
            }

            addAction(new WarpAction(destination));
        }

        if(json.get("start") != null) {
            this.start = Location.getByJSONString(json.get("start"));

            AnimationType animationType = AnimationType.valueOf(json.get("animationtype"));
            double animationHeight = Double.parseDouble(json.get("animationheight") + "");
            Particle particle = Particle.valueOf(json.get("particle"));
            double teleportRadius = Double.parseDouble(json.get("teleportradius") + "");
            this.startName = json.get("startname");
            this.destinationName = json.get("destinationname") == null ? null : (String) json.get("destinationname");
            this.hologramHeight = Double.parseDouble(json.get("hologramheight") + "");
            this.startHoloStatus = json.get("startholostatus") == null || Boolean.parseBoolean(json.get("startholostatus") + "");
            this.destinationHoloStatus = json.get("destinationholostatus") == null || Boolean.parseBoolean(json.get("destinationholostatus") + "");

            Sound sound = Sound.valueOf(json.get("teleportsound"));
            float soundVolume = Float.parseFloat(json.get("teleportsoundvolume") + "");
            float soundPitch = Float.parseFloat(json.get("teleportsoundpitch") + "");

            SoundData teleportSound = new SoundData(sound, soundVolume, soundPitch);

            Animation animation = new Animation(startName);
            ParticlePart particles = new ParticlePart(animationType.getCustom(), particle, teleportRadius, animationHeight, 10);
            animation.setTeleportSound(teleportSound);
            animation.getParticleParts().add(particles);

            AnimationManager.getInstance().addAnimation(animation);
            this.animation = AnimationManager.getInstance().getAnimation(startName);
        } else {
            this.animation = AnimationManager.getInstance().getAnimation(json.get("ep.anim.name"));
            this.start = Location.getByJSONString(json.get("ep.loc.start"));
            this.startName = json.get("ep.name.start");
            this.destinationName = json.get("ep.name.dest");
            this.hologramHeight = Double.parseDouble(json.get("ep.holo.height"));
            this.startHoloStatus = json.get("eo.holo.start.state");
            this.destinationHoloStatus = json.get("eo.holo.dest.state");
        }

        return true;
    }

    @Override
    public void write(JSONObject json) {
        super.write(json);

        json.put("ep.anim.name", this.animation == null ? null : this.animation.getName());
        json.put("ep.loc.start", this.start.toJSONString(4));
        json.put("ep.name.start", this.startName);
        json.put("ep.name.dest", this.destinationName);
        json.put("ep.holo.height", this.hologramHeight);
        json.put("ep.holo.start.state", this.startHoloStatus);
        json.put("ep.holo.dest.state", this.destinationHoloStatus);
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        EffectPortal effectPortal = (EffectPortal) object;

        this.start = effectPortal.getStart().clone();
        this.animation = effectPortal.animation == null ? null : effectPortal.animation.clone();
        this.startName = effectPortal.getStartName();
        this.destinationName = effectPortal.getDestinationName();
        this.hologramHeight = effectPortal.getHologramHeight();

        this.startHoloStatus = effectPortal.isStartHoloStatus();
        this.destinationHoloStatus = effectPortal.isDestinationHoloStatus();
    }

    public void add(Player player) {
        this.startHolo.addPlayer(player);
        if(this.destinationHolo != null) this.destinationHolo.addPlayer(player);
    }

    public void remove(Player player) {
        this.startHolo.removePlayer(player);
        if(this.destinationHolo != null) this.destinationHolo.removePlayer(player);
    }

    public void update() {
        if(isDisabled()) return;
        updateAnimations();
        updateHolograms();
    }

    public void updateHolograms() {
        if(isDisabled()) return;

        org.bukkit.Location destination = null;
        if(getDestination() != null && getDestination().getAdapter() instanceof PortalDestinationAdapter) {
            destination = getDestination().buildLocation();
        }

        if(this.startHolo == null)
            this.startHolo = new Hologram(this.start.clone().add(0, this.hologramHeight, 0), WarpSystem.getInstance(), ChatColor.translateAlternateColorCodes('&', startName.replace("_", " ")));
        else {
            this.startHolo.teleport(this.start.clone().add(0, this.hologramHeight, 0));
            this.startHolo.setText(ChatColor.translateAlternateColorCodes('&', startName.replace("_", " ")));
        }
        this.startHolo.setVisible(this.startHoloStatus);

        if(destination != null) {
            if(this.destinationHolo == null)
                this.destinationHolo = new Hologram(destination.clone().add(0, this.hologramHeight, 0), WarpSystem.getInstance(), ChatColor.translateAlternateColorCodes('&', destinationName.replace("_", " ")));
            else {
                this.destinationHolo.teleport(destination.clone().add(0, this.hologramHeight, 0));
                this.destinationHolo.setText(ChatColor.translateAlternateColorCodes('&', destinationName.replace("_", " ")));
            }
            this.destinationHolo.setVisible(this.destinationHoloStatus);
        } else {
            if(this.destinationHolo != null) {
                this.destinationHolo.destroy();
                this.destinationHolo = null;
            }
        }

        this.startHolo.update();
        this.startHolo.addAll();

        if(this.destinationHolo != null) {
            this.destinationHolo.update();
            this.destinationHolo.addAll();
        }
    }

    public void updateAnimations() {
        if(isDisabled()) return;
        if(this.startAnim != null && this.startAnim.isRunning()) this.startAnim.setRunning(false);
        if(this.destinationAnim != null && this.destinationAnim.isRunning()) this.destinationAnim.setRunning(false);
        if(animation == null || !showAnimation) return;

        org.bukkit.Location destination = null;
        if(getDestination() != null && getDestination().getAdapter() instanceof PortalDestinationAdapter) {
            destination = getDestination().buildLocation();
        } else if(this.destinationAnim != null) this.destinationAnim = null;

        this.startAnim = new AnimationPlayer(start, this.animation);
        if(destination != null) this.destinationAnim = new AnimationPlayer(destination, this.animation);

        this.startAnim.setMaxDistance(PortalManager.getInstance().getMaxParticleDistance());
        this.startAnim.setRunning(running);
        if(this.destinationAnim != null) {
            this.destinationAnim.setMaxDistance(PortalManager.getInstance().getMaxParticleDistance());
            this.destinationAnim.setRunning(running);
        }
    }

    @Override
    public void destroy() {
        setRunning(false);
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public Class<? extends Removable> getAbstractClass() {
        return EffectPortal.class;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public JavaPlugin getPlugin() {
        return WarpSystem.getInstance();
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Destination getDestination() {
        return hasAction(Action.WARP) ? getAction(WarpAction.class).getValue() : null;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if(this.running == running || isDisabled()) return;
        update();

        this.running = running;

        if(this.startAnim != null) this.startAnim.setRunning(running);
        if(this.destinationAnim != null) this.destinationAnim.setRunning(running);

        if(this.startHolo != null) {
            this.startHolo.setVisible(this.startHoloStatus && running);
            this.startHolo.update();
        }

        if(this.destinationHolo != null) {
            this.destinationHolo.setVisible(this.destinationHoloStatus && running);
            this.destinationHolo.update();
        }

        if(running) {
            this.startHolo.addAll();
            if(this.destinationHolo != null) this.destinationHolo.addAll();
        } else {
            this.startHolo.removeAll();
            if(this.destinationHolo != null) this.destinationHolo.removeAll();
        }

        if(running) API.addRemovable(this);
        else API.removeRemovable(this);
    }

    private double round(double d) {
        return ((double) Math.round(d * 10)) / 10;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
        this.updateHolograms();
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
        this.updateHolograms();
    }

    public boolean hasDestinationPortal() {
        return getDestination() != null && getDestination().getType() == DestinationType.EffectPortal;
    }

    public double getHologramHeight() {
        return hologramHeight;
    }

    public void setHologramHeight(double hologramHeight) {
        this.hologramHeight = round(hologramHeight);
        updateHolograms();
    }

    public void teleportToStart(Player player) {
        perform(player, true);
    }

    public void teleportToDestination(Player player) {
        perform(player);
    }

    @Override
    public FeatureObject perform(Player player) {
        return perform(player, false);
    }

    public FeatureObject perform(Player player, boolean toStart) {
        if(getDestination() == null) return this;
        super.perform(player, toStart ? this.destinationName : this.startName, toStart ? new Destination(new LocationAdapter(this.start)) : getAction(WarpAction.class).getValue(), this.animation.getTeleportSound(), true, !(getDestination().getAdapter() instanceof PortalDestinationAdapter));
        return this;
    }

    public boolean isRegistered() {
        PortalManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
        for(EffectPortal effectPortal : manager.getEffectPortals()) {
            if(effectPortal == this) return true;
        }

        return false;
    }

    public ItemStack getIcon() {
        return new ItemBuilder(Material.ENDER_PEARL).setName(ChatColor.GRAY + "\"" + ChatColor.RESET + this.startName + ChatColor.GRAY + "\"" + ChatColor.GRAY + " » " + ChatColor.GRAY + "\"" + ChatColor.RESET + this.destinationName + ChatColor.GRAY + "\"").getItem();
    }

    public boolean isStartHoloStatus() {
        return startHoloStatus;
    }

    public void setStartHoloStatus(boolean startHoloStatus) {
        this.startHoloStatus = startHoloStatus;
    }

    public boolean isDestinationHoloStatus() {
        return destinationHoloStatus;
    }

    public void setDestinationHoloStatus(boolean destinationHoloStatus) {
        this.destinationHoloStatus = destinationHoloStatus;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
        updateAnimations();
    }

    public boolean isShowAnimation() {
        return showAnimation;
    }

    public void setShowAnimation(boolean showAnimation) {
        if(this.showAnimation != showAnimation) {
            updateAnimations();
            this.showAnimation = showAnimation;
        }
    }
}
