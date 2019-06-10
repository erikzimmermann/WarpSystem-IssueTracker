package de.codingair.warpsystem.spigot.features.animations.utils;

import de.codingair.codingapi.particles.animations.PlayerAnimation;
import de.codingair.codingapi.particles.animations.customanimations.CustomAnimation;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AnimationPlayer {
    private Player player;
    private Animation animation;

    private boolean loop = false;
    private int seconds;
    private boolean running = false;
    private BukkitRunnable runnable;
    private List<PlayerAnimation> animations = new ArrayList<>();
    private List<PotionEffect> buffBackup = new ArrayList<>();
    private Location destination;

    public AnimationPlayer(Player player, Animation animation, int seconds) {
        this(player, animation, seconds, null);
    }

    public AnimationPlayer(Player player, Animation animation, int seconds, Location destination) {
        this.player = player;
        this.animation = animation;
        this.seconds = seconds;
        this.destination = destination;
    }

    private void buildBuffBackup() {
        for(PotionEffect p : player.getActivePotionEffects()) {
            buffBackup.add(new PotionEffect(p.getType(), p.getDuration(), p.getAmplifier(), p.isAmbient(), p.hasParticles(), p.hasIcon()));
        }
    }

    private void removeActivePotionEffects() {
        for(PotionEffect p : player.getActivePotionEffects()) {
            player.removePotionEffect(p.getType());
        }
    }

    private void restoreBuffs() {
        for(PotionEffect potionEffect : this.buffBackup) {
            player.addPotionEffect(potionEffect);
        }
    }

    private void buildAnimations() {
        for(ParticlePart particlePart : this.animation.getParticleParts()) {
            try {
                CustomAnimation anim = particlePart.build(player);
                if(anim != null) {
                    this.animations.add(anim);
                }
            } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildRunnable() {
        this.runnable = new BukkitRunnable() {
            private int left = seconds;
            private String msg = Lang.get("Teleporting_Info");

            @Override
            public void run() {
                if(left > 0) {
                    for(Buff buff : animation.getBuffList()) {
                        if(buff.getTimeBeforeTeleport() == left || (left == seconds && buff.getTimeBeforeTeleport() > left)) {
                            player.addPotionEffect(new PotionEffect(buff.getType(), 20 * left + 20 * buff.getTimeAfterTeleport() * (buff.getTimeAfterTeleport() == 0 ? 10 : 1), buff.getLevel(), false, false, false));
                        }
                    }

                    if(animation.getTickSound() != null) animation.getTickSound().play(player);
                    if(!loop) MessageAPI.sendActionBar(player, msg.replace("%seconds%", left + ""));
                } else if(left == 0) {
                    if(!loop) MessageAPI.sendActionBar(player, null);

                    for(PlayerAnimation anim : animations) {
                        anim.setRunning(false);
                    }

                    for(Buff buff : animation.getBuffList()) {
                        if(buff.getTimeAfterTeleport() == 0) {
                            player.removePotionEffect(buff.getType());
                        } else if(buff.getTimeBeforeTeleport() == 0) {
                            player.addPotionEffect(new PotionEffect(buff.getType(), 20 * buff.getTimeAfterTeleport(), buff.getLevel(), false, false, false));
                        }
                    }

                    if(animation.getTeleportSound() != null) animation.getTeleportSound().play(player, destination);

                    if(player.getActivePotionEffects().isEmpty()) setRunning(false);
                } else {
                    for(Buff buff : animation.getBuffList()) {
                        if(buff.getTimeAfterTeleport() == -left) {
                            player.removePotionEffect(buff.getType());
                        }
                    }

                    if(player.getActivePotionEffects().isEmpty()) setRunning(false);
                }

                left--;
            }
        };
    }

    public void update() {
        if(running) {
            if(loop) setRunning(false);
            else {
                setRunning(false);
                setRunning(true);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if(this.animation != null) {
            if(this.running != running) {
                if(running) {
                    buildBuffBackup();
                    removeActivePotionEffects();
                    buildRunnable();
                    buildAnimations();

                    for(PlayerAnimation anim : this.animations) {
                        anim.setRunning(true);
                    }

                    this.runnable.runTaskTimer(WarpSystem.getInstance(), 0, 20);
                } else {
                    for(PlayerAnimation anim : this.animations) {
                        anim.setRunning(false);
                    }
                    this.runnable.cancel();
                    removeActivePotionEffects();
                    if(!loop) restoreBuffs();
                    if(!loop) buffBackup.clear();
                    animations.clear();

                    if(loop) {
                        this.running = running;
                        setRunning(true);
                        return;
                    }
                }
            }
        }

        this.running = running;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}
