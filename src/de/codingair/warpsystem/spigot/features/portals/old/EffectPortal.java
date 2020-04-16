package de.codingair.warpsystem.spigot.features.portals.old;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.standalone.AnimationType;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.TeleportSoundPage;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.TeleportSoundAction;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class EffectPortal extends FeatureObject implements Removable {
    private final UUID uniqueId = UUID.randomUUID();

    public String name;
    public Location location, linkHelper;
    public EffectPortal link;
    public boolean useLink = false;

    private Animation animation;
    private boolean holoStatus;
    private String holoText;
    private Location holoPos;

    private SoundData teleportSound;

    public EffectPortal() {
        setSkip(true);
    }

    public Portal convert() {
        Portal portal = new Portal();
        portal.apply(this);

        portal.setDisplayName(this.name);
        portal.setSpawn(this.location);

        if(link != null) portal.setDestination(new Destination(link.name, DestinationType.Portal));

        EffectPortal data = useLink && link != null ? link : this;
        if(data.animation != null)
            for(ParticlePart particlePart : data.animation.getParticleParts()) {
                if(particlePart != null) portal.getAnimations().add(new de.codingair.warpsystem.spigot.features.portals.utils.Animation(particlePart, location));
            }

        portal.getHologram().setText(holoText);
        portal.getHologram().setLocation(holoPos);
        portal.getHologram().setHeight(0);
        portal.getHologram().setVisible(holoStatus);

        if(!TeleportSoundPage.isStandardSound(teleportSound)) portal.addAction(new TeleportSoundAction(teleportSound));

        return portal;
    }

    public boolean read(DataWriter d, EffectPortal link) throws Exception {
        super.read(d);

        if(d.get("skip") == null) setSkip(true);

        if(d.get("Destination") != null) {
            //old pattern

            Destination destination;
            try {
                destination = new Destination((String) d.get("Destination"));
            } catch(Throwable ex) {
                destination = new Destination(d.get("Destination"), DestinationType.Location);
            }

            if(destination.getType() == DestinationType.Location) addAction(new WarpAction(destination));
            else removeAction(Action.WARP);

            this.location = Location.getByJSONString(d.get("Start"));
            AnimationType animationType = AnimationType.valueOf(d.get("AnimationType"));
            double animationHeight = Double.parseDouble(d.get("AnimationHeight") + "");
            Particle particle = Particle.valueOf(d.get("Particle"));
            double teleportRadius = Double.parseDouble(d.get("TeleportRadius") + "");
            this.name = this.holoText = d.get("StartName");
            String destinationName = d.get("DestinationName");
            double hologramHeight = Double.parseDouble(d.get("HologramHeight") + "");
            Sound sound = Sound.valueOf(d.get("TeleportSound"));
            float soundVolume = Float.parseFloat(d.get("TeleportSoundVolume") + "");
            float soundPitch = Float.parseFloat(d.get("TeleportSoundPitch") + "");
            this.holoStatus = d.get("StartHoloStatus") == null || Boolean.parseBoolean(d.get("StartHoloStatus") + "");
            boolean destinationHoloStatus = d.get("DestinationHoloStatus") == null || Boolean.parseBoolean(d.get("DestinationHoloStatus") + "");
            setPermission(d.get("Permission") == null ? null : (String) d.get("Permission"));

            this.teleportSound = new SoundData(sound, soundVolume, soundPitch);

            Animation animation = new Animation(name);
            ParticlePart particles = new ParticlePart(animationType.getCustom(), particle, teleportRadius, animationHeight, 10);
            animation.getParticleParts().add(particles);

            AnimationManager.getInstance().addAnimation(animation);
            this.animation = AnimationManager.getInstance().getAnimation(name);

            if(hasDestinationPortal()) {
                Location destinationHoloPos = Location.getByLocation(getDestination().buildLocation().clone());
                destinationHoloPos.setY(destinationHoloPos.getY() + hologramHeight);
                destinationHoloPos.setYaw(0);
                destinationHoloPos.setPitch(0);

                link.apply(this);
                link.useLink = true;
                link.location = Location.getByLocation(getDestination().buildLocation());

                Destination dest = new Destination();
                dest.setId(location.toJSONString(4));
                dest.setType(DestinationType.Location);

                link.addAction(new WarpAction(dest));
                link.useLink = true;
                this.link = link;

                link.name = destinationName;
                link.holoStatus = destinationHoloStatus;
                link.holoPos = destinationHoloPos;
                link.holoText = destinationName;
            }

            holoPos = Location.getByLocation(location);
            holoPos.setY(holoPos.getY() + hologramHeight);
        } else if(d.get("start") != null) {
            this.location = Location.getByJSONString(d.getString("start"));

            AnimationType animationType = AnimationType.valueOf(d.getString("animationtype"));
            double animationHeight = Double.parseDouble(d.get("animationheight") + "");
            Particle particle = Particle.valueOf(d.get("particle"));
            double teleportRadius = Double.parseDouble(d.get("teleportradius") + "");
            this.name = this.holoText = d.get("startname");
            String destinationName = d.get("destinationname") == null ? null : (String) d.get("destinationname");
            this.holoStatus = d.get("startholostatus") == null || Boolean.parseBoolean(d.get("startholostatus") + "");
            boolean destinationHoloStatus = d.get("destinationholostatus") == null || Boolean.parseBoolean(d.get("destinationholostatus") + "");

            Sound sound = Sound.valueOf(d.get("teleportsound"));
            float soundVolume = Float.parseFloat(d.get("teleportsoundvolume") + "");
            float soundPitch = Float.parseFloat(d.get("teleportsoundpitch") + "");

            SoundData teleportSound = new SoundData(sound, soundVolume, soundPitch);

            Animation animation = new Animation(name);
            ParticlePart particles = new ParticlePart(animationType.getCustom(), particle, teleportRadius, animationHeight, 10);
            animation.getParticleParts().add(particles);

            this.teleportSound = teleportSound;

            AnimationManager.getInstance().addAnimation(animation);
            this.animation = AnimationManager.getInstance().getAnimation(name);

            double hologramHeight = Double.parseDouble(d.get("hologramheight") + "");
            if(hasDestinationPortal()) {
                Location destinationHoloPos = Location.getByLocation(getDestination().buildLocation().clone());
                destinationHoloPos.setY(destinationHoloPos.getY() + hologramHeight);
                destinationHoloPos.setYaw(0);
                destinationHoloPos.setPitch(0);

                link.apply(this);
                link.location = Location.getByLocation(getDestination().buildLocation());

                Destination dest = new Destination();
                dest.setId(location.toJSONString(4));

                link.addAction(new WarpAction(dest));
                link.useLink = true;
                this.link = link;

                link.name = destinationName;
                link.holoStatus = destinationHoloStatus;
                link.holoPos = destinationHoloPos;
                link.holoText = destinationName;
            }

            holoPos = Location.getByLocation(location);
            holoPos.setY(holoPos.getY() + hologramHeight);
        } else {
            if(d.getString("ep.anim.name") == null) {
                //link
                useLink = true;
            } else {
                this.animation = AnimationManager.getInstance().getAnimation(d.getString("ep.anim.name"));
                this.teleportSound = d.get("ep.sound.type") == null ? new SoundData(Sound.ENDERMAN_TELEPORT, 0.7F, 1F) : new SoundData(d.get("ep.sound.type", Sound.class), d.getFloat("ep.sound.volume"), d.getFloat("ep.sound.pitch"));
                linkHelper = d.getLocation("ep.link");
            }

            this.location = d.getLocation("ep.loc");
            this.name = d.getString("ep.name");
            this.holoText = d.getString("ep.holo.text");
            this.holoStatus = d.getBoolean("ep.holo.state");
            this.holoPos = d.getLocation("ep.holo.pos");
        }

        this.holoPos.setYaw(0);
        this.holoPos.setPitch(0);

        if(!useLink() && this.teleportSound != null && this.teleportSound.getSound() == null) this.teleportSound.setSound(Sound.ENDERMAN_TELEPORT);
        return true;
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public JavaPlugin getPlugin() {
        return WarpSystem.getInstance();
    }

    public boolean useLink() {
        return useLink && link != null;
    }

    public Destination getDestination() {
        return hasAction(Action.WARP) ? getAction(WarpAction.class).getValue() : null;
    }

    public boolean hasDestinationPortal() {
        return getDestination() != null && getDestination().getType() == DestinationType.Location;
    }
}
