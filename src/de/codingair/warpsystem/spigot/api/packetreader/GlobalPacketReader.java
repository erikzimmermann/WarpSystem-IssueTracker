package de.codingair.warpsystem.spigot.api.packetreader;

public interface GlobalPacketReader {

    /**
     * @param packet Object
     * @return intercept = true
     */
    boolean readPacket(Object packet);

    /**
     * @param packet Object
     * @return intercept = true
     */
    boolean writePacket(Object packet);

    String getName();
}
