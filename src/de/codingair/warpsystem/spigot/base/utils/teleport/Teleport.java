package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.reflections.PacketUtils;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.events.PlayerTeleportAcceptEvent;
import de.codingair.warpsystem.spigot.api.events.PlayerTeleportedEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.effects.RotatingParticleSpiral;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.base.utils.options.GeneralOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.AnimationPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Teleport {
    private Player player;
    private AnimationPlayer animation;
    private BukkitRunnable runnable;

    private Sound cancelSound = Sound.ITEM_BREAK;

    private long startTime = 0;

    private Destination destination;

    private String permission;
    private String displayName;
    private int seconds;
    private SoundData teleportSound;
    private double costs;
    private boolean canMove;
    private String message;
    private Origin origin;
    private boolean silent;
    private boolean afterEffects;
    private Callback<TeleportResult> callback;
    private List<Chunk> preLoadedChunks = null;

    public Teleport(Player player, Destination destination, Origin origin, String displayName, String permission, int seconds, double costs, String message, boolean canMove, boolean silent, SoundData teleportSound, boolean afterEffects, Callback<TeleportResult> callback) {
        this.player = player;
        this.destination = destination;
        this.origin = origin;
        this.displayName = displayName == null ? null : displayName.replace("_", " ");
        this.permission = permission;
        this.seconds = seconds;
        this.costs = costs;
        this.teleportSound = teleportSound;
        this.afterEffects = afterEffects;
        this.message = message;
        this.canMove = canMove;
        this.silent = silent;
        this.callback = callback;

        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs)) this.costs = 0;

        this.animation = new AnimationPlayer(player, AnimationManager.getInstance().getActive(), seconds, destination.buildLocation());
    }

    public void start() {
        if(!animation.isRunning()) {
            this.startTime = System.currentTimeMillis();
            this.animation.setRunning(true);
            this.runnable = new BukkitRunnable() {
                private int left = seconds;
                private String msg = Lang.get("Teleporting_Info");

                @Override
                public void run() {
                    if(left == 0) {
                        teleport();
                        MessageAPI.sendActionBar(player, null);
                        return;
                    }

                    MessageAPI.sendActionBar(player, msg.replace("%seconds%", left + ""));
                    left--;
                }
            };
            this.runnable.runTaskTimer(WarpSystem.getInstance(), 0L, 20L);
            preLoadChunks(-1);
        }
    }

    private void preLoadChunks(int radius) {
        GeneralOptions options = WarpSystem.getOptions(GeneralOptions.class);

        if((!options.isChunkPreLoadingLimitedByPerm() || getPlayer().hasPermission(WarpSystem.PERMISSION_TELEPORT_PRELOAD_CHUNKS)) && options.isChunkPreLoadEnabled() && this.destination != null) {
            radius = radius == -1 ? options.getChunkPreLoadRadius() : radius;
            Location l = this.destination.buildLocation();

            if(l == null) return;

            if(!getPlayer().getWorld().equals(l.getWorld()) ||
                    getPlayer().getLocation().distance(l) >= (Bukkit.getViewDistance() + 1) * 16) {
                preLoadedChunks = Environment.getChunks(l, radius);

                for(Chunk chunk : preLoadedChunks) {
                    if(!chunk.isLoaded()) chunk.load();
                }
            }
        }
    }

    private void sendLoadedChunks() {
        if(preLoadedChunks != null) {
            Class<?> packetClazz = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutMapChunk");
            Class<?> chunkClazz = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "Chunk");
            Class<?> craftChunkClazz = IReflection.getClass(IReflection.ServerPacket.CRAFTBUKKIT_PACKAGE, "CraftChunk");
            IReflection.MethodAccessor getHandle = IReflection.getMethod(craftChunkClazz, "getHandle", chunkClazz, new Class[] {});

            if(Version.getVersion().isBiggerThan(Version.v1_9)) {
                IReflection.ConstructorAccessor con = IReflection.getConstructor(packetClazz, chunkClazz, int.class);

                for(Chunk chunk : preLoadedChunks) {
                    Object packet = con.newInstance(getHandle.invoke(craftChunkClazz.cast(chunk)), 65535);
                    PacketUtils.sendPacket(getPlayer(), packet);
                }

                preLoadedChunks.clear();
                preLoadedChunks = null;
            } else {
                IReflection.ConstructorAccessor con = IReflection.getConstructor(packetClazz, chunkClazz, boolean.class, int.class);

                for(Chunk chunk : preLoadedChunks) {
                    Object packet = con.newInstance(getHandle.invoke(craftChunkClazz.cast(chunk)), true, 65535);
                    PacketUtils.sendPacket(getPlayer(), packet);
                }

                preLoadedChunks.clear();
                preLoadedChunks = null;
            }
        }
    }

    public void cancel(boolean sound, boolean finished) {
        if(runnable != null) {
            this.startTime = 0;
            this.runnable.cancel();
            this.runnable = null;
            MessageAPI.sendActionBar(player, null);
        }
        if(sound && cancelSound != null) cancelSound.playSound(player);

        if(!finished) {
            this.animation.setRunning(false);
            payBack();
            if(callback != null) callback.accept(TeleportResult.CANCELLED);
        }
    }

    public void teleport() {
        WarpSystem.getInstance().getTeleportManager().getTeleports().remove(this);

        cancel(false, true);
        if(destination == null) return;

        if(message != null) {
            if(this.costs > 0) {
                message = (message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName));
            } else {
                message = (message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName));
            }
        }

        if(seconds == 0) preLoadChunks(1);

        Location from = player.getLocation();

        if(!destination.teleport(player, message, displayName, this.permission == null, silent, costs, callback)) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onTeleportEd(PlayerTeleportAcceptEvent e) {
                if(player.isOnline()) {
                    sendLoadedChunks();

                    PlayerTeleportedEvent event = new PlayerTeleportedEvent(player, from, origin, afterEffects);
                    Bukkit.getPluginManager().callEvent(event);

                    if(event.isRunAfterEffects()) playAfterEffects(player);
                    if(teleportSound != null) teleportSound.play(player);
                }

                HandlerList.unregisterAll(this);
            }
        }, WarpSystem.getInstance());
    }

    private void payBack() {
        if(AdapterType.getActive() != null) {
            AdapterType.getActive().deposit(player, this.costs);
        }
    }

    public void playAfterEffects(Player player) {
        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Teleport.Animation_After_Teleport.Enabled", true)) {
            new RotatingParticleSpiral(player, player.getLocation()).runTaskTimer(WarpSystem.getInstance(), 1, 1);
        }
    }

    public SimulatedTeleportResult simulate(Player player) {
        if(this.destination == null) throw new IllegalArgumentException("Destination cannot be null!");
        if(this.permission != null && !this.permission.equals(TeleportManager.NO_PERMISSION) && !player.hasPermission(this.permission))
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"), TeleportResult.NO_PERMISSION);
        return this.destination.simulate(player, this.permission == null);
    }

    public Player getPlayer() {
        return player;
    }

    public Destination getDestination() {
        return destination;
    }

    public AnimationPlayer getAnimation() {
        return animation;
    }

    public BukkitRunnable getRunnable() {
        return runnable;
    }

    public SoundData getTeleportSound() {
        return teleportSound;
    }

    public void setTeleportSound(SoundData teleportSound) {
        this.teleportSound = teleportSound;
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

    public int getSeconds() {
        return seconds;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}