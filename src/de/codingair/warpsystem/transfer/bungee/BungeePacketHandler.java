package de.codingair.warpsystem.transfer.bungee;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.bungee.DeployIconPacket;
import de.codingair.warpsystem.transfer.packets.bungee.SendUUIDPacket;
import de.codingair.warpsystem.transfer.packets.general.BooleanPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PerformCommandPacket;
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
        ProxiedPlayer player = extra[1] == null ? null : BungeeCord.getInstance().getPlayer(extra[1]);

        switch(PacketType.getByObject(packet)) {
            case ERROR:
                System.out.println("Could handle anything!");
                return;

            case UploadIconPacket:
                DeployIconPacket answerDeployIcon = new DeployIconPacket(((UploadIconPacket) packet).icon);
                for(ServerInfo info : BungeeCord.getInstance().getServers().values()) {
                    dataHandler.send(answerDeployIcon, info);
                }
                break;

            case RequestUUIDPacket:
                SendUUIDPacket uuidPacket = new SendUUIDPacket(BungeeCord.getInstance().getPlayer(((RequestUUIDPacket) packet).getName()).getUniqueId());
                ((RequestUUIDPacket) packet).applyAsAnswer(uuidPacket);
                WarpSystem.getInstance().getDataHandler().send(uuidPacket, server);
                break;

            case PerformCommandPacket:
                PerformCommandPacket performCommandPacket = (PerformCommandPacket) packet;

                ProxiedPlayer p = BungeeCord.getInstance().getPlayer(performCommandPacket.getPlayer());
                if(p == null) return;

                String cmd = performCommandPacket.getCommand();

                BooleanPacket answerPacket = new BooleanPacket(BungeeCord.getInstance().getPluginManager().dispatchCommand(p, cmd));
                performCommandPacket.applyAsAnswer(answerPacket);

                WarpSystem.getInstance().getDataHandler().send(answerPacket, server);
                break;
        }
    }
}
