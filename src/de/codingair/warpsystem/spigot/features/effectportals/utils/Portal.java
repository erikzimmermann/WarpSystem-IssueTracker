package de.codingair.warpsystem.spigot.features.effectportals.utils;


import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.Animation;
import de.codingair.codingapi.particles.animations.standalone.*;
import de.codingair.codingapi.player.Hologram;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
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

public class Portal implements Removable {
    private final UUID uniqueId = UUID.randomUUID();
    private Location start;
    private Destination destination;

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

    private String permission;
    private boolean disabled = false;

    public Portal(Portal portal) {
        this(portal.getStart(), new Destination().apply(portal.getDestination()), portal.getAnimationType(), portal.getAnimationHeight(), portal.getParticle(), portal.getTeleportRadius(), portal.getStartName(), portal.getDestinationName(), portal.getTeleportSound(), 2.2, portal.isStartHoloStatus(), portal.isDestinationHoloStatus());

        this.hologramHeight = portal.getHologramHeight();
        this.permission = portal.getPermission();
    }

    public Portal(Location start, Destination destination, AnimationType animationType, double animationHeight, Particle particle, double teleportRadius, String startName, String destinationName, SoundData teleportSound, double
            hologramHeight, boolean startHoloStatus, boolean destinationHoloStatus) {
        this.start = start;
        this.destination = destination;
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

    public void add(Player player) {
        this.startHolo.addPlayer(player);
        if(this.destinationHolo != null) this.destinationHolo.addPlayer(player);
    }

    public void remove(Player player) {
        this.startHolo.removePlayer(player);
        if(this.destinationHolo != null) this.destinationHolo.removePlayer(player);
    }

    public void applyAttrs(Portal portal) {
        this.start = portal.getStart().clone();
        this.destination.apply(portal.getDestination());
        this.animationType = portal.getAnimationType();
        this.animationHeight = portal.getAnimationHeight();
        this.particle = portal.getParticle();
        this.teleportRadius = portal.getTeleportRadius();
        this.startName = portal.getStartName();
        this.destinationName = portal.getDestinationName();
        this.hologramHeight = portal.getHologramHeight();
        this.teleportSound = portal.getTeleportSound();
        this.permission = portal.getPermission();

        this.startHoloStatus = portal.isStartHoloStatus();
        this.destinationHoloStatus = portal.isDestinationHoloStatus();
    }

    public void update() {
        if(this.disabled) return;
        updateAnimations();
        updateHolograms();
    }

    public void updateHolograms() {
        if(this.disabled) return;

        org.bukkit.Location destination = null;
        if(this.destination.getAdapter() instanceof PortalDestinationAdapter) {
            destination = this.destination.buildLocation();
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
        if(this.disabled) return;
        if(this.startAnim != null && this.startAnim.isRunning()) this.startAnim.setRunning(false);
        if(this.destinationAnim != null && this.destinationAnim.isRunning()) this.destinationAnim.setRunning(false);

        org.bukkit.Location destination = null;
        if(this.destination.getAdapter() instanceof PortalDestinationAdapter) {
            destination = this.destination.buildLocation();
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
        if(destination == null) this.destination = new Destination();
        return destination;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if(this.running == running || this.disabled) return;
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
        if(this.permission != null && !player.hasPermission(this.permission)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Portal_Insufficient_Permissions"));
            return;
        }

        player.teleport(this.start);

        WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.EffectPortal, new Destination(new LocationAdapter(this.start)), this.destinationName, 0, true, true,
                WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.Portals", true) ?
                        Lang.getPrefix() + Lang.get("Teleported_To") : null,
                false, this.teleportSound, false, null);
    }

    public void teleportToDestination(Player player) {
        if(this.destination == null || this.destination.getAdapter() == null || this.destination.getId() == null) return;

        if(this.permission != null && !player.hasPermission(this.permission)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Portal_Insufficient_Permissions"));
            return;
        }

        WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.EffectPortal, this.destination, this.startName, 0, true, true,
                WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.Portals", true) ?
                        Lang.getPrefix() + Lang.get("Teleported_To") : null,
                false, this.teleportSound, !(this.destination.getAdapter() instanceof PortalDestinationAdapter), null);
    }

    public boolean isRegistered() {
        PortalManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
        for(Portal portal : manager.getPortals()) {
            if(portal == this) return true;
        }

        return false;
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();

        json.put("Start", this.start.toJSONString(4));
        json.put("Destination", this.destination.getType() == DestinationType.UNKNOWN ? null : this.destination.toJSONString());
        json.put("AnimationType", this.animationType.name());
        json.put("AnimationHeight", this.getAnimationHeight());
        json.put("Particle", this.particle.name());
        json.put("TeleportRadius", this.teleportRadius);
        json.put("StartName", this.startName);
        json.put("DestinationName", this.destinationName);
        json.put("TeleportSound", this.teleportSound.getSound().name());
        json.put("TeleportSoundVolume", this.teleportSound.getVolume());
        json.put("TeleportSoundPitch", this.teleportSound.getPitch());
        json.put("HologramHeight", this.getHologramHeight());
        json.put("StartHoloStatus", this.startHoloStatus);
        json.put("DestinationHoloStatus", this.destinationHoloStatus);
        json.put("Permission", this.permission);

        return json.toJSONString();
    }

    public static Portal getByJSONString(String code) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(code);

            Location start = Location.getByJSONString((String) json.get("Start"));

            Destination destination;
            if(json.get("Destination") == null) destination = new Destination();
            else {
                try {
                    destination = new Destination((String) json.get("Destination"));
                } catch(Throwable ex) {
                    destination = new Destination((String) json.get("Destination"), DestinationType.EffectPortal);
                }
            }

            AnimationType animationType = AnimationType.valueOf((String) json.get("AnimationType"));
            double animationHeight = Double.parseDouble(json.get("AnimationHeight") + "");
            Particle particle = Particle.valueOf((String) json.get("Particle"));
            double teleportDistance = Double.parseDouble(json.get("TeleportRadius") + "");
            String startName = (String) json.get("StartName");
            String destinationName = json.get("DestinationName") == null ? null : (String) json.get("DestinationName");
            double hologramHeight = Double.parseDouble(json.get("HologramHeight") + "");
            Sound sound = Sound.valueOf((String) json.get("TeleportSound"));
            float soundVolume = Float.parseFloat(json.get("TeleportSoundVolume") + "");
            float soundPitch = Float.parseFloat(json.get("TeleportSoundPitch") + "");
            boolean startHoloStatus = json.get("StartHoloStatus") == null || Boolean.parseBoolean(json.get("StartHoloStatus") + "");
            boolean destinationHoloStatus = json.get("DestinationHoloStatus") == null || Boolean.parseBoolean(json.get("DestinationHoloStatus") + "");
            String permission = json.get("Permission") == null ? null : (String) json.get("Permission");

            Portal portal = new Portal(start, destination, animationType, animationHeight, particle, teleportDistance, startName, destinationName, new SoundData(sound, soundVolume, soundPitch), hologramHeight, startHoloStatus, destinationHoloStatus);
            portal.setPermission(permission);
            return portal;
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
