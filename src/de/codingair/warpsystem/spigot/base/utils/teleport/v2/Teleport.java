package de.codingair.warpsystem.spigot.base.utils.teleport.v2;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Teleport {
    private final Player player;
    private final TeleportOptions options;
    private TeleportStage stage;
    private long started = 0;
    protected boolean motionRestricted = false;

    public Teleport(Player player, TeleportOptions options) {
        this.player = player;
        this.options = options;
    }

    public Teleport start() {
        started = System.currentTimeMillis();
        Value<Location> afterEffectPosition = new Value<>(player.getLocation());

        options.addCallback(new Callback<Result>() {
            @Override
            public void accept(Result result) {
                if(stage != null && result != Result.SUCCESS && stage.active().isBefore(ConfirmPayment.class) && Bank.adapter() != null) {
                    //payback
                    double costs = options.getCosts(player);
                    if(costs > 0) Bank.adapter().deposit(player, costs);
                }
            }
        });

        stage = new SimulateStage(this)
                .then(new WaitForTeleport())
                .then(new ConfirmPayment())
                .then(new TeleportDelay())
                .then(new PlayerTeleport(afterEffectPosition))
                .then(new AfterEffects(afterEffectPosition))
                .begin();
        return this;
    }

    public void cancel(Result result) {
        if(this.stage != null) this.stage.active().cancel(result);
        cancelByStage(result);
    }

    public void cancelByStage(Result result) {
        if(options.getCancelSound() != null && stage != null && stage.active().isFired(TeleportDelay.class)) options.getCancelSound().play(player);
        options.fireCallbacks(result);

        if(result == Result.NOT_ENOUGH_MONEY) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Not_enough_Money").replace("%AMOUNT%", options.getFinalCosts(player).toString()));
        }

        if(result == Result.DENIED_PAYMENT) {
            if(options.getPaymentDeniedMessage(player) != null) player.sendMessage(options.getPaymentDeniedMessage(player));
        }
    }

    public boolean expired() {
        return options.expired();
    }

    public Player getPlayer() {
        return player;
    }

    public TeleportOptions getOptions() {
        return options;
    }

    public long getStartTime() {
        return started;
    }

    public boolean isCanMove() {
        return options.isCanMove() || !motionRestricted;
    }

    public Destination getDestination() {
        return options.getDestination();
    }
}
