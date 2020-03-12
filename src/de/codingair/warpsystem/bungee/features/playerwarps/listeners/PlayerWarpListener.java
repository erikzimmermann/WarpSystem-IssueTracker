package de.codingair.warpsystem.bungee.features.playerwarps.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpData;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpUpdate;
import de.codingair.warpsystem.transfer.packets.bungee.SendPlayerWarpOptionsPacket;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpUpdatePacket;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.transfer.packets.spigot.RegisterServerForPlayerWarpsPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.serializeable.Serializable;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayerWarpListener implements PacketListener, Listener {

    @EventHandler
    public void onJoin(ServerConnectEvent e) {
        PlayerWarpManager.getInstance().checkPlayerWarpOwnerNames(e.getPlayer());
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.SendPlayerWarpsPacket) {
            List<PlayerWarpData> l = ((SendPlayerWarpsPacket) packet).getData();

            for(Serializable s : l) {
                PlayerWarpData w = (PlayerWarpData) s;
                PlayerWarpManager.getInstance().updateWarp(w);
            }

        } else if(packet.getType() == PacketType.RegisterServerForPlayerWarpsPacket) {
            List<PlayerWarpData> l = new ArrayList<>();

            for(List<PlayerWarpData> value : PlayerWarpManager.getInstance().getWarps().values()) {
                l.addAll(value);
            }

            ServerInfo server = BungeeCord.getInstance().getServerInfo(extra);
            PlayerWarpManager.getInstance().setActive(server, true);
            PlayerWarpManager.getInstance().setTimeDependent(server, ((RegisterServerForPlayerWarpsPacket) packet).isTimeDependent());

            SendPlayerWarpOptionsPacket options = new SendPlayerWarpOptionsPacket(PlayerWarpManager.getInstance().getInactiveTime());
            WarpSystem.getInstance().getDataHandler().send(options, server);

            SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(l);
            WarpSystem.getInstance().getDataHandler().send(p, server);
        } else if(packet.getType() == PacketType.MoveLocalPlayerWarpsPacket) {
            List<PlayerWarpData> l = new ArrayList<>();

            for(List<PlayerWarpData> value : PlayerWarpManager.getInstance().getWarps().values()) {
                for(PlayerWarpData w : value) {
                    if(w.getServer().equalsIgnoreCase(extra)) l.add(w);
                }
            }

            for(Serializable w : l) {
                PlayerWarpManager.getInstance().delete((PlayerWarpData) w);
            }

            ServerInfo server = BungeeCord.getInstance().getServerInfo(extra);
            PlayerWarpManager.getInstance().setActive(server, false);

            SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(l);
            WarpSystem.getInstance().getDataHandler().send(p, server);
        } else if(packet.getType() == PacketType.SendPlayerWarpUpdatesPacket) {
            PlayerWarpUpdate update = ((SendPlayerWarpUpdatePacket) packet).getUpdate();

            PlayerWarpData w = PlayerWarpManager.getInstance().getWarp(update.getId(), update.getOriginName());
            w.apply(update);

            //forwarding
            PlayerWarpManager.getInstance().interactWithServers(s -> {
                if(s.getName().equalsIgnoreCase(extra)) return;
                WarpSystem.getInstance().getDataHandler().send(packet, s);
            });
            update.destroy();
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
