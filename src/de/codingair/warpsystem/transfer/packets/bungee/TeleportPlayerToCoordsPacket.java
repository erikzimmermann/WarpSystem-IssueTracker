package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportPlayerToCoordsPacket implements Packet {
    private String gate, player, destinationName = null, world = null;
    private double x, y, z, costs = 0;
    private float yaw = 0, pitch = 0;
    private boolean relativeX, relativeY, relativeZ;

    public TeleportPlayerToCoordsPacket() {
    }

    public TeleportPlayerToCoordsPacket(String gate, String player, double x, double y, double z, boolean relativeX, boolean relativeY, boolean relativeZ) {
        this.gate = gate;
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
    }

    public TeleportPlayerToCoordsPacket(String gate, String player, String destinationName, double x, double y, double z, double costs, float yaw, float pitch, boolean relativeX, boolean relativeY, boolean relativeZ) {
        this.gate = gate;
        this.player = player;
        this.destinationName = destinationName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.costs = costs;
        this.yaw = yaw;
        this.pitch = pitch;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte b = (byte) (!gate.equalsIgnoreCase(player) ? 1 : 0);
        b |= (relativeX ? 1 : 0) << 1;
        b |= (relativeY ? 1 : 0) << 2;
        b |= (relativeZ ? 1 : 0) << 3;
        b |= (costs != 0 ? 1 : 0) << 4;
        b |= (destinationName != null ? 1 : 0) << 5;
        b |= (yaw != 0 || pitch != 0 ? 1 : 0) << 6;
        b |= (world != null ? 1 : 0) << 7;

        out.writeByte(b);
        out.writeUTF(this.gate);
        if(!gate.equalsIgnoreCase(player)) out.writeUTF(this.player);
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        if(costs != 0) out.writeDouble(costs);
        if(destinationName != null) out.writeUTF(destinationName);
        if(yaw != 0) out.writeFloat(yaw);
        if(pitch != 0) out.writeFloat(pitch);
        if(world != null) out.writeUTF(world);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte b = in.readByte();

        if((b & 1) != 0) {
            this.gate = in.readUTF();
            this.player = in.readUTF();
        } else this.gate = this.player = in.readUTF();
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.relativeX = (b & (1 << 1)) != 0;
        this.relativeY = (b & (1 << 2)) != 0;
        this.relativeZ = (b & (1 << 3)) != 0;
        if((b & (1 << 4)) != 0) this.costs = in.readDouble();
        if((b & (1 << 5)) != 0) this.destinationName = in.readUTF();
        if((b & (1 << 6)) != 0) {
            this.yaw = in.readFloat();
            this.pitch = in.readFloat();
        }
        if((b & (1 << 7)) != 0) this.world = in.readUTF();
    }

    public String getWorld() {
        return world;
    }

    public String getGate() {
        return gate;
    }

    public String getPlayer() {
        return player;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean isRelativeX() {
        return relativeX;
    }

    public boolean isRelativeY() {
        return relativeY;
    }

    public boolean isRelativeZ() {
        return relativeZ;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public double getCosts() {
        return costs;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
