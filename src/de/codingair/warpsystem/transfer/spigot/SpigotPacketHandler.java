package de.codingair.warpsystem.transfer.spigot;

import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.PacketHandler;
import de.codingair.warpsystem.transfer.packets.utils.PacketType;

public class SpigotPacketHandler implements PacketHandler {
    private SpigotDataHandler dataHandler;

    public SpigotPacketHandler(SpigotDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public void handle(Packet packet, String... extra) {
        switch(PacketType.getByObject(packet)) {
            case ERROR:
                System.out.println("Couldn't handle anything!");
                break;
        }
    }
}
