package de.codingair.warpsystem.spigot.features.teleportcommand;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.transfer.utils.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportPacketListener implements PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        switch(PacketType.getByObject(packet)) {
            case TeleportPlayerToPlayerPacket: {
                TeleportPlayerToPlayerPacket tpPacket = (TeleportPlayerToPlayerPacket) packet;

                Player gate = Bukkit.getPlayer(tpPacket.getGate());
                Player player = Bukkit.getPlayer(tpPacket.getPlayer());
                Player other = Bukkit.getPlayer(tpPacket.getTarget());

                if(gate != null && player != null && other != null) {
                    if(gate != player) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", player.getName()).replace("%warp%", other.getName()));

                    WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.TeleportCommand, new Destination(new LocationAdapter(other.getLocation())),
                            other.getName(), 0, true,
                            gate == player ? Lang.get("Teleported_To") :
                                    Lang.get("Teleported_To_By").replace("%gate%", gate.getName())
                            , false, null);
                }
                break;
            }

            case TeleportPlayerToCoordsPacket: {
                TeleportPlayerToCoordsPacket tpPacket = (TeleportPlayerToCoordsPacket) packet;

                Player gate = Bukkit.getPlayer(tpPacket.getGate());
                Player player = Bukkit.getPlayer(tpPacket.getPlayer());
                double x = tpPacket.getX();
                double y = tpPacket.getY();
                double z = tpPacket.getZ();
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
