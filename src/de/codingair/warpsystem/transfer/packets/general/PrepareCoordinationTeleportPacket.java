package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareCoordinationTeleportPacket extends RequestPacket<Integer> {
    public static final String NO_MESSAGE = "EMPTY";
    private String player, server, world, destinationName, message;
    private double x, y, z;
    private float yaw, pitch;
    private double costs;

    public PrepareCoordinationTeleportPacket() {
    }

    public PrepareCoordinationTeleportPacket(String player, String server, String world, String destinationName, String message, double x, double y, double z, float yaw, float pitch, double costs, Callback<Integer> callback) {
        super(callback);
        this.player = player;
        this.server = server;
        this.world = world;
        this.destinationName = destinationName;
        this.message = message;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.costs = costs;
    }

    public PrepareCoordinationTeleportPacket clone(Callback<Integer> callback) {
        return new PrepareCoordinationTeleportPacket(player, server, world, destinationName, message, x, y, z, yaw, pitch, costs, callback);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);

        byte b = (byte) (server != null ? 1 : 0);
        b |= (costs != 0 ? 1 : 0) << 1;
        b |= (message != null ? 1 : 0) << 2;
        out.writeByte(b);

        out.writeUTF(this.player);
        if(this.server != null) out.writeUTF(this.server);
        out.writeUTF(this.world);
        out.writeUTF(this.destinationName);
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        if(costs != 0) out.writeDouble(costs);
        if(message != null) out.writeUTF(message);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);

        byte b = in.readByte();

        this.player = in.readUTF();
        if((b & 1) != 0) this.server = in.readUTF();
        this.world = in.readUTF();
        this.destinationName = in.readUTF();
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        if((b & (1 << 1)) != 0) this.costs = in.readDouble();
        if((b & (1 << 2)) != 0) this.message = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getWorld() {
        return world;
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public double getCosts() {
        return costs;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getMessage() {
        return message;
    }
}
