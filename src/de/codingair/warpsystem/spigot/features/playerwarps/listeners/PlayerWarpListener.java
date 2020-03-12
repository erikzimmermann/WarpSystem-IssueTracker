package de.codingair.warpsystem.spigot.features.playerwarps.listeners;

import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpData;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpUpdate;
import de.codingair.warpsystem.transfer.packets.bungee.DeletePlayerWarpPacket;
import de.codingair.warpsystem.transfer.packets.bungee.SendPlayerWarpOptionsPacket;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpUpdatePacket;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerWarpListener implements PacketListener, Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerWarpManager.getManager().checkPlayerWarpOwnerNames(e.getPlayer());
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.SendPlayerWarpsPacket) {
            List<PlayerWarpData> l = ((SendPlayerWarpsPacket) packet).getData();

            for(PlayerWarpData s : l) {
                PlayerWarp w = new PlayerWarp();
                w.setData(s);
                PlayerWarpManager.getManager().updateWarp(w);
                s.destroy();
            }

            l.clear();

        } else if(packet.getType() == PacketType.SendPlayerWarpUpdatesPacket) {
            PlayerWarpUpdate update = ((SendPlayerWarpUpdatePacket) packet).getUpdate();

            PlayerWarp w = PlayerWarpManager.getManager().getWarp(update.getId(), update.getOriginName());
            w.setData(update);
            update.destroy();

        } else if(packet.getType() == PacketType.SendPlayerWarpOptionsPacket) {
            SendPlayerWarpOptionsPacket p = (SendPlayerWarpOptionsPacket) packet;
            PlayerWarpManager.getManager().setInactiveTime(p.getInactiveTime());
        } else if(packet.getType() == PacketType.DeletePlayerWarpPacket) {
            DeletePlayerWarpPacket p = (DeletePlayerWarpPacket) packet;

            PlayerWarp warp = PlayerWarpManager.getManager().getWarp(p.getId(), p.getName());
            if(warp != null) warp.setSource(true);
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
