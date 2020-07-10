package de.codingair.warpsystem.spigot.base.utils.teleport.v2;

import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;

public class SimulateStage extends TeleportStage {
    protected SimulateStage(Teleport teleport) {
        super(teleport);
    }

    @Override
    public void start() {
        if(options.getDestination() == null) throw new IllegalArgumentException("Destination cannot be null!");
        if(options.getPermission() != null && !options.getPermission().equals(TeleportManager.NO_PERMISSION) && !player.hasPermission(options.getPermission())) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"));
            cancel(Result.NO_PERMISSION);
            return;
        }

        SimulatedTeleportResult sim = this.options.getDestination().simulate(player, options.getPermission() == null);
        if(sim.getError() != null) {
            player.sendMessage(sim.getError());
            cancel(sim.getResult());
            return;
        }

        if(sim.getResult() == Result.NO_ADAPTER) {
            cancel(sim.getResult());
            return;
        }

        double costs = options.getCosts(player);
        if(costs > 0 && (!Bank.isReady() || Bank.adapter().getMoney(player) < costs)) {
            cancel(Result.NOT_ENOUGH_MONEY);
            return;
        }

        end();
    }

    @Override
    public void destroy() {

    }
}
