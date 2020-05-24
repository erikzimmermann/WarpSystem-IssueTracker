package de.codingair.warpsystem.bungee.features.teleport.listeners;

import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.transfer.packets.spigot.GetOnlineCountPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportPacketListener implements PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.ToggleForceTeleportsPacket) {
            ToggleForceTeleportsPacket tpPacket = (ToggleForceTeleportsPacket) packet;

            ProxiedPlayer player = Players.getPlayer(tpPacket.getPlayer());
            if(player != null) {
                TeleportManager.getInstance().setDenyForceTps(player, tpPacket.isAutoDenyTp());
                TeleportManager.getInstance().setDenyForceTpRequests(player, tpPacket.isAutoDenyTpa());
            }
        } else if(packet.getType() == PacketType.GetOnlineCountPacket) {
            GetOnlineCountPacket tpPacket = (GetOnlineCountPacket) packet;
            ServerInfo origin = BungeeCord.getInstance().getServerInfo(extra);

            IntegerPacket answer = new IntegerPacket(BungeeCord.getInstance().getPlayers().size());
            tpPacket.applyAsAnswer(answer);
            WarpSystem.getInstance().getDataHandler().send(answer, origin);
        }

    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
