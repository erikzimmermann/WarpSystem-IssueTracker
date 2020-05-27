package de.codingair.warpsystem.spigot.features.teleportcommand.listeners;

import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
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

                if(other == null) return;

                TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(other.getLocation())), other.getName());
                options.setCosts(Math.max(tpPacket.getCosts(), 0));
                options.setSkip(true);
                options.setConfirmPayment(false);
                options.setOrigin(Origin.TeleportCommand);
                options.setMessage(gate == player ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gate.getName()));

                if(gate != null && gate != player && tpPacket.isMessageToGate())
                    gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", tpPacket.getPlayer()).replace("%warp%", other.getName()));

                TeleportListener.setSpawnPositionOrTeleport(tpPacket.getPlayer(), options);
                break;
            }

            case ToggleForceTeleportsPacket: {
                ToggleForceTeleportsPacket tpPacket = (ToggleForceTeleportsPacket) packet;

                Player player = Bukkit.getPlayer(tpPacket.getPlayer());
                if(player != null) TeleportCommandManager.getInstance().setDenyForceTps(player, tpPacket.isAutoDenyTp());
                break;
            }
        }
    }

    private Number cut(double n) {
        double d = ((double) (int) (n * 100)) / 100;
        if(d == (int) d) return (int) d;
        else return d;
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
