package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.events.PlayerGlobalWarpEvent;
import de.codingair.warpsystem.spigot.api.events.PlayerWarpEvent;
import de.codingair.warpsystem.spigot.api.events.utils.GlobalWarp;
import de.codingair.warpsystem.spigot.api.events.utils.Warp;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.options.specific.GeneralOptions;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.*;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TeleportManager {
    public static final String NO_PERMISSION = "%NO_PERMISSION%";
    private List<Particle> particles = new ArrayList<>();
    private List<Teleport> teleports = new ArrayList<>();

    private GeneralOptions options;

    public TeleportManager() {
        particles.add(Particle.FIREWORKS_SPARK);
        particles.add(Particle.SUSPENDED_DEPTH);
        particles.add(Particle.CRIT);
        particles.add(Particle.CRIT_MAGIC);
        particles.add(Particle.SMOKE_NORMAL);
        particles.add(Particle.SMOKE_LARGE);
        particles.add(Particle.SPELL);
        particles.add(Particle.SPELL_INSTANT);
        particles.add(Particle.SPELL_MOB);
        particles.add(Particle.SPELL_WITCH);
        particles.add(Particle.DRIP_WATER);
        particles.add(Particle.DRIP_LAVA);
        particles.add(Particle.VILLAGER_ANGRY);
        particles.add(Particle.VILLAGER_HAPPY);
        particles.add(Particle.TOWN_AURA);
        particles.add(Particle.NOTE);
        particles.add(Particle.ENCHANTMENT_TABLE);
        particles.add(Particle.FLAME);
        particles.add(Particle.CLOUD);
        particles.add(Particle.REDSTONE);
        particles.add(Particle.SNOW_SHOVEL);
        particles.add(Particle.HEART);
        particles.add(Particle.PORTAL);
    }

    /**
     * Have to be launched after the IconManager (see WarpSign.class - fromJSONString method - need warps and categories)
     */
    public boolean load() {
        boolean success = true;

        this.options = WarpSystem.getOptions(GeneralOptions.class);
        return success;
    }

    public void save(boolean saver) {
        WarpSystem.getInstance().getFileManager().getFile("Config").saveConfig();
    }

    public void teleport(Player player, Location location, String displayName, boolean afterEffects) {
        teleport(player, Origin.Custom, location, displayName, afterEffects);
    }

    public void teleport(Player player, Origin origin, Location location, String displayName, boolean afterEffects) {
        teleport(player, origin, location, displayName, afterEffects, true);
    }

    public void teleport(Player player, Origin origin, Location location, String displayName, boolean afterEffects, boolean skip) {
        teleport(player, origin, new Destination(new LocationAdapter(location)), displayName, null, 0, skip, skip, true, false, afterEffects, null);
    }

    public void teleport(Player player, Origin origin, Location location, String displayName, boolean afterEffects, boolean skip, Callback<TeleportResult> callBack) {
        teleport(player, origin, new Destination(new LocationAdapter(location)), displayName, null, 0, skip, skip, true, false, afterEffects, callBack);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs) {
        teleport(player, origin, destination, displayName, costs, null);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean message) {
        teleport(player, origin, destination, displayName, costs, false, this.options.isAllowMove(), message, false, null);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean message) {
        teleport(player, origin, destination, displayName, permission, costs, false, this.options.isAllowMove(), message, false, null);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean message, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, false, this.options.isAllowMove(), message, false, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean message, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, false, this.options.isAllowMove(), message, false, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, false, this.options.isAllowMove(), true, false, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, this.options.isAllowMove(), message, silent, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean canMove, boolean message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, null, costs, skip, canMove, message, silent, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, boolean message, boolean silent, boolean afterEffects, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, canMove, message, silent, new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F), afterEffects, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, boolean message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, canMove, message, silent, new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F), callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, boolean message, boolean silent, SoundData soundData, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, canMove, message, silent, soundData, true, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, boolean message, boolean silent, SoundData soundData, boolean afterEffects, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, canMove, message ?
                costs > 0 && MoneyAdapterType.getActive() != null && !player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs) ?
                        Lang.getPrefix() + Lang.get("Money_Paid")
                        : Lang.getPrefix() + Lang.get("Teleported_To")
                : null, silent, soundData, afterEffects, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, String message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, this.options.isAllowMove(), message, silent, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean canMove, String message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, canMove, message, silent, new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F), callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean canMove, String message, boolean silent, SoundData teleportSound, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, costs, skip, canMove, message, silent, teleportSound, true, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs, boolean skip, boolean canMove, String message, boolean silent, SoundData teleportSound, boolean afterEffects, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, null, costs, skip, canMove, message, silent, teleportSound, true, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, String message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, this.options.isAllowMove(), message, silent, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, String message, boolean silent, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, canMove, message, silent, new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F), callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, String message, boolean silent, SoundData teleportSound, Callback<TeleportResult> callback) {
        teleport(player, origin, destination, displayName, permission, costs, skip, canMove, message, silent, teleportSound, true, callback);
    }

    public void teleport(Player player, Origin origin, Destination destination, String displayName, String permission, double costs, boolean skip, boolean canMove, String message, boolean silent, SoundData teleportSound, boolean afterEffects, Callback<TeleportResult> callback) {
        TeleportOptions options = new TeleportOptions(destination, displayName);
        options.setOrigin(origin);
        options.setPermission(permission);
        options.setCosts(costs);
        options.setSkip(skip);
        options.setCanMove(canMove);
        options.setMessage(message);
        options.setSilent(silent);
        options.setTeleportSound(teleportSound);
        options.setAfterEffects(afterEffects);
        options.setCallback(callback);

        teleport(player, options);
    }

    public void teleport(Player player, TeleportOptions options) {
        if(WarpSystem.maintenance && !player.hasPermission(WarpSystem.PERMISSION_ByPass_Maintenance)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Warning_Maintenance"));
            return;
        }

        if(isTeleporting(player)) {
            Teleport teleport = getTeleport(player);
            long diff = System.currentTimeMillis() - teleport.getStartTime();
            if(diff > 50)
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting"));
            return;
        }

        if((options.getDestination().getType() == DestinationType.GlobalWarp || options.getDestination().getType() == DestinationType.Server) && !WarpSystem.getInstance().isOnBungeeCord()) {
            if(options.getCallback() != null) options.getCallback().accept(TeleportResult.NOT_ON_BUNGEE_CORD);
            player.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
            return;
        }

        int seconds = this.options.getTeleportDelay();
        Callback<TeleportResult> resultCallback;

        if(!options.isNoDelayByPass() && player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Delay)) seconds = 0;
        String message = options.getFinalMessage(player);

        //Call events
        if(options.getDestination().getType() == DestinationType.GlobalWarp) {
            String name = GlobalWarpManager.getInstance().getCaseCorrectlyName(options.getDestination().getId());
            String server = GlobalWarpManager.getInstance().getGlobalWarps().get(name);

            PlayerGlobalWarpEvent event = new PlayerGlobalWarpEvent(player, new GlobalWarp(name, server), options.getOrigin(), options.getDisplayName(), message, seconds, options.getCosts());
            event.setWaitForTeleport(options.isWaitForTeleport());
            Bukkit.getPluginManager().callEvent(event);

            if(event.isCancelled()) {
                if(options.getCallback() != null) options.getCallback().accept(TeleportResult.CANCELLED_BY_SYSTEM);
                if(event.getTeleportResultCallback() != null) event.getTeleportResultCallback().accept(TeleportResult.CANCELLED_BY_SYSTEM);
                return;
            }

            resultCallback = event.getTeleportResultCallback();
            options.setCosts(event.getCosts());
            options.setWaitForTeleport(event.isWaitForTeleport());
            options.setDisplayName(event.getDisplayName());
            message = event.getMessage();
            seconds = event.getSeconds();
        } else {
            PlayerWarpEvent event = new PlayerWarpEvent(player, new Warp(options.getDestination().buildLocation(), options.getDestination().getId(), options.getDestination().getType()), options.getOrigin(), options.getDisplayName(), message, seconds, options.getCosts());
            event.setWaitForTeleport(options.isWaitForTeleport());
            Bukkit.getPluginManager().callEvent(event);

            if(event.isCancelled()) {
                if(options.getCallback() != null) options.getCallback().accept(TeleportResult.CANCELLED_BY_SYSTEM);
                if(event.getTeleportResultCallback() != null) event.getTeleportResultCallback().accept(TeleportResult.CANCELLED_BY_SYSTEM);
                return;
            }

            resultCallback = event.getTeleportResultCallback();
            options.setCosts(event.getCosts());
            options.setWaitForTeleport(event.isWaitForTeleport());
            options.setDisplayName(event.getDisplayName());
            message = event.getMessage();
            seconds = event.getSeconds();
        }

        final int finalSeconds = seconds;
        Callback<TeleportResult> finalResultCallback = resultCallback;
        String finalMessage = message;
        Callback waiting = new Callback() {
            @Override
            public void accept(Object object) {
                Teleport teleport = new Teleport(player, options.getDestination(), options.getOrigin(), options.getDisplayName(), options.getPermission(), finalSeconds, options.getCosts(), finalMessage, options.isCanMove(), options.isSilent(), options.getTeleportSound(), options.isAfterEffects(), new Callback<TeleportResult>() {
                    @Override
                    public void accept(TeleportResult object) {
                        if(options.getCallback() != null) options.getCallback().accept(object);
                        if(finalResultCallback != null) finalResultCallback.accept(object);
                    }
                });

                SimulatedTeleportResult simulated = teleport.simulate(player);
                if(simulated.getError() != null) {
                    player.sendMessage(simulated.getError());
                    if(options.getCallback() != null) options.getCallback().accept(simulated.getResult());
                    return;
                }

                if(simulated.getResult() == TeleportResult.NO_ADAPTER) {
                    if(options.getCallback() != null) options.getCallback().accept(simulated.getResult());
                    return;
                }

                try {
                    player.closeInventory();
                } catch(Throwable ignored) {
                }

                if(options.getFinalCosts(player) > 0) {
                    double bank = MoneyAdapterType.getActive().getMoney(player);

                    if(bank < options.getCosts()) {
                        if(options.getCallback() != null) options.getCallback().accept(TeleportResult.NOT_ENOUGH_MONEY);
                        player.sendMessage(Lang.getPrefix() + Lang.get("Not_Enough_Money").replace("%AMOUNT%", (options.getCosts() % ((int) options.getCosts()) == 0 ? (int) options.getCosts() : options.getCosts()) + ""));
                        return;
                    }

                    teleports.add(teleport);
                    MoneyAdapterType.getActive().withdraw(player, options.getCosts());
                } else teleports.add(teleport);

                if(finalSeconds == 0 || options.isSkip()) teleport.teleport();
                else teleport.start();
            }
        };

        if(options.isWaitForTeleport() && !options.isCanMove() && finalSeconds > 0) {
            waitWhileWalking(player, waiting);
        } else waiting.accept(null);
    }

    public void waitWhileWalking(Player player, Callback callback) {
        BukkitRunnable runnable = new BukkitRunnable() {
            int notMoving = 0;
            int shakeTicks = 0;
            boolean shake = false;
            Location location = player.getLocation();

            @Override
            public void run() {
                if(location.getWorld() == player.getWorld() && location.distance(player.getLocation()) <= 0.2) {
                    if(notMoving < 0) notMoving = 0;
                    notMoving++;
                } else {
                    if(notMoving > 0) notMoving = 0;
                    else notMoving--;
                    location = player.getLocation();
                    MessageAPI.sendActionBar(player, "§7» " + (shake ? " " : "") + Lang.get("Teleport_Stop_Moving") + (shake ? " " : "") + " §7«");

                    if(shakeTicks == 3) {
                        shakeTicks = 0;
                        shake = !shake;
                    } else shakeTicks++;
                }

                if(notMoving == 2) {
                    this.cancel();
                    callback.accept(null);
                }
            }
        };

        runnable.runTaskTimer(WarpSystem.getInstance(), 2, 2);
    }

    public void cancelTeleport(Player p) {
        if(!isTeleporting(p)) return;

        Teleport teleport = getTeleport(p);
        teleport.cancel(true, false);
        this.teleports.remove(teleport);

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Cancel_Message", true)) {
            MessageAPI.sendActionBar(p, Lang.get("Teleport_Cancelled"));
        }
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

    public List<Particle> getParticles() {
        return particles;
    }

    public List<Teleport> getTeleports() {
        return teleports;
    }

    public GeneralOptions getOptions() {
        return options;
    }
}
