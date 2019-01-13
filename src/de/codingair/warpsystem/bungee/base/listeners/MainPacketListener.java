package de.codingair.warpsystem.bungee.base.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

public class MainPacketListener implements PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        ServerInfo server = BungeeCord.getInstance().getServerInfo(extra);

        switch(PacketType.getByObject(packet)) {
            case RequestInitialPacket: {
                WarpSystem.getInstance().getServerManager().sendInitialPacket(server);
            }
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
