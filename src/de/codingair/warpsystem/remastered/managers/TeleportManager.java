package de.codingair.warpsystem.remastered.managers;

import de.CodingAir.v1_6.CodingAPI.Particles.Animations.CircleAnimation;
import de.CodingAir.v1_6.CodingAPI.Particles.Particle;
import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.codingair.warpsystem.remastered.WarpSystem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TeleportManager {
    private List<Teleport> teleports = new ArrayList<>();
    private boolean canMove = false;
    private int seconds = 5;
    private Particle particle = Particle.FLAME;
    private double radius = 1.5;

    public void teleport(Player player, Location to) {
        Teleport teleport = new Teleport(player, to);
        if(seconds == 0) teleport.teleport();
        else teleport.start();
    }

    public void cancelTeleport(Player p) {
        if(!isTeleporting(p)) return;

        Teleport teleport = getTeleport(p);
        teleport.cancel(true);
        this.teleports.remove(teleport);
    }

    public Teleport getTeleport(Player p) {
        for(Teleport teleport : teleports) {
            if(teleport.getPlayer().getName().equalsIgnoreCase(p.getName())) return teleport;
        }

        return null;
    }

    public boolean isTeleporting(Player p) {
        return getTeleport(p) != null;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
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
        this.radius = radius;
    }

    public class Teleport {
        private Player player;
        private Location to;
        private CircleAnimation animation;
        private BukkitRunnable runnable;
        private Sound finishSound = Sound.ENDERMAN_TELEPORT;
        private Sound cancelSound = Sound.ITEM_BREAK;

        public Teleport(Player player, Location to) {
            this.player = player;
            this.to = to;
            this.animation = new CircleAnimation(particle, player, WarpSystem.getInstance(), radius);
            this.runnable = new BukkitRunnable() {
                private int left = seconds;

                @Override
                public void run() {
                    if(left == 0) {
                        teleport();
                        return;
                    }

                    left--;
                }
            };
        }

        public void start() {
            this.animation.setRunning(true);
            this.runnable.runTaskTimer(WarpSystem.getInstance(), 0L, 20L);
        }

        public void cancel(boolean sound) {
            if(animation.isRunning()) {
                this.animation.setRunning(false);
                this.runnable.cancel();
            }
            if(sound) cancelSound.playSound(player);
        }

        public void teleport() {
            cancel(false);
            player.teleport(to);
            finishSound.playSound(player);
        }

        public Player getPlayer() {
            return player;
        }

        public Location getTo() {
            return to;
        }

        public CircleAnimation getAnimation() {
            return animation;
        }

        public BukkitRunnable getRunnable() {
            return runnable;
        }

        public Sound getFinishSound() {
            return finishSound;
        }

        public void setFinishSound(Sound finishSound) {
            this.finishSound = finishSound;
        }

        public Sound getCancelSound() {
            return cancelSound;
        }

        public void setCancelSound(Sound cancelSound) {
            this.cancelSound = cancelSound;
        }
    }
}
