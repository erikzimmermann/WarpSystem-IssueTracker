package de.codingair.warpsystem.spigot.features.randomteleports.listeners;

import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import de.codingair.warpsystem.spigot.features.randomteleports.packets.QueueRTPUsagePacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;

import java.util.List;
import java.util.UUID;

public class BungeePacketListener implements PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.QueueRTPUsagePacket) {
            QueueRTPUsagePacket p = (QueueRTPUsagePacket) packet;

            List<UUID> l = p.getIds();
            for(UUID uuid : l) {
                RandomTeleporterManager.getInstance().increaseTeleports(uuid);
            }
            l.clear();
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
