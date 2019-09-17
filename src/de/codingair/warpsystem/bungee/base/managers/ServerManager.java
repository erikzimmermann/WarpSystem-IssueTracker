package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.bungee.InitialPacket;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerManager implements Listener {
    private List<ServerInfo> onlineServer = new ArrayList<>();

    public List<ServerInfo> getOnlineServer() {
        return onlineServer;
    }

    public boolean isOnline(ServerInfo info) {
        return onlineServer.contains(info);
    }

    public void run() {
        BungeeCord.getInstance().getScheduler().schedule(WarpSystem.getInstance(), () -> {
            for(ServerInfo info : BungeeCord.getInstance().getServers().values()) {
                info.ping((serverPing, error) -> setStatus(info, error == null));
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void sendInitialPacket(ServerInfo server) {
        WarpSystem.getInstance().getDataHandler().send(new InitialPacket(WarpSystem.getInstance().getDescription().getVersion()), server);
    }

    public void setStatus(ServerInfo info, boolean online) {
        if(!online) this.onlineServer.remove(info);
        else if(!this.onlineServer.contains(info)) this.onlineServer.add(info);
    }
}
