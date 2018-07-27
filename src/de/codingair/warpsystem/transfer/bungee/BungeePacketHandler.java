package de.codingair.warpsystem.transfer.bungee;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.bungee.WarpSystem;
import de.codingair.warpsystem.transfer.packets.bungee.DeployIconPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPacket;
import de.codingair.warpsystem.transfer.packets.general.BooleanPacket;
import de.codingair.warpsystem.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.UploadIconPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketHandler;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePacketHandler implements PacketHandler {
    private BungeeDataHandler dataHandler;

    public BungeePacketHandler(BungeeDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void handle(Packet packet, String extra) {
        ServerInfo server = BungeeCord.getInstance().getServerInfo(extra);

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

            case PublishGlobalWarpPacket:
                ((PublishGlobalWarpPacket) packet).warp.setServer(extra);

                BooleanPacket answerBooleanPacket = new BooleanPacket();

                if(WarpSystem.getInstance().getGlobalWarpManager().add(((PublishGlobalWarpPacket) packet).warp)) {
                    //Added
                    answerBooleanPacket.setValue(true);
                    WarpSystem.getInstance().getGlobalWarpManager().synchronize(((PublishGlobalWarpPacket) packet).warp.getName());
                } else {
                    //Name already exists
                    answerBooleanPacket.setValue(false);
                }

                ((PublishGlobalWarpPacket) packet).applyAsAnswer(answerBooleanPacket);
                this.dataHandler.send(answerBooleanPacket, server);
                break;

            case DeleteGlobalWarpPacket:
                SGlobalWarp warp = WarpSystem.getInstance().getGlobalWarpManager().get(((DeleteGlobalWarpPacket) packet).getWarp());
                answerBooleanPacket = new BooleanPacket();
                ((DeleteGlobalWarpPacket) packet).applyAsAnswer(answerBooleanPacket);

                if(warp == null) {
                    answerBooleanPacket.setValue(false);
                    this.dataHandler.send(answerBooleanPacket, server);
                } else {
                    WarpSystem.getInstance().getGlobalWarpManager().remove(warp.getName());
                    answerBooleanPacket.setValue(true);
                    this.dataHandler.send(answerBooleanPacket, server);

                    WarpSystem.getInstance().getGlobalWarpManager().synchronize(warp.getName());
                }
                break;

            case PrepareTeleportPacket:
                String player = ((PrepareTeleportPacket) packet).getPlayer();
                String teleport = ((PrepareTeleportPacket) packet).getTeleportName();
                warp = WarpSystem.getInstance().getGlobalWarpManager().get(teleport);
                ServerInfo otherServer = BungeeCord.getInstance().getServerInfo(warp.getServer());

                IntegerPacket answerIntegerPacket = new IntegerPacket();

                if(warp == null) answerIntegerPacket.setValue(1);
                else if(otherServer == null || !WarpSystem.getInstance().getServerManager().isOnline(otherServer)) answerIntegerPacket.setValue(2);
                else answerIntegerPacket.setValue(0);

                ((PrepareTeleportPacket) packet).applyAsAnswer(answerIntegerPacket);
                this.dataHandler.send(answerIntegerPacket, server);

                if(answerIntegerPacket.getValue() != 0) return;

                ProxiedPlayer p = BungeeCord.getInstance().getPlayer(player);
                p.connect(otherServer, (connected, throwable) -> {
                    if(connected) dataHandler.send(new TeleportPacket(player, warp), otherServer);
                });
                break;

            case RequestGlobalWarpNamesPacket:
                System.out.println("Got the request packet, goingt to send data...");
                WarpSystem.getInstance().getGlobalWarpManager().sendData(server);
                break;
        }
    }
}
