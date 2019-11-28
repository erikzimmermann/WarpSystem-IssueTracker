package de.codingair.warpsystem.spigot.features.teleportcommand.listeners;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.api.players.BungeePlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.EmptyAdapter;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.ClearInvitesPacket;
import de.codingair.warpsystem.transfer.packets.bungee.PrepareTeleportRequestPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.general.StartTeleportToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TeleportPacketListener implements Listener, PacketListener {

    @Override
    public void onReceive(Packet packet, String extra) {
        switch(PacketType.getByObject(packet)) {
            case TeleportPlayerToPlayerPacket: {
                TeleportPlayerToPlayerPacket tpPacket = (TeleportPlayerToPlayerPacket) packet;

                Player gate = Bukkit.getPlayer(tpPacket.getGate());
                Player player = Bukkit.getPlayer(tpPacket.getPlayer());
                Player other = Bukkit.getPlayer(tpPacket.getTarget());

                if(gate != null && player != null && other != null) {
                    if(gate != player && tpPacket.isMessageToGate())
                        gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", player.getName()).replace("%warp%", other.getName()));

                    TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(other.getLocation())), other.getName());
                    options.setCosts(Math.max(tpPacket.getCosts(), 0));
                    options.setSkip(true);
                    options.setConfirmPayment(false);
                    options.setOrigin(Origin.TeleportCommand);
                    options.setMessage(gate == player ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gate.getName()));

                    if(options.getFinalCosts(player) > 0) MoneyAdapterType.getActive().deposit(player, options.getFinalCosts(player));

                    WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                } else {
                    TeleportListener.setSpawnPositionOrTeleport(tpPacket.getPlayer(), new de.codingair.codingapi.tools.Location(other.getLocation()), tpPacket.getTarget(), gate == player ? Math.max(tpPacket.getCosts(), 0) > 0 ? Lang.get("Money_Paid").replace("%AMOUNT%", tpPacket.getCosts() + "") : Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", tpPacket.getGate()));
                }
                break;
            }

            case PrepareTeleportRequestPacket: {
                PrepareTeleportRequestPacket tpPacket = (PrepareTeleportRequestPacket) packet;
                TeleportCommandManager.getInstance().sendTeleportRequest(new BungeePlayer(tpPacket.getSender(), tpPacket.getSenderDisplayName()), tpPacket.isTpToSender(), tpPacket.isNotifySender(), Bukkit.getPlayer(tpPacket.getReceiver()));
                break;
            }

            case StartTeleportToPlayerPacket: {
                StartTeleportToPlayerPacket tpPacket = (StartTeleportToPlayerPacket) packet;
                Player player = Bukkit.getPlayer(tpPacket.getPlayer());

                if(player == null) return;

                TeleportOptions options = new TeleportOptions(new Destination(new EmptyAdapter()), tpPacket.getToDisplayName());
                options.setOrigin(Origin.CustomTeleportCommands);
                options.setWaitForTeleport(true);
                options.setMessage(null);
                options.setPayMessage(null);
                options.setCosts(TeleportCommandManager.getInstance().getTpaCosts());
                options.setCallback(new Callback<TeleportResult>() {
                    @Override
                    public void accept(TeleportResult result) {
                        //move
                        WarpSystem.getInstance().getDataHandler().send(new ClearInvitesPacket(tpPacket.getTeleportRequestSender()));

                        if(result == TeleportResult.TELEPORTED) {
                            WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportPlayerToPlayerPacket(player.getName(), tpPacket.getTo(), new Callback<Integer>() {
                                @Override
                                public void accept(Integer result) {
                                    if(result == 0) {
                                        //teleported
                                    } else {
                                        player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_not_valid").replace("%PLAYER%", ChatColor.stripColor(tpPacket.getToDisplayName())));
                                    }
                                }
                            }).setCosts(TeleportCommandManager.getInstance().getTpaCosts()));
                        }
                    }
                });

                WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                break;
            }

            case TeleportPlayerToCoordsPacket: {
                TeleportPlayerToCoordsPacket tpPacket = (TeleportPlayerToCoordsPacket) packet;

                Player gate = Bukkit.getPlayer(tpPacket.getGate());
                Player player = Bukkit.getPlayer(tpPacket.getPlayer());
                double x = (tpPacket.isRelativeX() ? gate.getLocation().getX() : 0) + tpPacket.getX();
                double y = (tpPacket.isRelativeY() ? gate.getLocation().getY() : 0) + tpPacket.getY();
                double z = (tpPacket.isRelativeZ() ? gate.getLocation().getZ() : 0) + tpPacket.getZ();
                String destination = "x=" + x + ", y=" + y + ", z=" + z;

                if(gate != null && player != null) {
                    if(gate != player) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", player.getName()).replace("%warp%", destination));

                    Location location = player.getLocation();
                    location.setX(x);
                    location.setY(y);
                    location.setZ(z);
                    location.setYaw(0);
                    location.setPitch(0);

                    WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.TeleportCommand, new Destination(new LocationAdapter(location)),
                            destination, 0, true,
                            gate == player ? Lang.get("Teleported_To") :
                                    Lang.get("Teleported_To_By").replace("%gate%", gate.getName())
                            , false, null);
                } else {
                    TeleportListener.setSpawnPositionOrTeleport(tpPacket.getPlayer(), new de.codingair.codingapi.tools.Location(null, x, y, z, 0, 0), gate.getDisplayName(), gate == player ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gate.getName()));
                }
                break;
            }
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
