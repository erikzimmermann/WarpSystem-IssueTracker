package de.codingair.warpsystem.bungee.features.playerwarps.listeners;

import de.codingair.warpsystem.bungee.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.bungee.features.playerwarps.utils.PlayerWarp;
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
                PlayerWarpManager.getInstance().updateWarp(w);
            }

            System.out.println("    ...updated");
            PlayerWarpManager.getInstance().save(false);
        } else if(packet.getType() == PacketType.RegisterServerForPlayerWarps) {
//            PlayerWarpManager.getInstance()
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
