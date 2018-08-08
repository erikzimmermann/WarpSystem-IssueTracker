package de.codingair.warpsystem.spigot.utils;

import de.codingair.codingapi.particles.animations.Animation;
import de.codingair.codingapi.particles.animations.playeranimations.CircleAnimation;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.gui.affiliations.Warp;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
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
    private long startTime = 0;
    private String globalWarpName;
    private String globalWarpDisplayName;

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

    public Teleport(Player player, String globalWarpName, String globalWarpDisplayName) {
        this.player = player;
        this.globalWarpName = globalWarpName;
        this.globalWarpDisplayName = globalWarpDisplayName;
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
        if(!animation.isRunning()) {
            this.startTime = System.currentTimeMillis();
            this.animation.setRunning(true);
            this.runnable.runTaskTimer(WarpSystem.getInstance(), 0L, 20L);
        }
    }

    public void cancel(boolean sound) {
        if(animation.isRunning()) {
            this.startTime = 0;
            this.animation.setRunning(false);
            this.runnable.cancel();
            MessageAPI.sendActionBar(player, null);
        }
        if(sound) cancelSound.playSound(player);
    }

    public void teleport() {
        cancel(false);

        String warpName = warp == null ? globalWarpName : warp.getName();

        WarpSystem.getInstance().getTeleportManager().getTeleports().remove(this);

        if(warp != null) player.teleport(warp.getLocation());
        else {
            WarpSystem.getInstance().getGlobalWarpManager().teleport(getPlayer(), globalWarpDisplayName, warpName, new Callback<PrepareTeleportPacket.Result>() {
                @Override
                public void accept(PrepareTeleportPacket.Result result) {
                    switch(result) {
                        case WARP_NOT_EXISTS:
                            getPlayer().sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists", new Example("ENG", "&7The GlobalWarp '&b%GLOBAL_WARP%&7' &cdoes not exist&7."), new Example("GER", "&7Der GlobalWarp '&b%GLOBAL_WARP%&7' &cexistiert nicht&7.")).replace("%GLOBAL_WARP%", globalWarpName));
                            break;

                        case SERVER_NOT_AVAILABLE:
                            getPlayer().sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Server_Is_Not_Online", new Example("ENG", "&cThe target server is not online!"), new Example("GER", "&cDer Ziel-Server ist nicht online!")));
                            break;

                        case PLAYER_ALREADY_ON_SERVER:
                            getPlayer().sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Player_Is_Already_On_Target_Server", new Example("ENG", "&cYou are already on the target server."), new Example("GER", "&cDu befindest dich bereits auf dem Ziel-Server.")));
                            break;
                    }
                }
            });

            return;
        }

        finishSound.playSound(player);

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

    public long getStartTime() {
        return startTime;
    }
}