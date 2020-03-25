package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerWarpTeleportProcessPacket implements Packet {
    private String name;
    private UUID id;
    private byte b;

    //reset to 0: b = -1
    //increase by 1: b = 1

    public PlayerWarpTeleportProcessPacket(String name, UUID id) {
        this.name = name;
        this.id = id;
        this.b = 0;
    }

    public PlayerWarpTeleportProcessPacket(String name, UUID id, boolean increaseSales, boolean resetSales, boolean increasePerformed) {
        this.name = name;
        this.id = id;

        this.b = (byte) (increaseSales ? 1 : 0);
        this.b |= (resetSales ? 1 : 0) << 1;
        this.b |= (increasePerformed ? 1 : 0) << 2;
    }

    public PlayerWarpTeleportProcessPacket() {
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeLong(id.getMostSignificantBits());
        out.writeLong(id.getLeastSignificantBits());
        out.writeByte(b);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.id = new UUID(in.readLong(), in.readLong());
        this.b = in.readByte();
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public boolean increaseSales() {
        return (b & 1) != 0;
    }

    public boolean resetSales() {
        return (b & (1 << 1)) != 0;
    }

    public boolean increasePerformed() {
        return (b & (1 << 2)) != 0;
    }

    public void setIncreaseSales(boolean increase) {
        this.b ^= 1;
        this.b |= increase ? 1 : 0;
    }

    public void setResetSales(boolean reset) {
        this.b ^= 1 << 1;
        this.b |= (reset ? 1 : 0) << 1;
    }

    public void setIncreasePerformed(boolean increase) {
        this.b ^= 1 << 2;
        this.b |= (increase ? 1 : 0) << 2;
    }
}
