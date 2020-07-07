package de.codingair.warpsystem.spigot.base.utils.teleport.v2;

import de.codingair.codingapi.particles.animations.movables.PlayerMid;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.AnimationPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportDelay extends TeleportStage {
    private AnimationPlayer animation;
    private BukkitRunnable runnable;

    protected TeleportDelay() {
    }

    @Override
    public void destroy() {
        if(this.animation != null) {
            this.animation.setRunning(false);
            this.animation = null;
        }
        
        if(this.runnable != null) {
            this.runnable.cancel();
            this.runnable = null;
        }
        
        MessageAPI.sendActionBar(player, null);
    }

    @Override
    public void start() {
        teleport.motionRestricted = true;
        int delay = teleport.getOptions().getDelay(player);
        if(delay == 0) {
            end();
            return;
        }
        
        boolean hasAnimation;
        if(hasAnimation = teleport.getOptions().isTeleportAnimation()) {
            this.animation = new AnimationPlayer(player, new PlayerMid(player), AnimationManager.getInstance().getActive(), delay, true, teleport.getOptions().isPublicAnimations());
            this.animation.setTeleportSound(false);
            this.animation.setRunning(true);
        }
        
        this.runnable = new BukkitRunnable() {
            private int left = delay;
            private final String msg = Lang.get("Teleporting_Info");

            @Override
            public void run() {
                if(left == 0) {
                    end();
                    return;
                }

                if(!hasAnimation && AnimationManager.getInstance().getActive().getTickSound() != null) AnimationManager.getInstance().getActive().getTickSound().play(player);
                MessageAPI.sendActionBar(player, msg.replace("%seconds%", left + ""));
                left--;
            }
        };
        
        this.runnable.runTaskTimer(WarpSystem.getInstance(), 0L, 20L);
    }

    @Override
    public void end() {
        teleport.motionRestricted = false;
        super.end();
    }
}
