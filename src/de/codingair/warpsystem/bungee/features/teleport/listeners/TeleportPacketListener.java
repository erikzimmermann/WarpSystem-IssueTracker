package de.codingair.warpsystem.bungee.features.teleport.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.ClearInvitesPacket;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.transfer.packets.general.StartTeleportToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportPacketListener implements PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        if(PacketType.getByObject(packet) == PacketType.TeleportCommandOptions) {
            ServerInfo info = BungeeCord.getInstance().getServerInfo(extra);
            TeleportCommandOptionsPacket p = (TeleportCommandOptionsPacket) packet;

            TeleportManager.getInstance().registerOptions(info, p.getOptions());
        } else if(PacketType.getByObject(packet) == PacketType.ClearInvitesPacket) {
            TeleportManager.getInstance().clear(((ClearInvitesPacket) packet).getName());
        } else if(PacketType.getByObject(packet) == PacketType.PrepareTeleportPlayerToPlayerPacket) {
            PrepareTeleportPlayerToPlayerPacket p = (PrepareTeleportPlayerToPlayerPacket) packet;

            ProxiedPlayer player = BungeeCord.getInstance().getPlayer(p.getPlayer());
            ProxiedPlayer target = BungeeCord.getInstance().getPlayer(p.getDestinationPlayer());

            IntegerPacket answer = new IntegerPacket(0);
            p.applyAsAnswer(answer);

            if(player == null || target == null) {
                answer.setValue(1);
                WarpSystem.getInstance().getDataHandler().send(answer, BungeeCord.getInstance().getServerInfo(extra));
            } else {
                TeleportPlayerToPlayerPacket tpPacket = new TeleportPlayerToPlayerPacket(player.getName(), player.getName(), target.getName());
                tpPacket.setCosts(p.getCosts());
                WarpSystem.getInstance().getDataHandler().send(tpPacket, target.getServer().getInfo());

                if(!player.getServer().getInfo().equals(target.getServer().getInfo())) {
                    player.connect(target.getServer().getInfo(), (connected, throwable) -> {
                        if(!connected) answer.setValue(2);
                        WarpSystem.getInstance().getDataHandler().send(answer, BungeeCord.getInstance().getServerInfo(extra));
                    });
                } else WarpSystem.getInstance().getDataHandler().send(answer, BungeeCord.getInstance().getServerInfo(extra));
            }

        } else if(PacketType.getByObject(packet) == PacketType.StartTeleportToPlayerPacket) {
            ProxiedPlayer player = BungeeCord.getInstance().getPlayer(((StartTeleportToPlayerPacket) packet).getPlayer());
            if(player != null) WarpSystem.getInstance().getDataHandler().send(packet, player.getServer().getInfo());
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
