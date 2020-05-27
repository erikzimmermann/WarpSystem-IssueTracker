package de.codingair.warpsystem.bungee.features.globalwarps.listeners;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.managers.ServerManager;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.transfer.packets.general.BooleanPacket;
import de.codingair.warpsystem.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.transfer.packets.spigot.GlobalWarpTeleportPacket;
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
                boolean overwrite = ((PublishGlobalWarpPacket) packet).isOverwrite();

                if(overwrite) {
                    SGlobalWarp warp = manager.get(((PublishGlobalWarpPacket) packet).warp.getName());
                    if(warp != null) {
                        //Changed
                        answerBooleanPacket.setValue(true);
                        warp.setLoc(((PublishGlobalWarpPacket) packet).warp.getLoc());
                        warp.setServer(((PublishGlobalWarpPacket) packet).warp.getServer());
                        manager.synchronize(((PublishGlobalWarpPacket) packet).warp);
                    } else {
                        //Name already exists
                        answerBooleanPacket.setValue(false);
                    }
                } else {
                    if(manager.add(((PublishGlobalWarpPacket) packet).warp)) {
                        //Added
                        answerBooleanPacket.setValue(true);
                        manager.synchronize(((PublishGlobalWarpPacket) packet).warp);
                    } else {
                        //Name already exists
                        answerBooleanPacket.setValue(false);
                    }
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

            case GlobalWarpTeleportPacket:
                GlobalWarpTeleportPacket teleportPacket = (GlobalWarpTeleportPacket) packet;
                String player = teleportPacket.getPlayer();
                String teleport = teleportPacket.getId();
                warp = manager.get(teleport);
                String teleportDisplayName = teleportPacket.getDisplayName();
                if(teleportDisplayName == null) teleportDisplayName = warp.getName();

                if(warp == null) {
                    WarpSystem.getInstance().getLogger().log(Level.WARNING, "The server \"" + server.getName() + "\" is not up to date. Please reload it!");
                    return;
                }

                ServerInfo otherServer = BungeeCord.getInstance().getServerInfo(warp.getServer());
                ProxiedPlayer p = BungeeCord.getInstance().getPlayer(player);

                IntegerPacket answerIntegerPacket = new IntegerPacket();

                if(warp == null) answerIntegerPacket.setValue(1);
                else if(otherServer == null) answerIntegerPacket.setValue(2);
                else answerIntegerPacket.setValue(0);

                teleportPacket.applyAsAnswer(answerIntegerPacket);

                PrepareCoordinationTeleportPacket out = new PrepareCoordinationTeleportPacket(p.getName(), null, warp.getLoc().getWorld(), teleportDisplayName, teleportPacket.getMessage() == null ? PrepareCoordinationTeleportPacket.NO_MESSAGE : teleportPacket.getMessage(), warp.getLoc().getX(), warp.getLoc().getY(), warp.getLoc().getZ(), warp.getLoc().getYaw(), warp.getLoc().getPitch(), teleportPacket.getCosts(), null);

                if(p.getServer().getInfo().equals(otherServer)) {
                    WarpSystem.getInstance().getDataHandler().send(answerIntegerPacket, server);
                    WarpSystem.getInstance().getDataHandler().send(out, otherServer);
                } else {
                    if(WarpSystem.getInstance().getServerManager().isOnline(otherServer)) {
                        WarpSystem.getInstance().getDataHandler().send(answerIntegerPacket, server);
                        ServerManager.sendPlayerTo(otherServer, p, new Callback<ServerInfo>() {
                            @Override
                            public void accept(ServerInfo object) {
                                WarpSystem.getInstance().getDataHandler().send(out, otherServer);
                            }
                        });
                    } else {
                        answerIntegerPacket.setValue(2);
                        WarpSystem.getInstance().getDataHandler().send(answerIntegerPacket, server);
                    }
                }
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
