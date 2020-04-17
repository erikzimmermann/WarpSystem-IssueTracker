package de.codingair.warpsystem.spigot.features.portals.utils;

import de.codingair.codingapi.particles.animations.customanimations.CustomAnimation;
import de.codingair.codingapi.particles.animations.movables.LocationMid;
import de.codingair.codingapi.tools.HitBox;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;

import java.lang.reflect.InvocationTargetException;

public class Animation implements Serializable {
    private ParticlePart effect;
    private CustomAnimation animation;
    private Location location;
    private HitBox hitBox = null;

    public Animation() {
    }

    public Animation(Animation animation) {
        this.effect = new ParticlePart(animation.effect);
        this.location = animation.location.clone();
    }

    public Animation(ParticlePart effect, Location location) {
        this.effect = effect;
        this.location = location;
    }

    public HitBox getHitBox() {
        if(this.animation == null) return null;

        if(hitBox == null) {
            HitBox box = animation.getHitBox();
            if(hitBox == null) hitBox = box;
            else hitBox.addProperty(box);
        }

        return hitBox;
    }

    public boolean isVisible() {
        return this.animation != null && this.animation.isRunning();
    }

    public void setVisible(boolean visible) {
        if(!visible && this.animation == null) return;
        if(visible && this.animation == null) {
            try {
                this.animation = effect.build(null, new LocationMid(this.location));
                this.animation.setMaxDistance(PortalManager.getInstance().getMaxParticleDistance());
            } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        this.animation.setRunning(visible);
    }

    public void update() {
        boolean visible = isVisible();
        if(visible) setVisible(false);
        this.animation = null;
        if(visible) setVisible(true);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.effect = new ParticlePart();
        this.effect.read(d);
        this.location = new Location();
        this.location.read(d);
        return true;
    }

    @Override
    public void write(DataWriter d) {
        this.effect.write(d);
        this.location.write(d);
    }

    @Override
    public void destroy() {
        this.effect.destroy();
        this.location.destroy();
        if(this.animation != null) this.animation.setRunning(false);
    }

    public ParticlePart getEffect() {
        return effect;
    }

    public CustomAnimation getAnimation() {
        return animation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
