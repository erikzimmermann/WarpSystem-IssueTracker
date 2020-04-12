package de.codingair.warpsystem.transfer.bungee;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.bungee.DeployIconPacket;
import de.codingair.warpsystem.transfer.packets.bungee.SendUUIDPacket;
import de.codingair.warpsystem.transfer.packets.general.BooleanPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PerformCommandOnBungeePacket;
import de.codingair.warpsystem.transfer.packets.spigot.RequestUUIDPacket;
import de.codingair.warpsystem.transfer.packets.spigot.UploadIconPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketHandler;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePacketHandler implements PacketHandler {
    private BungeeDataHandler dataHandler;

    public BungeePacketHandler(BungeeDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void handle(Packet packet, String... extra) {
        ServerInfo server = BungeeCord.getInstance().getServerInfo(extra[0]);

        switch(PacketType.getByObject(packet)) {
            case ERROR:
                System.out.println("Could not handle anything!");
                return;

            case UploadIconPacket:
                DeployIconPacket answerDeployIcon = new DeployIconPacket(((UploadIconPacket) packet).icon);
                for(ServerInfo info : BungeeCord.getInstance().getServers().values()) {
                    dataHandler.send(answerDeployIcon, info);
                }
                break;

            case RequestUUIDPacket:
                ProxiedPlayer pp = BungeeCord.getInstance().getPlayer(((RequestUUIDPacket) packet).getName());
                SendUUIDPacket uuidPacket = new SendUUIDPacket(pp == null ? null : pp.getUniqueId());
                ((RequestUUIDPacket) packet).applyAsAnswer(uuidPacket);
                WarpSystem.getInstance().getDataHandler().send(uuidPacket, server);
                break;

            case PerformCommandOnBungeePacket: {
                PerformCommandOnBungeePacket performCommandOnBungeePacket = (PerformCommandOnBungeePacket) packet;
                String cmd = performCommandOnBungeePacket.getCommand();

                boolean success = false;
                if(cmd.contains("%player%")) {
                    success = BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), cmd.replace("%player%", performCommandOnBungeePacket.getPlayer()));
                } else {
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(performCommandOnBungeePacket.getPlayer());
                    if(p != null) success = BungeeCord.getInstance().getPluginManager().dispatchCommand(p, cmd);
                }

                BooleanPacket answerPacket = new BooleanPacket(success);
                performCommandOnBungeePacket.applyAsAnswer(answerPacket);
                WarpSystem.getInstance().getDataHandler().send(answerPacket, server);
                break;
            }
        }
    }
}
