package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.reflections.PacketUtils;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ImprovedDouble;
import de.codingair.warpsystem.spigot.api.events.PlayerTeleportAcceptEvent;
import de.codingair.warpsystem.spigot.api.events.PlayerTeleportedEvent;
import de.codingair.warpsystem.spigot.api.events.PreTeleportAttemptEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.TeleportSoundPage;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.effects.RotatingParticleSpiral;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.base.utils.options.specific.GeneralOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.UnmodifiableDestination;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.AnimationPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
    private boolean teleportAnimation;
    private Callback<TeleportResult> callback;
    private List<Chunk> preLoadedChunks = null;
    private Vector velocity = null;

    public Teleport(Player player, Destination destination, Origin origin, String displayName, String permission, int seconds, double costs, String message, boolean canMove, boolean silent, SoundData teleportSound, boolean afterEffects, boolean teleportAnimation, Callback<TeleportResult> callback) {
        this.player = player;
        this.destination = destination;
        this.origin = origin;
        this.displayName = displayName == null ? null : displayName.replace("_", " ");
        this.permission = permission;
        this.seconds = seconds;
        this.costs = costs;
        this.teleportSound = teleportSound;
        if(this.teleportSound == null) this.teleportSound = AnimationManager.getInstance().getActive().getTeleportSound();
        if(this.teleportSound == null) this.teleportSound = TeleportSoundPage.createStandard();
        this.afterEffects = afterEffects;
        this.teleportAnimation = teleportAnimation;
        this.message = message;
        this.canMove = canMove;
        this.silent = silent;
        this.callback = callback;

        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs)) this.costs = 0;

        if(this.teleportAnimation) {
            this.animation = new AnimationPlayer(player, AnimationManager.getInstance().getActive(), seconds, destination.buildLocation());
            this.animation.setTeleportSound(false);
        }
    }

    public void start() {
        if(startTime == 0) {
            //Starting timer and call PreTeleportAttemptEvent
            PreTeleportAttemptEvent e = new PreTeleportAttemptEvent(this.player, new Callback() {
                private boolean used = false;

                @Override
                public void accept(Object object) {
                    if(used) return;
                    used = true;

                    MessageAPI.sendActionBar(player, null);
                    teleport();
                }
            }, new UnmodifiableDestination(this.destination));
            Bukkit.getPluginManager().callEvent(e);

            this.startTime = System.currentTimeMillis();
            if(teleportAnimation) this.animation.setRunning(true);
            this.runnable = new BukkitRunnable() {
                private int left = seconds;
                private String msg = Lang.get("Teleporting_Info");

                @Override
                public void run() {
                    if(left == 0) {
                        if(e.isWaitForCallback()) {
                            MessageAPI.sendActionBar(player, e.getHotbarMessage(), WarpSystem.getInstance(), Integer.MAX_VALUE);
                        } else {
                            e.getTeleportFinisher().accept(null);
                        }
                        return;
                    }

                    if(!teleportAnimation && AnimationManager.getInstance().getActive().getTickSound() != null) AnimationManager.getInstance().getActive().getTickSound().play(player);
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
            this.runnable.cancel();
            this.runnable = null;
            MessageAPI.sendActionBar(player, null);
        }
        if(sound && cancelSound != null) cancelSound.playSound(player);

        if(!finished) {
            if(teleportAnimation) this.animation.setRunning(false);
            payBack();
            if(callback != null) callback.accept(TeleportResult.CANCELLED);
        }

        this.startTime = 0;
    }

    public void teleport() {
        WarpSystem.getInstance().getTeleportManager().getTeleports().remove(this);

        Callback<?> teleport = new Callback<Object>() {
            private boolean used = false;

            @Override
            public void accept(Object object) {
                if(used) return;
                used = true;

                MessageAPI.stopSendingActionBar(player);
                cancel(false, true);
                if(destination == null) return;

                if(message != null) {
                    message = (message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", new ImprovedDouble(costs) + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName));
                }

                if(seconds == 0) preLoadChunks(1);

                Location from = player.getLocation();
                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
                    public void onTeleport(PlayerTeleportEvent e) {
                        if(player.equals(e.getPlayer()) && e.isCancelled()) {
                            player.sendMessage(Lang.getPrefix() + Lang.get("Teleport_Cancelled"));
                            HandlerList.unregisterAll(this);
                        }
                    }

                    @EventHandler
                    public void onTeleported(PlayerTeleportAcceptEvent e) {
                        if(player.equals(e.getPlayer())) {
                            if(player.isOnline()) {
                                sendLoadedChunks();

                                PlayerTeleportedEvent event = new PlayerTeleportedEvent(player, from, origin, afterEffects);
                                Bukkit.getPluginManager().callEvent(event);

                                destination.sendMessage(player, message, displayName, costs);
                                if(event.isRunAfterEffects()) playAfterEffects(player);
                                if(teleportSound != null) teleportSound.play(player);
                                if(velocity != null) player.setVelocity(velocity);

                                HandlerList.unregisterAll(this);
                            }
                        }
                    }

                    @EventHandler
                    public void onQuit(PlayerQuitEvent e) {
                        if(player.equals(e.getPlayer())) HandlerList.unregisterAll(this);
                    }
                }, WarpSystem.getInstance());

                if(!destination.teleport(player, message, displayName, permission == null, silent, costs, callback)) return;
            }
        };

        if(this.runnable == null) {
            //no timer set, call PreTeleportAttemptEvent
            PreTeleportAttemptEvent e = new PreTeleportAttemptEvent(this.player, teleport, new UnmodifiableDestination(this.destination));
            Bukkit.getPluginManager().callEvent(e);

            if(e.isWaitForCallback()) {
                MessageAPI.sendActionBar(player, e.getHotbarMessage(), WarpSystem.getInstance(), Integer.MAX_VALUE);
                return;
            }
        }

        teleport.accept(null);
    }

    private void payBack() {
        if(MoneyAdapterType.getActive() != null) {
            MoneyAdapterType.getActive().deposit(player, this.costs);
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

    public BukkitRunnable getRunnable() {
        return runnable;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isCanMove() {
        return canMove;
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

    public Teleport setVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }
}