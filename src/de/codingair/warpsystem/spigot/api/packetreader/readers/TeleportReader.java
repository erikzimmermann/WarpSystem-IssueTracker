package de.codingair.warpsystem.spigot.api.packetreader.readers;

import de.codingair.warpsystem.spigot.api.events.PlayerTeleportedEvent;
import de.codingair.warpsystem.spigot.api.packetreader.GlobalPacketReader;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportReader implements GlobalPacketReader {
    @Override
    public String getName() {
        return "WarpSystem-TeleportReader";
    }

    @Override
    public boolean readPacket(Player player, Object packet) {
        if(packet.getClass().getSimpleName().equals("PacketPlayInTeleportAccept")) {
            Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> Bukkit.getPluginManager().callEvent(new PlayerTeleportedEvent(player)));
        }
        return false;
    }

    @Override
    public boolean writePacket(Player player, Object packet) {
        return false;
    }
}
