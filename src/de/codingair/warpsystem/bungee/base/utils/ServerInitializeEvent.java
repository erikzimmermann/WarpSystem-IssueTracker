package de.codingair.warpsystem.bungee.base.utils;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

public class ServerInitializeEvent extends Event {
    private ServerInfo info;

    public ServerInitializeEvent(ServerInfo info) {
        this.info = info;
    }

    public ServerInfo getInfo() {
        return info;
    }
}
