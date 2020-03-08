package de.codingair.warpsystem.spigot.features.playerwarps.listeners;

import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.serializeable.Serializable;
import de.codingair.warpsystem.transfer.utils.PacketListener;

import java.util.List;

public class PlayerWarpListener implements PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.SendPlayerWarpsPacket) {
            List<Serializable> l = ((SendPlayerWarpsPacket) packet).getSerializables();
            System.out.println("Got UploadPlayerWarpsPacket with " + l.size() + " warps!");

            for(Serializable s : l) {
                PlayerWarp w = (PlayerWarp) s;
                PlayerWarpManager.getManager().updateWarp(w);
            }

            System.out.println("    ...updated");
            PlayerWarpManager.getManager().save(false);
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
