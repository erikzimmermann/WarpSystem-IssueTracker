package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ImprovedDouble;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.api.events.PlayerGlobalWarpEvent;
import de.codingair.warpsystem.spigot.api.events.PlayerWarpEvent;
import de.codingair.warpsystem.spigot.api.events.utils.GlobalWarp;
import de.codingair.warpsystem.spigot.api.events.utils.Warp;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.base.utils.options.specific.GeneralOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.*;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TeleportManager {
    public static final String NO_PERMISSION = "%NO_PERMISSION%";
    private List<Teleport> teleports = new ArrayList<>();
    private GeneralOptions options;

    /**
     * Have to be launched after the IconManager (see WarpSign.class - fromJSONString method - need warps and categories)
     */
    public boolean load() {
        boolean success = true;

        this.options = WarpSystem.getOptions(GeneralOptions.class);
        return success;
    }

    public void save() {
        WarpSystem.getInstance().getFileManager().getFile("Config").saveConfig();
    }

    @Deprecated
    public void teleport(Player player, Origin origin, Destination destination, String displayName, double costs) {
        TeleportOptions options = new TeleportOptions(destination, displayName);
        options.setOrigin(origin);
        options.setCosts(costs);
        options.setCanMove(this.options.isAllowMove());
        options.setMessage(costs > 0 && MoneyAdapterType.getActive() != null && !player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs) ?
                Lang.getPrefix() + Lang.get("Money_Paid")
                : Lang.getPrefix() + Lang.get("Teleported_To"));

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

        if(options.getDestination() == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            return;
        }

        if((options.getDestination().getType() == DestinationType.GlobalWarp || options.getDestination().getType() == DestinationType.Server) && !WarpSystem.getInstance().isOnBungeeCord()) {
            options.runCallbacks(TeleportResult.NOT_ON_BUNGEE_CORD);
            player.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
            return;
        }

        int seconds = this.options.getTeleportDelay();
        Callback<TeleportResult> resultCallback;

        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Delay)) seconds = 0;
        String message = options.getFinalMessage(player);

        //Call events
        if(options.getDestination().getType() == DestinationType.GlobalWarp) {
            String name = GlobalWarpManager.getInstance().getCaseCorrectlyName(options.getDestination().getId());
            String server = GlobalWarpManager.getInstance().getGlobalWarps().get(name);

            PlayerGlobalWarpEvent event = new PlayerGlobalWarpEvent(player, new GlobalWarp(name, server), options.getOrigin(), options.getDisplayName(), message, seconds, options.getCosts());
            event.setWaitForTeleport(options.isWaitForTeleport());
            Bukkit.getPluginManager().callEvent(event);

            if(event.isCancelled()) {
                options.runCallbacks(TeleportResult.CANCELLED_BY_SYSTEM);
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
                options.runCallbacks(TeleportResult.CANCELLED_BY_SYSTEM);
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
        Callback<?> waiting = new Callback() {
            @Override
            public void accept(Object object) {
                Teleport teleport = new Teleport(player, finalSeconds, options, new Callback<TeleportResult>() {
                    @Override
                    public void accept(TeleportResult object) {
                        options.runCallbacks(object);
                        if(finalResultCallback != null) finalResultCallback.accept(object);

                        options.destroy();
                    }
                }).setVelocity(options.getVelocity());

                SimulatedTeleportResult simulated = teleport.simulate(player);
                if(simulated.getError() != null) {
                    player.sendMessage(simulated.getError());
                    options.runCallbacks(simulated.getResult());
                    return;
                }

                if(simulated.getResult() == TeleportResult.NO_ADAPTER) {
                    options.runCallbacks(simulated.getResult());
                    return;
                }

                try {
                    player.closeInventory();
                } catch(Throwable ignored) {
                }

                if(options.getFinalCosts(player).doubleValue() > 0) {
                    double bank = MoneyAdapterType.getActive().getMoney(player);

                    if(bank < options.getFinalCosts(player).doubleValue()) {
                        options.runCallbacks(TeleportResult.NOT_ENOUGH_MONEY);
                        player.sendMessage(Lang.getPrefix() + Lang.get("Not_Enough_Money").replace("%AMOUNT%", options.getFinalCosts(player).toString()));
                        return;
                    }

                    if(options.isConfirmPayment()) {
                        //true = accept; false = timeOut/deny
                        confirmPayment(player, options.getFinalCosts(player).doubleValue(), new Callback<Boolean>() {
                            @Override
                            public void accept(Boolean confirm) {
                                if(confirm) {
                                    //pay
                                    teleports.add(teleport);
                                    if(finalSeconds == 0 || options.isSkip()) teleport.teleport();
                                    else teleport.start();
                                } else {
                                    options.runCallbacks(TeleportResult.DENIED_PAYMENT);
                                    if(options.getPaymentDeniedMessage(player) != null) player.sendMessage(options.getPaymentDeniedMessage(player));
                                }
                            }
                        });
                        return;
                    }

                    teleports.add(teleport);
                    MoneyAdapterType.getActive().withdraw(player, options.getFinalCosts(player).doubleValue());
                } else teleports.add(teleport);

                if(finalSeconds == 0 || options.isSkip()) teleport.teleport();
                else teleport.start();
            }
        };

        if(options.isWaitForTeleport() && !options.isCanMove() && finalSeconds > 0 || (options.isConfirmPayment() && options.getFinalCosts(player).doubleValue() > 0)) {
            waitWhileWalking(player, waiting);
        } else waiting.accept(null);
    }

    public static void confirmPayment(Player player, double costs, Callback<Boolean> callback) {
        Value<Listener> listenerValue = new Value<>(null);

        Callback<Boolean> confirmation = new Callback<Boolean>() {
            @Override
            public void accept(Boolean confirm) {
                MessageAPI.sendTitle(player, "§e" + Lang.get("Sneak_to_confirm"), "§6" + Lang.get("Costs") + ": §7" + new ImprovedDouble(costs) + " " + Lang.get("Coins"), 0, 0, 5);
                HandlerList.unregisterAll(listenerValue.getValue());

                if(confirm) {
                    //pay
                    MoneyAdapterType.getActive().withdraw(player, costs);
                } else {
                    //deny
                    Sound.ENTITY_ITEM_BREAK.playSound(player, 0.7F, 0.9F);
                }

                if(callback != null) callback.accept(confirm);
            }
        };

        int timeOut = 10 * 20;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                confirmation.accept(false);
            }
        };

        listenerValue.setValue(new Listener() {
            @EventHandler
            public void onWalk(PlayerWalkEvent e) {
                if(e.getPlayer().equals(player)) {
                    //deny!
                    runnable.cancel();
                    confirmation.accept(false);
                    HandlerList.unregisterAll(this);
                }
            }

            @EventHandler
            public void onToggleSneak(PlayerToggleSneakEvent e) {
                if(e.getPlayer().equals(player) && e.isSneaking()) {
                    //confirm!
                    runnable.cancel();
                    confirmation.accept(true);
                    HandlerList.unregisterAll(this);
                }
            }
        });
        Bukkit.getPluginManager().registerEvents(listenerValue.getValue(), WarpSystem.getInstance());

        runnable.runTaskLater(WarpSystem.getInstance(), timeOut + 5); //title fadeIn = 5
        Sound.BLOCK_NOTE_BLOCK_HARP.playSound(player, 0.7F, 1.1F);
        MessageAPI.sendTitle(player, "§e" + Lang.get("Sneak_to_confirm"), "§6" + Lang.get("Costs") + ": §7" + new ImprovedDouble(costs) + " " + Lang.get("Coins"), 5, timeOut, 5);
    }

    public static void waitWhileWalking(Player player, Callback callback) {
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
                    MessageAPI.stopSendingActionBar(player);
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

    public List<Teleport> getTeleports() {
        return teleports;
    }

    public GeneralOptions getOptions() {
        return options;
    }
}
