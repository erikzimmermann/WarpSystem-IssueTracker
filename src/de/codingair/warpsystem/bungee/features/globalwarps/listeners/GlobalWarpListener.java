package de.codingair.warpsystem.bungee.features.globalwarps.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPacket;
import de.codingair.warpsystem.transfer.packets.general.BooleanPacket;
import de.codingair.warpsystem.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.logging.Level;

public class GlobalWarpListener implements Listener, PacketListener {

    @EventHandler
    public void onConnect(ServerConnectedEvent e) {
        if(e.getServer().getInfo().getPlayers().size() <= 1) {
            //Update it
            GlobalWarpManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
            manager.synchronize(e.getServer().getInfo());
        }
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        GlobalWarpManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
        ServerInfo server = BungeeCord.getInstance().getServerInfo(extra);

        switch(PacketType.getByObject(packet)) {
            case PublishGlobalWarpPacket:
                ((PublishGlobalWarpPacket) packet).warp.setServer(extra);

                BooleanPacket answerBooleanPacket = new BooleanPacket();

                if(manager.add(((PublishGlobalWarpPacket) packet).warp)) {
                    //Added
                    answerBooleanPacket.setValue(true);
                    manager.synchronize(((PublishGlobalWarpPacket) packet).warp);
                } else {
                    //Name already exists
                    answerBooleanPacket.setValue(false);
                }

                ((PublishGlobalWarpPacket) packet).applyAsAnswer(answerBooleanPacket);
                WarpSystem.getInstance().getDataHandler().send(answerBooleanPacket, server);
                break;

            case DeleteGlobalWarpPacket:
                SGlobalWarp warp = manager.get(((DeleteGlobalWarpPacket) packet).getWarp());
                answerBooleanPacket = new BooleanPacket();
                ((DeleteGlobalWarpPacket) packet).applyAsAnswer(answerBooleanPacket);

                if(warp == null) {
                    answerBooleanPacket.setValue(false);
                    WarpSystem.getInstance().getDataHandler().send(answerBooleanPacket, server);
                } else {
                    manager.remove(warp.getName());
                    answerBooleanPacket.setValue(true);
                    WarpSystem.getInstance().getDataHandler().send(answerBooleanPacket, server);

                    manager.synchronize(warp);
                }
                break;

            case PrepareTeleportPacket:
                String player = ((PrepareTeleportPacket) packet).getPlayer();
                String teleport = ((PrepareTeleportPacket) packet).getTeleportName();
                String teleportDisplayName = ((PrepareTeleportPacket) packet).getDisplayName();
                warp = manager.get(teleport);

                if(warp == null) {
                    WarpSystem.getInstance().getLogger().log(Level.WARNING, "The server \"" + server.getName() + "\" is not up to date. Please reload it!");
                    return;
                }

                ServerInfo otherServer = BungeeCord.getInstance().getServerInfo(warp.getServer());
                ProxiedPlayer p = BungeeCord.getInstance().getPlayer(player);

                IntegerPacket answerIntegerPacket = new IntegerPacket();

                if(warp == null) answerIntegerPacket.setValue(1);
                else if(otherServer == null || !WarpSystem.getInstance().getServerManager().isOnline(otherServer)) answerIntegerPacket.setValue(2);
                else answerIntegerPacket.setValue(0);

                ((PrepareTeleportPacket) packet).applyAsAnswer(answerIntegerPacket);
                WarpSystem.getInstance().getDataHandler().send(answerIntegerPacket, server);

                if(answerIntegerPacket.getValue() != 0) return;

                if(p.getServer().getInfo().equals(otherServer)) {
                    WarpSystem.getInstance().getDataHandler().send(new TeleportPacket(player, warp, teleportDisplayName, ((PrepareTeleportPacket) packet).getCosts()), otherServer);
                } else p.connect(otherServer, (connected, throwable) -> {
                    if(connected) WarpSystem.getInstance().getDataHandler().send(new TeleportPacket(player, warp, teleportDisplayName, ((PrepareTeleportPacket) packet).getCosts()), otherServer);
                });
                break;

            case RequestGlobalWarpNamesPacket:
                manager.synchronize(server);
                break;
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
