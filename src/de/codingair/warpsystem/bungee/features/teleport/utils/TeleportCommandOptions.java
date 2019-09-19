package de.codingair.warpsystem.bungee.features.teleport.utils;

public class TeleportCommandOptions {
    private int options;

    public TeleportCommandOptions(int options) {
        this.options = options;
    }

    public boolean isBungeeCord() {
        return (options & 1) != 0;
    }

    public boolean isBack() {
        return (options & (1 << 1)) != 0;
    }

    public boolean isTp() {
        return (options & (1 << 2)) != 0;
    }

    public boolean isTpAll() {
        return (options & (1 << 3)) != 0;
    }

    public boolean isTpToggle() {
        return (options & (1 << 4)) != 0;
    }

    public boolean isTpa() {
        return (options & (1 << 5)) != 0;
    }

    public boolean isTpaHere() {
        return (options & (1 << 6)) != 0;
    }

    public boolean isTpaAll() {
        return (options & (1 << 7)) != 0;
    }

    public boolean isTpaToggle() {
        return (options & (1 << 8)) != 0;
    }
}
