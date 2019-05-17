package de.codingair.warpsystem.spigot.features.effectportals.utils;


import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.Animation;
import de.codingair.codingapi.particles.animations.standalone.*;
import de.codingair.codingapi.player.Hologram;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CostsAction;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.UUID;

public class Portal extends FeatureObject implements Removable {
    private final UUID uniqueId = UUID.randomUUID();
    private Location start;

    private AnimationType animationType;
    private Particle particle;
    private Animation startAnim;
    private Animation destinationAnim;
    private Hologram startHolo;
    private Hologram destinationHolo;
    private boolean startHoloStatus;
    private boolean destinationHoloStatus;

    private double teleportRadius;
    private double hologramHeight;
    private double animationHeight;

    private boolean running = false;

    private String startName;
    private String destinationName;
    private SoundData teleportSound;

    public Portal() {
    }

    public Portal(Portal portal) {
        this(portal.getStart(), new Destination().apply(portal.getDestination()), portal.getAnimationType(), portal.getAnimationHeight(), portal.getParticle(), portal.getTeleportRadius(), portal.getStartName(), portal.getDestinationName(), portal.getTeleportSound(), portal.getHologramHeight(), portal.isStartHoloStatus(), portal.isDestinationHoloStatus(), portal.getPermission());
    }

    public Portal(Location start, Destination destination, AnimationType animationType, double animationHeight, Particle particle, double teleportRadius, String startName, String destinationName, SoundData teleportSound, double
            hologramHeight, boolean startHoloStatus, boolean destinationHoloStatus, String permission) {
        super(permission, false, new WarpAction(destination));

        this.start = start;
        this.animationType = animationType;
        this.animationHeight = animationHeight;
        this.particle = particle;
        this.teleportRadius = teleportRadius;
        this.startName = startName;
        this.destinationName = destinationName;
        this.teleportSound = teleportSound;
        this.hologramHeight = hologramHeight;

        this.startHoloStatus = startHoloStatus;
        this.destinationHoloStatus = destinationHoloStatus;
    }

    @Override
    public boolean read(JSONObject json) throws Exception {
        super.read(json);

        if(json.get("Destination") != null) {
            Destination destination;
            try {
                destination = new Destination((String) json.get("Destination"));
            } catch(Throwable ex) {
                destination = new Destination((String) json.get("Destination"), DestinationType.EffectPortal);
            }
            
            addAction(new WarpAction(destination));
        }

        if(json.get("Start") != null) {
            this.start = Location.getByJSONString((String) json.get("Start"));

            this.animationType = AnimationType.valueOf((String) json.get("AnimationType"));
            this.animationHeight = Double.parseDouble(json.get("AnimationHeight") + "");
            this.particle = Particle.valueOf((String) json.get("Particle"));
            this.teleportRadius = Double.parseDouble(json.get("TeleportRadius") + "");
            this.startName = (String) json.get("StartName");
            this.destinationName = json.get("DestinationName") == null ? null : (String) json.get("DestinationName");
            this.hologramHeight = Double.parseDouble(json.get("HologramHeight") + "");
            this.startHoloStatus = json.get("StartHoloStatus") == null || Boolean.parseBoolean(json.get("StartHoloStatus") + "");
            this.destinationHoloStatus = json.get("DestinationHoloStatus") == null || Boolean.parseBoolean(json.get("DestinationHoloStatus") + "");

            Sound sound = Sound.valueOf((String) json.get("TeleportSound"));
            float soundVolume = Float.parseFloat(json.get("TeleportSoundVolume") + "");
            float soundPitch = Float.parseFloat(json.get("TeleportSoundPitch") + "");

            this.teleportSound = new SoundData(sound, soundVolume, soundPitch);
        } else {
            this.start = Location.getByJSONString((String) json.get("start"));

            this.animationType = AnimationType.valueOf((String) json.get("animationtype"));
            this.animationHeight = Double.parseDouble(json.get("animationheight") + "");
            this.particle = Particle.valueOf((String) json.get("particle"));
            this.teleportRadius = Double.parseDouble(json.get("teleportradius") + "");
            this.startName = (String) json.get("startname");
            this.destinationName = json.get("destinationname") == null ? null : (String) json.get("destinationname");
            this.hologramHeight = Double.parseDouble(json.get("hologramheight") + "");
            this.startHoloStatus = json.get("startholostatus") == null || Boolean.parseBoolean(json.get("startholostatus") + "");
            this.destinationHoloStatus = json.get("destinationholostatus") == null || Boolean.parseBoolean(json.get("destinationholostatus") + "");

            Sound sound = Sound.valueOf((String) json.get("teleportsound"));
            float soundVolume = Float.parseFloat(json.get("teleportsoundvolume") + "");
            float soundPitch = Float.parseFloat(json.get("teleportsoundpitch") + "");

            this.teleportSound = new SoundData(sound, soundVolume, soundPitch);
        }

        return true;
    }

    @Override
    public void write(JSONObject json) {
        super.write(json);

        json.put("start", this.start.toJSONString(4));
        json.put("animationtype", this.animationType.name());
        json.put("animationheight", this.getAnimationHeight());
        json.put("particle", this.particle.name());
        json.put("teleportradius", this.teleportRadius);
        json.put("startname", this.startName);
        json.put("destinationname", this.destinationName);
        json.put("teleportsound", this.teleportSound.getSound().name());
        json.put("teleportsoundvolume", this.teleportSound.getVolume());
        json.put("teleportsoundpitch", this.teleportSound.getPitch());
        json.put("hologramheight", this.getHologramHeight());
        json.put("startholostatus", this.startHoloStatus);
        json.put("destinationholostatus", this.destinationHoloStatus);
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        Portal portal = (Portal) object;
                
        this.start = portal.getStart().clone();
        this.animationType = portal.getAnimationType();
        this.animationHeight = portal.getAnimationHeight();
        this.particle = portal.getParticle();
        this.teleportRadius = portal.getTeleportRadius();
        this.startName = portal.getStartName();
        this.destinationName = portal.getDestinationName();
        this.hologramHeight = portal.getHologramHeight();
        this.teleportSound = portal.getTeleportSound();

        this.startHoloStatus = portal.isStartHoloStatus();
        this.destinationHoloStatus = portal.isDestinationHoloStatus();
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
        if(getDestination().getAdapter() instanceof PortalDestinationAdapter) {
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

        org.bukkit.Location destination = null;
        if(getDestination().getAdapter() instanceof PortalDestinationAdapter) {
            destination = getDestination().buildLocation();
        } else if(this.destinationAnim != null) this.destinationAnim = null;

        switch(animationType) {
            case SINUS: {
                this.startAnim = new SinusAnimation(particle, start, this.teleportRadius, this.animationHeight);
                if(destination != null) this.destinationAnim = new SinusAnimation(particle, destination, this.teleportRadius, this.animationHeight);
                break;
            }

            case CIRCLE: {
                this.startAnim = new CircleAnimation(particle, this.teleportRadius, this.animationHeight, start);
                if(destination != null) this.destinationAnim = new CircleAnimation(particle, this.teleportRadius, this.animationHeight, destination);
                break;
            }

            case ROTATING_CIRCLE: {
                this.startAnim = new RotatingCircleAnimation(particle, this.teleportRadius, this.animationHeight, start);
                if(destination != null) this.destinationAnim = new RotatingCircleAnimation(particle, this.teleportRadius, this.animationHeight, destination);
                break;
            }

            case PULSING_CIRCLE: {
                this.startAnim = new PulsingCircleAnimation(particle, this.teleportRadius, this.animationHeight, start, 15);
                if(destination != null) this.destinationAnim = new PulsingCircleAnimation(particle, this.teleportRadius, this.animationHeight, destination, 15);
                break;
            }
        }

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
        return Portal.class;
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

    public AnimationType getAnimationType() {
        return animationType;
    }

    public void setAnimationType(AnimationType type) {
        this.animationType = type;
        updateAnimations();
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
        updateAnimations();
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

    public void setSoundVolume(double volume) {
        if(volume < 0) volume = 0;
        if(volume > 1) volume = 1;

        this.teleportSound.setVolume((float) round(volume));
    }

    public void setSoundPitch(double pitch) {
        if(pitch < 0) pitch = 0;
        if(pitch > 1) pitch = 1;

        this.teleportSound.setPitch((float) round(pitch));
    }

    public SoundData getTeleportSound() {
        return teleportSound;
    }

    public void setTeleportSound(SoundData teleportSound) {
        this.teleportSound = teleportSound;
    }

    public double getTeleportRadius() {
        return teleportRadius;
    }

    public void setTeleportRadius(double teleportRadius) {
        if(teleportRadius < 0) teleportRadius = 0;
        if(teleportRadius > 100) teleportRadius = 100;
        this.teleportRadius = round(teleportRadius);
        updateAnimations();
    }

    public double getHologramHeight() {
        return hologramHeight;
    }

    public void setHologramHeight(double hologramHeight) {
        this.hologramHeight = round(hologramHeight);
        updateHolograms();
    }

    public double getAnimationHeight() {
        return animationHeight;
    }

    public void setAnimationHeight(double animationHeight) {
        this.animationHeight = round(animationHeight);
        updateAnimations();
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
        super.perform(player, toStart ? this.destinationName : this.startName, toStart ? new Destination(new LocationAdapter(this.start)) : getAction(WarpAction.class).getValue(), this.teleportSound, true, !(getDestination().getAdapter() instanceof PortalDestinationAdapter));
        return this;
    }

    public boolean isRegistered() {
        PortalManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
        for(Portal portal : manager.getPortals()) {
            if(portal == this) return true;
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
}
