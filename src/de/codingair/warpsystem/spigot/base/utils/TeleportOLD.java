package de.codingair.warpsystem.spigot.base.utils;

import de.codingair.codingapi.particles.animations.Animation;
import de.codingair.codingapi.particles.animations.playeranimations.CircleAnimation;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.SpigotAPI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportOLD {
    private Player player;
    private Animation animation;
    private BukkitRunnable runnable;

    private Sound finishSound = Sound.ENDERMAN_TELEPORT;
    private Sound cancelSound = Sound.ITEM_BREAK;

    private long startTime = 0;

    private String globalWarpName;
    private Location location;

    private String displayName;
    private double costs;
    private boolean showMessage;
    private boolean canMove;
    private String message = null;
    private boolean silent = false;
    private Callback<Boolean> callback;

    public TeleportOLD(Player player, String displayName, double costs, boolean showMessage, boolean canMove) {
        this.player = player;
        this.displayName = displayName;
        this.costs = costs;
        this.showMessage = showMessage;
        this.canMove = canMove;

        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs)) this.costs = 0;

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

                String msg = Lang.get("Teleporting_Info").replace("%seconds%", left + "");

                MessageAPI.sendActionBar(player, msg);

                left--;
            }
        };
    }

    public TeleportOLD(Player player, String globalWarp, String displayName, double costs, boolean showMessage, boolean canMove) {
        this(player, displayName, costs, showMessage, canMove);
        this.globalWarpName = globalWarp;
    }

    public TeleportOLD(Player player, Sound finishSound, Sound cancelSound, String globalWarp, String displayName, double costs, boolean showMessage, boolean canMove) {
        this(player, globalWarp, displayName, costs, showMessage, canMove);
        this.finishSound = finishSound;
        this.cancelSound = cancelSound;
    }

    public TeleportOLD(Player player, Location location, String displayName, double costs, String message, boolean canMove, Callback<Boolean> callback) {
        this(player, displayName, costs, message != null && !message.isEmpty(), canMove);
        this.location = location;
        this.message = message;
        this.callback = callback;
    }

    public TeleportOLD(Player player, Location location, String displayName, double costs, String message, boolean canMove, boolean silent, Callback<Boolean> callback) {
        this(player, location, displayName, costs, message, canMove, callback);
        this.silent = silent;
    }

    public TeleportOLD(Player player, Location location, String displayName, double costs, boolean showMessage, boolean canMove) {
        this(player, displayName, costs, showMessage, canMove);
        this.location = location;
    }

    public TeleportOLD(Player player, Sound finishSound, Sound cancelSound, Location location, String displayName, double costs, boolean showMessage, boolean canMove) {
        this(player, location, displayName, costs, showMessage, canMove);
        this.finishSound = finishSound;
        this.cancelSound = cancelSound;
    }

    public void start() {
        if(!animation.isRunning()) {
            this.startTime = System.currentTimeMillis();
            this.animation.setRunning(true);
            this.runnable.runTaskTimer(WarpSystem.getInstance(), 0L, 20L);
        }
    }

    public void cancel(boolean sound, boolean finished) {
        if(animation.isRunning()) {
            this.startTime = 0;
            this.animation.setRunning(false);
            this.runnable.cancel();
            MessageAPI.sendActionBar(player, null);
        }
        if(sound && cancelSound != null) cancelSound.playSound(player);

        if(!finished) {
            if(AdapterType.getActive() != null) {
                AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) + this.costs);
            }
        }

        if(callback != null) callback.accept(false);
    }

    public void teleport() {
        cancel(false, true);

        if(location != null) {
            player.setFallDistance(0F);

            if(location.getWorld() == null) {
                getPlayer().sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
            } else {
                if(silent) SpigotAPI.getInstance().silentTeleport(player, location);
                else player.teleport(location);
            }

            WarpSystem.getInstance().getTeleportManager().getTeleports().remove(this);
        } else if(this.globalWarpName != null) {
            ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).teleport(getPlayer(), this.displayName, this.globalWarpName, this.costs, new Callback<PrepareTeleportPacket.Result>() {
                @Override
                public void accept(PrepareTeleportPacket.Result result) {
                    WarpSystem.getInstance().getTeleportManager().getTeleports().remove(TeleportOLD.this);

                    switch(result) {
                        case TELEPORTED:
                            if(callback != null) callback.accept(true);
                            break;

                        case WARP_NOT_EXISTS:
                            getPlayer().sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", globalWarpName));
                            break;

                        case SERVER_NOT_AVAILABLE:
                            getPlayer().sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Server_Is_Not_Online"));
                            break;

                        case PLAYER_ALREADY_ON_SERVER:
                            getPlayer().sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Player_Is_Already_On_Target_Server"));
                            break;
                    }

                    if(result != PrepareTeleportPacket.Result.TELEPORTED && AdapterType.getActive() != null && costs != 0) {
                        AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) + costs);
                    }
                }
            });

            return;
        }

//        WarpSystem.getInstance().getTeleportManager().playAfterEffects(player);
        if(finishSound != null) finishSound.playSound(player);
        if(message == null || message.isEmpty()) return;

        if(this.costs > 0) {
            player.sendMessage((message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName)));
        } else if(showMessage) {
            player.sendMessage((message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName)));
        }

        if(callback != null) callback.accept(true);
    }

    public Player getPlayer() {
        return player;
    }

    public Location getTo() {
        return this.location;
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

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public Location getLocation() {
        return location;
    }
}