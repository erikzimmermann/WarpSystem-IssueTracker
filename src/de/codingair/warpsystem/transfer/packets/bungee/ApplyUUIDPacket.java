package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ApplyUUIDPacket implements Packet {
    private String name;
    private UUID id;
    private boolean joined;

    public ApplyUUIDPacket() {
    }

    public ApplyUUIDPacket(String name, UUID id, boolean joined) {
        this.name = name;
        this.id = id;
        this.joined = joined;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeLong(this.id.getMostSignificantBits());
        out.writeLong(this.id.getLeastSignificantBits());
        out.writeBoolean(this.joined);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.id = new UUID(in.readLong(), in.readLong());
        this.joined = in.readBoolean();
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public boolean isJoined() {
        return joined;
    }
}
