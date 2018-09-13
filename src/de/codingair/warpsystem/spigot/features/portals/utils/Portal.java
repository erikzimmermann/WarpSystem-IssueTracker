package de.codingair.warpsystem.spigot.features.portals.utils;


import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.Animation;
import de.codingair.codingapi.particles.animations.standalone.*;
import de.codingair.codingapi.player.Hologram;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.base.language.Example;
import de.codingair.warpsystem.spigot.base.language.Lang;
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
    private Location destination;

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
        this(portal.getStart(), portal.getDestination(), portal.getAnimationType(), portal.getAnimationHeight(), portal.getParticle(), portal.getTeleportRadius(), portal.getStartName(), portal.getDestinationName(), portal.getTeleportSound(), 2.2, portal.isStartHoloStatus(), portal.isDestinationHoloStatus());

        this.hologramHeight = portal.getHologramHeight();
        this.permission = portal.getPermission();
    }

    public Portal(Location start, Location destination, AnimationType animationType, double animationHeight, Particle particle, double teleportRadius, String startName, String destinationName, SoundData teleportSound, double
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

        update();
    }

    public void update(Player player) {
        this.startHolo.update(player);
        this.destinationHolo.update(player);
    }

    public void applyAttrs(Portal portal) {
        this.start = portal.getStart().clone();
        this.destination = portal.getDestination().clone();
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
        if(this.startHolo != null && this.startHolo.isVisible()) this.startHolo.hide();
        if(this.destinationHolo != null && this.destinationHolo.isVisible()) this.destinationHolo.hide();

        this.startHolo = new Hologram(this.start.clone().add(0, this.hologramHeight, 0), WarpSystem.getInstance(), ChatColor.translateAlternateColorCodes('&', startName));
        this.destinationHolo = new Hologram(this.destination.clone().add(0, this.hologramHeight, 0), WarpSystem.getInstance(), ChatColor.translateAlternateColorCodes('&', destinationName));

        if(this.startHoloStatus && running) this.startHolo.show();
        if(this.destinationHoloStatus && running) this.destinationHolo.show();
    }

    public void updateAnimations() {
        if(this.disabled) return;
        if(this.startAnim != null && this.startAnim.isRunning()) this.startAnim.setRunning(false);
        if(this.destinationAnim != null && this.destinationAnim.isRunning()) this.destinationAnim.setRunning(false);

        switch(animationType) {
            case SINUS: {
                this.startAnim = new SinusAnimation(particle, start, this.teleportRadius, this.animationHeight);
                this.destinationAnim = new SinusAnimation(particle, destination, this.teleportRadius, this.animationHeight);
                break;
            }

            case CIRCLE: {
                this.startAnim = new CircleAnimation(particle, this.teleportRadius, this.animationHeight, start);
                this.destinationAnim = new CircleAnimation(particle, this.teleportRadius, this.animationHeight, destination);
                break;
            }

            case ROTATING_CIRCLE: {
                this.startAnim = new RotatingCircleAnimation(particle, this.teleportRadius, this.animationHeight, start);
                this.destinationAnim = new RotatingCircleAnimation(particle, this.teleportRadius, this.animationHeight, destination);
                break;
            }

            case PULSING_CIRCLE: {
                this.startAnim = new PulsingCircleAnimation(particle, this.teleportRadius, this.animationHeight, start, 15);
                this.destinationAnim = new PulsingCircleAnimation(particle, this.teleportRadius, this.animationHeight, destination, 15);
                break;
            }
        }

        this.startAnim.setRunning(running);
        this.destinationAnim.setRunning(running);
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

    public Location getDestination() {
        return destination;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if(this.running == running || this.disabled) return;

        update();

        if(this.startAnim != null && destinationAnim != null) {
            this.startAnim.setRunning(running);
            this.destinationAnim.setRunning(running);


            if(running) {
                if(this.startHoloStatus) this.startHolo.show();
                if(this.destinationHoloStatus) this.destinationHolo.show();
            } else {
                this.startHolo.hide();
                this.destinationHolo.hide();
            }
        }

        this.running = running;

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
        updateHolograms();
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
        updateHolograms();
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
            player.sendMessage(Lang.getPrefix() + Lang.get("Portal_Insufficient_Permissions", new Example("ENG", "&cYou have insufficient permissions to teleport with this portal."), new Example("GER", "&cDu hast nicht genug Berechtigungen, um dich mit diesem Portal zu teleportieren.")));
            return;
        }

        player.teleport(this.start);
        update(player);

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.Portals", true)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Teleported_To").replace("%warp%", this.startName));
        }

        if(this.teleportSound != null) this.teleportSound.play(player);
    }

    public void teleportToDestination(Player player) {
        if(this.permission != null && !player.hasPermission(this.permission)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Portal_Insufficient_Permissions", new Example("ENG", "&cYou have insufficient permissions to teleport with this portal."), new Example("GER", "&cDu hast nicht genug Berechtigungen, um dich mit diesem Portal zu teleportieren.")));
            return;
        }

        player.teleport(this.destination);
        update(player);

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.Portals", true)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Teleported_To").replace("%warp%", this.destinationName));
        }

        if(this.teleportSound != null) this.teleportSound.play(player);
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
        json.put("Destination", this.destination.toJSONString(4));
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

        return json.toJSONString();
    }

    public static Portal getByJSONString(String code) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(code);

            Location start = Location.getByJSONString((String) json.get("Start"));
            Location destination = Location.getByJSONString((String) json.get("Destination"));
            AnimationType animationType = AnimationType.valueOf((String) json.get("AnimationType"));
            double animationHeight = Double.parseDouble(json.get("AnimationHeight") + "");
            Particle particle = Particle.valueOf((String) json.get("Particle"));
            double teleportDistance = Double.parseDouble(json.get("TeleportRadius") + "");
            String messageToStart = (String) json.get("StartName");
            String messageToDestination = (String) json.get("DestinationName");
            double hologramHeight = Double.parseDouble(json.get("HologramHeight") + "");
            Sound sound = Sound.valueOf((String) json.get("TeleportSound"));
            float soundVolume = Float.parseFloat(json.get("TeleportSoundVolume") + "");
            float soundPitch = Float.parseFloat(json.get("TeleportSoundPitch") + "");
            boolean startHoloStatus = json.get("StartHoloStatus") == null || Boolean.parseBoolean(json.get("StartHoloStatus") + "");
            boolean destinationHoloStatus = json.get("DestinationHoloStatus") == null || Boolean.parseBoolean(json.get("DestinationHoloStatus") + "");

            return new Portal(start, destination, animationType, animationHeight, particle, teleportDistance, messageToStart, messageToDestination, new SoundData(sound, soundVolume, soundPitch), hologramHeight, startHoloStatus, destinationHoloStatus);
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
