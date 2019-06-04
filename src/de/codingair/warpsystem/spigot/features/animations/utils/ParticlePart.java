package de.codingair.warpsystem.spigot.features.animations.utils;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.playeranimations.CustomAnimation;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.Serializable;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;

public class ParticlePart implements Serializable {
    private AnimationType animation;
    private Particle particle;
    private double radius, height;

    public ParticlePart() {
    }

    public ParticlePart(AnimationType animation, Particle particle, double radius, double height) {
        this.animation = animation;
        this.particle = particle;
        this.radius = radius;
        this.height = height;
    }

    @Override
    public boolean read(JSONObject json) throws Exception {
        animation = AnimationType.getById(Integer.parseInt(json.get("animation") + ""));
        particle = Particle.getById(Integer.parseInt(json.get("particle") + ""));
        radius = Double.parseDouble(json.get("radius") + "");
        height = Double.parseDouble(json.get("height") + "");
        return true;
    }

    @Override
    public void write(JSONObject json) {
        json.put("animation", animation.getId());
        json.put("particle", particle.getId());
        json.put("radius", radius);
        json.put("height", height);
    }

    @Override
    public void destroy() {
    }

    public CustomAnimation build(Player player) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return animation == null ? null : animation.build(particle, player, radius, height);
    }

    public AnimationType getAnimation() {
        return animation;
    }

    public void setAnimation(AnimationType animation) {
        this.animation = animation;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = round(radius);
        if(this.radius < 0.1) this.radius = 0.1;
        if(this.radius > 3) this.radius = 3;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = round(height);
        if(this.height < 0) this.height = 0;
        if(this.height > 3) this.height = 3;
    }

    private double round(double d) {
        return ((double) Math.round(d * 10)) / 10;
    }
}
