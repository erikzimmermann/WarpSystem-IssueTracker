package de.codingair.warpsystem.bungee.api;

import de.codingair.codingapi.tools.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class ServerSwitchAttemptEvent extends Event {
    private ProxiedPlayer player;
    private ServerInfo info;
    private Callback teleportFinisher;
    private boolean waitForCallback;

    public ServerSwitchAttemptEvent(ProxiedPlayer player, ServerInfo info, Callback teleportFinisher) {
        this.player = player;
        this.info = info;
        this.teleportFinisher = teleportFinisher;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public ServerInfo getInfo() {
        return info;
    }

    public Callback getTeleportFinisher() {
        return teleportFinisher;
    }

    public boolean isWaitForCallback() {
        return waitForCallback;
    }

    public void setWaitForCallback(boolean waitForCallback) {
        this.waitForCallback = waitForCallback;
    }
}
