package de.codingair.warpsystem.bungee.base.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.general.BooleanPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MainListener implements Listener, PacketListener {

    @EventHandler
    public void onConnect(ServerConnectedEvent e) {
        if(e.getServer().getInfo().getPlayers().size() <= 1) {
            //Update it
            WarpSystem.getInstance().getServerManager().sendInitialPacket(e.getServer().getInfo());
        }
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        ServerInfo server = BungeeCord.getInstance().getServerInfo(extra);

        switch(PacketType.getByObject(packet)) {
            case RequestInitialPacket: {
                WarpSystem.getInstance().getServerManager().sendInitialPacket(server);
                break;
            }
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
