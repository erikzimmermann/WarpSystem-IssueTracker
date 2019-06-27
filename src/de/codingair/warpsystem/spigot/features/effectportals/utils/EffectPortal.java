package de.codingair.warpsystem.spigot.features.effectportals.utils;


import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.standalone.AnimationType;
import de.codingair.codingapi.player.Hologram;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.HitBox;
import de.codingair.codingapi.tools.JSON.JSONObject;
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
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.animations.utils.AnimationPlayer;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import de.codingair.warpsystem.spigot.features.effectportals.managers.EffectPortalManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class EffectPortal extends FeatureObject implements Removable {
    private final UUID uniqueId = UUID.randomUUID();

    private String name;
    private Location location;
    private EffectPortal link;
    private boolean useLink = false;

    private Animation animation;
    private AnimationPlayer animPlayer;
    private boolean showAnimation = true;

    private Hologram hologram;
    private boolean holoStatus;
    private String holoText;
    private Location holoPos;

    private SoundData teleportSound;

    private boolean running = false;

    public EffectPortal() {
    }

    public EffectPortal(EffectPortal effectPortal) {
        apply(effectPortal);
        commitClonedActions();
    }

    public EffectPortal(Location location, Destination destination, Animation animation, String name, boolean holoStatus, String permission) {
        super(permission, false, new WarpAction(destination));

        this.name = name;
        this.location = location;

        this.animation = animation;
        this.teleportSound = new SoundData(Sound.ENDERMAN_TELEPORT, 0.7F, 1F);

        this.holoStatus = holoStatus;
        this.holoText = name;
        this.holoPos = location.clone().add(0, 1.7, 0);
    }

    public boolean entered(Entity entity, org.bukkit.Location from, org.bukkit.Location to) {
        HitBox hFrom;
        HitBox move;
        if(entity instanceof Player) {
            hFrom = new HitBox(from, 0.1, ((Player) entity).getEyeHeight());

            move = new HitBox(hFrom);
            move.addProperty(new HitBox(to, 0.1, ((Player) entity).getEyeHeight()));
        } else {
            hFrom = new HitBox(from, 0, 0);

            move = new HitBox(hFrom);
            move.addProperty(new HitBox(to, 0, 0));
        }

        return !entered(entity.getWorld(), hFrom) && entered(entity.getWorld(), move);
    }

    private boolean entered(World world, HitBox entity) {
        if(this.location == null || this.location.getWorld() == null || this.animPlayer == null) return false;
        if(!world.getName().equals(this.location.getWorld().getName())) return false;

        HitBox box = animPlayer.getHitBox();
        if(box == null) return false;

        return box.collides(entity);
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
            this.location = Location.getByJSONString(json.get("start"));

            AnimationType animationType = AnimationType.valueOf(json.get("animationtype"));
            double animationHeight = Double.parseDouble(json.get("animationheight") + "");
            Particle particle = Particle.valueOf(json.get("particle"));
            double teleportRadius = Double.parseDouble(json.get("teleportradius") + "");
            this.name = this.holoText = json.get("startname");
            String destinationName = json.get("destinationname") == null ? null : (String) json.get("destinationname");
            this.holoStatus = json.get("startholostatus") == null || Boolean.parseBoolean(json.get("startholostatus") + "");
            boolean destinationHoloStatus = json.get("destinationholostatus") == null || Boolean.parseBoolean(json.get("destinationholostatus") + "");

            Sound sound = Sound.valueOf(json.get("teleportsound"));
            float soundVolume = Float.parseFloat(json.get("teleportsoundvolume") + "");
            float soundPitch = Float.parseFloat(json.get("teleportsoundpitch") + "");

            SoundData teleportSound = new SoundData(sound, soundVolume, soundPitch);

            Animation animation = new Animation(name);
            ParticlePart particles = new ParticlePart(animationType.getCustom(), particle, teleportRadius, animationHeight, 10);
            animation.getParticleParts().add(particles);

            this.teleportSound = teleportSound;

            AnimationManager.getInstance().addAnimation(animation);
            this.animation = AnimationManager.getInstance().getAnimation(name);

            double hologramHeight = Double.parseDouble(json.get("hologramheight") + "");
            if(hasDestinationPortal()) {
                Location destinationHoloPos = Location.getByLocation(getDestination().buildLocation().clone());
                destinationHoloPos.setY(destinationHoloPos.getY() + hologramHeight);

                EffectPortal link = new EffectPortal();
                link.apply(this);
                link.setLocation(Location.getByLocation(getDestination().buildLocation()));

                Destination d = new Destination();
                d.setId(getLocation().toJSONString(4));
                d.setAdapter(new PortalDestinationAdapter());
                d.setType(DestinationType.EffectPortal);

                link.addAction(new WarpAction(d));
                setLink(link);

                link.setName(destinationName);
                link.setHoloStatus(destinationHoloStatus);
                link.setHoloPos(destinationHoloPos);
                link.setHoloText(destinationName);

                EffectPortalManager.getInstance().getEffectPortals().add(link);
            }

            holoPos = Location.getByLocation(location);
            holoPos.setY(holoPos.getY() + hologramHeight);
        } else {
            this.animation = AnimationManager.getInstance().getAnimation(json.get("ep.anim.name"));
            this.location = Location.getByJSONString(json.get("ep.loc"));
            this.name = json.get("ep.name");
            this.holoText = json.get("ep.holo.text");
            this.holoStatus = json.get("ep.holo.state");
            this.holoPos = json.get("ep.holo.pos") == null ? null : new Location((org.json.simple.JSONObject) json.get("ep.holo.pos"));
            this.useLink = json.get("ep.link.use");

            this.teleportSound = json.get("ep.sound.type") == null ? new SoundData(Sound.ENDERMAN_TELEPORT, 0.7F, 1F) : new SoundData(Sound.valueOf(json.get("ep.sound.type")), (float) (double) json.get((Object) "ep.sound.volume"), (float) (double) json.get((Object) "ep.sound.pitch"));

            Location link = json.get("ep.link") == null ? null : new Location((org.json.simple.JSONObject) json.get("ep.link"));

            if(link != null) {
                setLink(EffectPortalManager.getInstance().getPortal(link));
            }
        }

        return true;
    }

    @Override
    public void write(JSONObject json) {
        super.write(json);

        json.put("ep.anim.name", this.animation == null ? null : this.animation.getName());
        json.put("ep.loc", this.location.toJSONString(4));
        json.put("ep.name", this.name);
        json.put("ep.holo.state", this.holoStatus);
        json.put("ep.holo.text", this.holoText);
        json.put("ep.holo.pos", this.holoPos.toJSON(4));
        json.put("ep.link", this.link == null ? null : this.link.getLocation().toJSON(4));
        json.put("ep.link.use", useLink);
        json.put("ep.sound.type", teleportSound.getSound().name());
        json.put("ep.sound.volume", teleportSound.getVolume());
        json.put("ep.sound.pitch", teleportSound.getPitch());
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        if(object instanceof EffectPortal) {
            EffectPortal ep = (EffectPortal) object;

            this.name = ep.name;
            this.location = ep.location;
            this.link = ep.link;

            this.animation = ep.animation;
            this.animPlayer = ep.animPlayer;
            this.showAnimation = ep.showAnimation;

            this.hologram = ep.hologram;
            this.holoStatus = ep.holoStatus;
            this.holoText = ep.holoText;
            this.holoPos = ep.holoPos;

            this.teleportSound = ep.teleportSound;
        }
    }

    public void add(Player player) {
        this.hologram.addPlayer(player);
    }

    public void remove(Player player) {
        this.hologram.removePlayer(player);
    }

    public void update() {
        if(isDisabled()) return;
        updateAnimations();
        updateHolograms();
    }

    public void updateHolograms() {
        if(isDisabled()) return;

        if(holoText != null) {
            if(this.hologram == null)
                this.hologram = new Hologram((this.holoPos == null ? this.location : this.holoPos).clone(), WarpSystem.getInstance(), ChatColor.translateAlternateColorCodes('&', holoText.replace("_", " ")));
            else {
                this.hologram.teleport((this.holoPos == null ? this.location : this.holoPos));
                this.hologram.setText(ChatColor.translateAlternateColorCodes('&', holoText.replace("_", " ")));
            }
            this.hologram.setVisible(this.holoStatus);
        } else {
            if(this.hologram != null) {
                this.hologram.destroy();
                this.hologram = null;
            }
        }

        if(this.hologram != null) {
            this.hologram.update();
            this.hologram.addAll();
        }
    }

    public void updateAnimations() {
        if(isDisabled()) return;
        if(this.animPlayer != null && this.animPlayer.isRunning()) this.animPlayer.setRunning(false);
        if(getAnimation() == null || !showAnimation()) return;

        this.animPlayer = new AnimationPlayer(location, getAnimation());

        this.animPlayer.setMaxDistance(EffectPortalManager.getInstance().getMaxParticleDistance());
        this.animPlayer.setRunning(isRunning());
    }

    @Override
    public void destroy() {
        setRunning(false);
    }

    public void unregister() {
        EffectPortalManager.getInstance().getEffectPortals().remove(this);
        if(link != null && !useLink()) link.unregister();
        setLink(null);
        destroy();
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Destination getDestination() {
        return hasAction(Action.WARP) ? getAction(WarpAction.class).getValue() : null;
    }

    public boolean isRunning() {
        return useLink() ? link.isRunning() : running;
    }

    public void setRunning(boolean running) {
        if(useLink()) link.setRunning(running);
        if(this.running == running || isDisabled()) return;
        this.running = running;

        update();

        if(this.animPlayer != null) this.animPlayer.setRunning(isRunning());

        if(this.hologram != null) {
            this.hologram.setVisible(this.holoStatus && isRunning());
            this.hologram.update();
        }

        if(isRunning()) {
            if(this.hologram != null) this.hologram.addAll();
        } else {
            if(this.hologram != null) this.hologram.removeAll();
        }

        if(isRunning()) API.addRemovable(this);
        else API.removeRemovable(this);
    }

    private double round(double d) {
        return ((double) (int) Math.round(d * 10)) / 10;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updateHolograms();
    }

    public boolean hasDestinationPortal() {
        return getDestination() != null && getDestination().getType() == DestinationType.EffectPortal;
    }

    @Override
    public FeatureObject perform(Player player) {
        if(getDestination() == null || getDestination().getId() == null) return this;
        return super.perform(player, this.name, getAction(WarpAction.class).getValue(), getTeleportSound(), true, !(getDestination().getAdapter() instanceof PortalDestinationAdapter));
    }

    public boolean isRegistered() {
        EffectPortalManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
        return manager.getEffectPortals().contains(this);
    }

    public ItemStack getIcon() {
        return new ItemBuilder(Material.ENDER_PEARL).setName(ChatColor.GRAY + "\"" + ChatColor.RESET + this.name + ChatColor.GRAY + "\"" + ChatColor.GRAY).getItem();
    }

    public boolean isHoloStatus() {
        return holoStatus;
    }

    public void setHoloStatus(boolean holoStatus) {
        this.holoStatus = holoStatus;
    }

    public double getRelHoloHeight() {
        return getHoloPos().getY() - getLocation().getY();
    }

    public Animation getAnimation() {
        if(useLink()) {
            return link.animation;
        } else return animation;
    }

    public void setAnimation(Animation animation) {
        if(useLink()) {
            link.setAnimation(animation);
        } else this.animation = animation;
        updateAnimations();
    }

    public boolean showAnimation() {
        if(useLink()) {
            return link.showAnimation;
        } else return showAnimation;
    }

    public void setShowAnimation(boolean showAnimation) {
        if(useLink()) {
            link.setShowAnimation(showAnimation);
            updateAnimations();
        } else if(this.showAnimation != showAnimation) {
            this.showAnimation = showAnimation;
            updateAnimations();
        }
    }

    public String getHoloText() {
        return holoText;
    }

    public void setHoloText(String holoText) {
        this.holoText = holoText;
        this.updateHolograms();
    }

    @Override
    public FeatureObject setDisabled(boolean disabled) {
        if(useLink()) {
            return link.setDisabled(disabled);
        } else return super.setDisabled(disabled);
    }

    @Override
    public boolean isDisabled() {
        if(useLink()) {
            return link.isDisabled();
        } else return super.isDisabled();
    }

    public Location getHoloPos() {
        return holoPos;
    }

    public void setHoloPos(Location holoPos) {
        this.holoPos = holoPos;
        this.updateHolograms();
    }

    public EffectPortal getLink() {
        return link;
    }

    public void setLink(EffectPortal link) {
        if(link != null) {
            link.link = this;
            this.link = link;
        } else if(this.link != null) {
            this.link.link = null;
            this.link = null;
        }
    }

    public boolean useLink() {
        return useLink && link != null;
    }

    public void setUseLink(boolean useLink) {
        this.useLink = useLink;
    }

    public SoundData getTeleportSound() {
        if(useLink()) {
            return link.teleportSound;
        } else return this.teleportSound;
    }

    public void setTeleportSound(SoundData teleportSound) {
        if(useLink()) {
            link.teleportSound = teleportSound;
        } else this.teleportSound = teleportSound;
    }
}
