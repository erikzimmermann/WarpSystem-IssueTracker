package de.codingair.warpsystem.spigot.api.packetreader;

import org.bukkit.entity.Player;

public interface GlobalPacketReader {

    /**
     * @param packet Object
     * @return intercept = true
     */
    boolean readPacket(Player player, Object packet);

    /**
     * @param packet Object
     * @return intercept = true
     */
    boolean writePacket(Player player, Object packet);

    String getName();
}
