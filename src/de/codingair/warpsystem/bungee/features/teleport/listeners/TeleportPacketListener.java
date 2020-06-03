package de.codingair.warpsystem.bungee.features.teleport.listeners;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.*;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.transfer.packets.general.LongPacket;
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
        } else if(PacketType.getByObject(packet) == PacketType.TeleportRequestHandledPacket) {
            ProxiedPlayer sender = Players.getPlayer(((TeleportRequestHandledPacket) packet).getSender());
            if(sender != null) WarpSystem.getInstance().getDataHandler().send(packet, sender.getServer().getInfo());
        } else if(PacketType.getByObject(packet) == PacketType.PrepareTeleportPlayerToPlayerPacket) {
            PrepareTeleportPlayerToPlayerPacket p = (PrepareTeleportPlayerToPlayerPacket) packet;

            ProxiedPlayer player = Players.getPlayer(p.getPlayer());
            ProxiedPlayer target = Players.getPlayer(p.getDestinationPlayer());

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
            StartTeleportToPlayerPacket tpPacket = (StartTeleportToPlayerPacket) packet;
            ProxiedPlayer player = Players.getPlayer(tpPacket.getPlayer());
            ServerInfo origin = BungeeCord.getInstance().getServerInfo(extra);

            IntegerPacket answer = new IntegerPacket(0);
            tpPacket.applyAsAnswer(answer);

            if(player != null) WarpSystem.getInstance().getDataHandler().send(new StartTeleportToPlayerPacket(new Callback<Integer>() {
                @Override
                public void accept(Integer id) {
                    answer.setValue(id);
                    WarpSystem.getInstance().getDataHandler().send(answer, origin);
                }
            }, tpPacket.getPlayer(), tpPacket.getTo(), tpPacket.getToDisplayName(), tpPacket.getTeleportRequestSender()), player.getServer().getInfo());
            else {
                answer.setValue(1);
                WarpSystem.getInstance().getDataHandler().send(answer, origin);
            }
        } else if(PacketType.getByObject(packet) == PacketType.PrepareTeleportRequestPacket) {
            PrepareTeleportRequestPacket tpPacket = (PrepareTeleportRequestPacket) packet;
            ServerInfo origin = BungeeCord.getInstance().getServerInfo(extra);

            String recipient = tpPacket.getRecipient();

            if(recipient == null) {
                //forward to all
                int servers = WarpSystem.getInstance().getServerManager().getOnlineServer().size() - 1;
                Value<Integer> handled = new Value<>(0);
                Value<Long> generalResult = new Value<>(0L);

                for(ServerInfo s : WarpSystem.getInstance().getServerManager().getOnlineServer()) {
                    if(s.getName().equalsIgnoreCase(extra) || !TeleportManager.getInstance().isAccessible(s)) continue;

                    WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportRequestPacket(new Callback<Long>() {
                        @Override
                        public void accept(Long result) {
                            handled.setValue(handled.getValue() + 1);
                            generalResult.setValue(generalResult.getValue() + result);

                            if(handled.getValue() == servers) {
                                LongPacket answer = new LongPacket(generalResult.getValue());
                                tpPacket.applyAsAnswer(answer);
                                WarpSystem.getInstance().getDataHandler().send(answer, origin);
                            }
                        }
                    }, tpPacket.getSender(), null, true), s);
                }
            } else {
                //only recipient
                ProxiedPlayer player = Players.getPlayer(tpPacket.getRecipient());

                if(player == null || !TeleportManager.getInstance().isAccessible(player.getServer().getInfo()) || WarpSystem.getVanishManager().isVanished(player.getName())) {
                    //not online/accessible
                    LongPacket answer = new LongPacket(0);
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, origin);
                } else if(TeleportManager.getInstance().deniesForceTpRequests(player)) {
                    //auto deny
                    LongPacket answer = new LongPacket(-1L << 32);
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, origin);
                } else {
                    WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportRequestPacket(new Callback<Long>() {
                        @Override
                        public void accept(Long result) {
                            LongPacket answer = new LongPacket(result);
                            tpPacket.applyAsAnswer(answer);
                            WarpSystem.getInstance().getDataHandler().send(answer, origin);
                        }
                    }, tpPacket.getSender(), player.getName(), tpPacket.isTpToSender()), player.getServer().getInfo());
                }
            }
        } else if(PacketType.getByObject(packet) == PacketType.PrepareTeleportPacket) {
            PrepareTeleportPacket tpPacket = (PrepareTeleportPacket) packet;

            ServerInfo origin = BungeeCord.getInstance().getServerInfo(extra);
            ProxiedPlayer sender = Players.getPlayer(tpPacket.getSender());
            ProxiedPlayer targetPlayer = tpPacket.getSender().equalsIgnoreCase(tpPacket.getTarget()) ? sender : Players.getPlayer(tpPacket.getTarget());

            if(targetPlayer == null || !TeleportManager.getInstance().isAccessible(targetPlayer.getServer().getInfo())) {
                LongPacket answer = new LongPacket((((long) 0) << 32));
                tpPacket.applyAsAnswer(answer);
                WarpSystem.getInstance().getDataHandler().send(answer, origin);
                return;
            }

            ServerInfo target = targetPlayer.getServer().getInfo();

            String recipient = tpPacket.getRecipient();
            if(recipient == null) {
                //forward to all
                int handled = 0;
                int sent = 0;

                for(ServerInfo s : WarpSystem.getInstance().getServerManager().getOnlineServer()) {
                    if(s.getName().equalsIgnoreCase(extra)) continue;
                    handled += s.getPlayers().size();
                    if(!TeleportManager.getInstance().isAccessible(s)) continue;

                    //tp all
                    for(ProxiedPlayer player : s.getPlayers()) {
                        if(TeleportManager.getInstance().deniesForceTps(player)) continue;

                        sent++;
                        TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(tpPacket.getSender(), player.getName(), targetPlayer.getName(), false);
                        WarpSystem.getInstance().getDataHandler().send(ptpPacket, target);
                        player.connect(target);
                    }
                }

                LongPacket answer = new LongPacket((((long) handled) << 32) | (sent & 0xffffffffL));
                tpPacket.applyAsAnswer(answer);
                WarpSystem.getInstance().getDataHandler().send(answer, origin);
            } else {
                //only recipient
                ProxiedPlayer player = Players.getPlayer(tpPacket.getRecipient());

                if(player == null || !TeleportManager.getInstance().isAccessible(player.getServer().getInfo())) {
                    //not online/accessible
                    LongPacket answer = new LongPacket(0);
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, origin);
                } else if(TeleportManager.getInstance().deniesForceTps(player) && !player.equals(sender)) {
                    //auto deny
                    LongPacket answer = new LongPacket(1L << 32);
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, origin);
                } else {
                    if(tpPacket.isCoordsPacket()) {
                        TeleportPlayerToCoordsPacket ptcPacket = new TeleportPlayerToCoordsPacket(
                                tpPacket.getSender(), player.getName(),
                                tpPacket.getX(), tpPacket.getY(), tpPacket.getZ(),
                                false, false, false);

                        WarpSystem.getInstance().getDataHandler().send(ptcPacket, target);
                    } else {
                        TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(tpPacket.getSender(), player.getName(), targetPlayer.getName(), true);
                        WarpSystem.getInstance().getDataHandler().send(ptpPacket, target);
                    }
                    player.connect(target);

                    LongPacket answer = new LongPacket((((long) 1) << 32) | (1 & 0xffffffffL));
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, origin);
                }
            }
        } else if(packet.getType() == PacketType.ToggleForceTeleportsPacket) {
            ToggleForceTeleportsPacket tpPacket = (ToggleForceTeleportsPacket) packet;

            ProxiedPlayer player = Players.getPlayer(tpPacket.getPlayer());
            if(player != null) {
                TeleportManager.getInstance().setDenyForceTps(player, tpPacket.isAutoDenyTp());
                TeleportManager.getInstance().setDenyForceTpRequests(player, tpPacket.isAutoDenyTpa());
            }
        }

    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
