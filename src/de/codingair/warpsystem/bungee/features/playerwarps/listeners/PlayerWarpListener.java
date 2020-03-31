package de.codingair.warpsystem.bungee.features.playerwarps.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpData;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpUpdate;
import de.codingair.warpsystem.transfer.packets.bungee.SendPlayerWarpOptionsPacket;
import de.codingair.warpsystem.transfer.packets.general.DeletePlayerWarpPacket;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpUpdatePacket;
import de.codingair.warpsystem.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PlayerWarpTeleportProcessPacket;
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

            //forwarding
            synchronized(PlayerWarpManager.getInstance()) {
                PlayerWarpManager.getInstance().interactWithServers(s -> {
                    if(s.getName().equalsIgnoreCase(extra)) return;
                    WarpSystem.getInstance().getDataHandler().send(packet, s);
                });
            }
        } else if(packet.getType() == PacketType.RegisterServerForPlayerWarpsPacket) {
            List<List<PlayerWarpData>> uploads = new ArrayList<>();
            List<PlayerWarpData> l = new ArrayList<>();

            for(List<PlayerWarpData> value : PlayerWarpManager.getInstance().getWarps().values()) {
                for(PlayerWarpData w : value) {
                    l.add(w);

                    if(l.size() == 100) {
                        uploads.add(new ArrayList<>(l));
                        l.clear();
                    }
                }
            }

            if(!l.isEmpty()) uploads.add(l);

            ServerInfo server = BungeeCord.getInstance().getServerInfo(extra);
            PlayerWarpManager.getInstance().setActive(server, true);
            PlayerWarpManager.getInstance().setTimeDependent(server, ((RegisterServerForPlayerWarpsPacket) packet).isTimeDependent());

            SendPlayerWarpOptionsPacket options = new SendPlayerWarpOptionsPacket(PlayerWarpManager.getInstance().getInactiveTime());
            WarpSystem.getInstance().getDataHandler().send(options, server);

            for(List<PlayerWarpData> upload : uploads) {
                SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(upload);
                WarpSystem.getInstance().getDataHandler().send(p, server);
            }

            uploads.clear();
        } else if(packet.getType() == PacketType.MoveLocalPlayerWarpsPacket) {
            List<List<PlayerWarpData>> uploads = new ArrayList<>();
            List<PlayerWarpData> l = new ArrayList<>();

            for(List<PlayerWarpData> value : PlayerWarpManager.getInstance().getWarps().values()) {
                for(PlayerWarpData w : value) {
                    if(w.getServer().equalsIgnoreCase(extra)) l.add(w);
                    if(l.size() == 100) {
                        uploads.add(new ArrayList<>(l));
                        l.clear();
                    }
                }
            }

            if(!l.isEmpty()) uploads.add(l);

            ServerInfo server = BungeeCord.getInstance().getServerInfo(extra);
            PlayerWarpManager.getInstance().setActive(server, false);

            for(List<PlayerWarpData> upload : uploads) {
                for(PlayerWarpData d : upload) {
                    PlayerWarpManager.getInstance().delete(d, true);
                }

                SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(upload);
                WarpSystem.getInstance().getDataHandler().send(p, server);
            }

            uploads.clear();
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
        } else if(packet.getType() == PacketType.PlayerWarpTeleportProcessPacket) {
            PlayerWarpTeleportProcessPacket p = (PlayerWarpTeleportProcessPacket) packet;

            PlayerWarpData w = PlayerWarpManager.getInstance().getWarp(p.getId(), p.getName());

            if(p.increaseSales()) w.increaseInactiveSales();
            if(p.resetSales()) w.setInactiveSales((byte) 0);
            if(p.increasePerformed()) w.increasePerformed();

            //forwarding
            PlayerWarpManager.getInstance().interactWithServers(s -> {
                if(s.getName().equalsIgnoreCase(extra)) return;
                WarpSystem.getInstance().getDataHandler().send(packet, s);
            });
        } else if(packet.getType() == PacketType.DeletePlayerWarpPacket) {
            DeletePlayerWarpPacket p = (DeletePlayerWarpPacket) packet;

            PlayerWarpData w = PlayerWarpManager.getInstance().getWarp(p.getId(), p.getName());

            PlayerWarpManager.getInstance().delete(w, false);

            //forwarding
            PlayerWarpManager.getInstance().interactWithServers(s -> {
                if(s.getName().equalsIgnoreCase(extra)) return;
                WarpSystem.getInstance().getDataHandler().send(packet, s);
            });
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
