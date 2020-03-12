package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class DeletePlayerWarpPacket implements Packet {
    private String name;
    private UUID id;

    public DeletePlayerWarpPacket(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

    public DeletePlayerWarpPacket() {
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeLong(id.getMostSignificantBits());
        out.writeLong(id.getLeastSignificantBits());
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        name = in.readUTF();
        id = new UUID(in.readLong(), in.readLong());
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }
}
