package de.codingair.warpsystem.spigot.features.warps.guis.utils;

import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import org.bukkit.entity.Player;

public class Task {
    private Type type = Type.NONE;
    private Warp warp;

    public Task runWarp(Warp warp) {
        if(warp == null) return this;

        this.type = Type.RUN_WARP;
        this.warp = warp;

        return this;
    }

    public void runTask(Player player, boolean editing) {
        switch(this.type) {
            case RUN_WARP: {
                this.warp.perform(player, editing);
                break;
            }
        }
    }

    public Warp getWarp() {
        return warp;
    }

    private enum Type {
        NONE, RUN_WARP
    }
}
