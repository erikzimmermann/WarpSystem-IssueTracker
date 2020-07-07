package de.codingair.warpsystem.spigot.base.utils.teleport.v2;

import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import org.bukkit.entity.Player;

public abstract class TeleportStage {
    private TeleportStage previous, next;
    protected Teleport teleport;
    protected TeleportOptions options;
    protected Player player;
    private boolean active = false;

    protected TeleportStage(Teleport teleport) {
        this.teleport = teleport;
        this.options = teleport.getOptions();
        this.player = teleport.getPlayer();
    }

    protected TeleportStage() {
    }

    protected abstract void start();

    protected abstract void destroy();

    public TeleportStage then(TeleportStage stage) {
        this.next = stage;
        stage.previous = this;

        stage.teleport = teleport;
        stage.options = options;
        stage.player = player;
        return stage;
    }

    protected TeleportStage begin() {
        TeleportStage stage = first();
        stage.active = true;
        stage.start();
        return stage;
    }

    public boolean isBefore(Class<? extends TeleportStage> before) {
        TeleportStage stage = previous;

        while(stage != null) {
            if(before.isInstance(stage)) return true;
            stage = stage.previous;
        }

        return false;
    }

    public boolean isFired(Class<? extends TeleportStage> active) {
        TeleportStage stage = first();

        while(stage != null) {
            if(active.isInstance(stage)) return true;

            if(stage.active) break;
            stage = stage.next;
        }

        return false;
    }

    public TeleportStage active() {
        return first().activeOrNext();
    }

    private TeleportStage activeOrNext() {
        if(!active && next != null) return next.activeOrNext();
        else return this;
    }

    public TeleportStage first() {
        if(previous == null) return this;
        else return previous.first();
    }

    protected void end() {
        active = false;
        destroy();
        if(next != null) {
            next.active = true;
            next.start();
        } else options.fireCallbacks(Result.SUCCESS);
    }

    protected void cancel(Result result) {
        destroy();
        teleport.cancelByStage(result);
    }
}
