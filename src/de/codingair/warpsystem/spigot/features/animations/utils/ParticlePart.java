package de.codingair.warpsystem.spigot.features.animations.utils;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.customanimations.AnimationType;
import de.codingair.codingapi.particles.animations.customanimations.CustomAnimation;
import de.codingair.codingapi.particles.animations.movables.MovableMid;
import de.codingair.codingapi.particles.utils.Color;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.Serializable;
import de.codingair.codingapi.tools.JSON.JSONObject;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ParticlePart implements Serializable {
    private AnimationType animation;
    private Particle particle;
    private Color color = Color.RED;
    private double radius, height;
    private int xRotation, yRotation, zRotation;
    private int speed = CustomAnimation.MAX_SPEED;

    public ParticlePart() {
    }

    public ParticlePart(ParticlePart part) {
        this(part.getAnimation(), part.getParticle(), part.getRadius(), part.getHeight(), part.getSpeed());
        this.xRotation = part.xRotation;
        this.yRotation = part.yRotation;
        this.zRotation = part.zRotation;
        this.color = part.color;
    }

    public ParticlePart(AnimationType animation, Particle particle, double radius, double height, int speed) {
        this.animation = animation;
        this.particle = particle;
        this.radius = radius;
        this.height = height;
        this.speed = speed;
    }

    @Override
    public boolean read(JSONObject json) {
        animation = AnimationType.getById(json.get("animation"));
        particle = Particle.getById(json.get("particle"));
        radius = json.get("radius");
        height = json.get("height");
        return true;
    }

    @Override
    public void write(JSONObject json) {
        json.put("animation", animation.getId());
        json.put("particle", particle.getId());
        json.put("height", height);
        json.put("radius", radius);
    }

    @Override
    public void destroy() {
    }

    public CustomAnimation build(Player player, MovableMid mid) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return animation == null ? null : animation.build(particle, player, mid, radius, height, speed).setXRotation(xRotation).setYRotation(yRotation).setZRotation(zRotation).setColor(color);
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
        if(this.height < -5) this.height = -5;
        if(this.height > 10) this.height = 10;
    }

    private double round(double d) {
        return ((double) Math.round(d * 10)) / 10;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        if(this.speed < CustomAnimation.MIN_SPEED) this.speed = CustomAnimation.MIN_SPEED;
        if(this.speed > CustomAnimation.MAX_SPEED) this.speed = CustomAnimation.MAX_SPEED;
    }

    public int getxRotation() {
        return xRotation;
    }

    public void setxRotation(int xRotation) {
        this.xRotation = xRotation;
    }

    public int getyRotation() {
        return yRotation;
    }

    public void setyRotation(int yRotation) {
        this.yRotation = yRotation;
    }

    public int getzRotation() {
        return zRotation;
    }

    public void setzRotation(int zRotation) {
        this.zRotation = zRotation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
