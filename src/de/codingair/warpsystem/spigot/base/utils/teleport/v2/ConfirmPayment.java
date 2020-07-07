package de.codingair.warpsystem.spigot.base.utils.teleport.v2;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ImprovedDouble;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmPayment extends TeleportStage {
    protected ConfirmPayment() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {
        double costs = options.getCosts(player);
        confirm(player, costs, new Callback<Result>() {
            @Override
            public void accept(Result result) {
                if(result == Result.SUCCESS) end();
                else cancel(result);
            }
        });
    }

    public static void confirm(Player player, double costs, Callback<Result> callback) {
        if(costs <= 0) {
            callback.accept(Result.SUCCESS);
            return;
        }

        if(!MoneyAdapterType.canEnable() || MoneyAdapterType.getActive().getMoney(player) < costs) {
            callback.accept(Result.NOT_ENOUGH_MONEY);
            return;
        }

        Value<Listener> listenerValue = new Value<>(null);

        Callback<Result> confirmation = new Callback<Result>() {
            @Override
            public void accept(Result result) {
                MessageAPI.sendTitle(player, "§e" + Lang.get("Sneak_to_confirm"), "§6" + Lang.get("Costs") + ": §7" + new ImprovedDouble(costs) + " " + Lang.get("Coins"), 0, 0, 5);
                HandlerList.unregisterAll(listenerValue.getValue());

                if(MoneyAdapterType.getActive().getMoney(player) < costs) result = Result.NOT_ENOUGH_MONEY;

                if(result == Result.SUCCESS) {
                    //pay
                    MoneyAdapterType.getActive().withdraw(player, costs);
                } else {
                    //deny
                    Sound.ENTITY_ITEM_BREAK.playSound(player, 0.7F, 0.9F);
                }

                if(callback != null) callback.accept(result);
            }
        };

        int timeOut = 10 * 20;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                confirmation.accept(Result.DENIED_PAYMENT);
            }
        };

        listenerValue.setValue(new Listener() {
            @EventHandler
            public void onWalk(PlayerWalkEvent e) {
                if(e.getPlayer().equals(player)) {
                    //deny!
                    runnable.cancel();
                    confirmation.accept(Result.DENIED_PAYMENT);
                    HandlerList.unregisterAll(this);
                }
            }

            @EventHandler
            public void onToggleSneak(PlayerToggleSneakEvent e) {
                if(e.getPlayer().equals(player) && e.isSneaking()) {
                    //confirm!
                    runnable.cancel();
                    confirmation.accept(Result.SUCCESS);
                    HandlerList.unregisterAll(this);
                }
            }
        });
        Bukkit.getPluginManager().registerEvents(listenerValue.getValue(), WarpSystem.getInstance());

        runnable.runTaskLater(WarpSystem.getInstance(), timeOut + 5); //title fadeIn = 5
        Sound.BLOCK_NOTE_BLOCK_HARP.playSound(player, 0.7F, 1.1F);
        MessageAPI.sendTitle(player, "§e" + Lang.get("Sneak_to_confirm"), "§6" + Lang.get("Costs") + ": §7" + new ImprovedDouble(costs) + " " + Lang.get("Coins"), 5, timeOut, 5);
    }
}
