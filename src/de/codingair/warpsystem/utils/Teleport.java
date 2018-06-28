package de.codingair.warpsystem.utils;

import de.codingair.codingapi.particles.animations.Animation;
import de.codingair.codingapi.particles.animations.playeranimations.CircleAnimation;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Sound;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Warp;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Teleport {
    private Player player;
    private Warp warp;
    private Animation animation;
    private BukkitRunnable runnable;
    private Sound finishSound = Sound.ENDERMAN_TELEPORT;
    private Sound cancelSound = Sound.ITEM_BREAK;

    public Teleport(Player player, Warp warp) {
        this.player = player;
        this.warp = warp;
        this.animation = new CircleAnimation(WarpSystem.getInstance().getTeleportManager().getParticle(), player, WarpSystem.getInstance(), WarpSystem.getInstance().getTeleportManager().getRadius());
        this.runnable = new BukkitRunnable() {
            private int left = WarpSystem.getInstance().getTeleportManager().getSeconds();

            @Override
            public void run() {
                if(left == 0) {
                    teleport();
                    MessageAPI.sendActionBar(player, null);
                    return;
                }

                player.playSound(player.getLocation(), Sound.NOTE_PIANO.bukkitSound(), 1.5F, 0.5F);

                String msg = Lang.get("Teleporting_Info", new Example("ENG", "&cTeleport in §l§n%seconds%"), new Example("GER", "&cTeleport in &l&n%seconds%")).replace("%seconds%", left + "");

                MessageAPI.sendActionBar(player, msg);

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
            MessageAPI.sendActionBar(player, null);
        }
        if(sound) cancelSound.playSound(player);
    }

    public void teleport() {
        cancel(false);
        player.teleport(warp.getLocation());
        finishSound.playSound(player);

        WarpSystem.getInstance().getTeleportManager().getTeleports().remove(this);

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message", true)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Teleported_To", new Example("ENG", "&7You have been teleported to '&b%warp%&7'."), new Example("GER", "&7Du wurdest zu '&b%warp%&7' teleportiert.")).replace("%warp%", ChatColor.translateAlternateColorCodes('&', warp.getName())));
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Warp getTo() {
        return warp;
    }

    public Animation getAnimation() {
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