package de.codingair.warpsystem.transfer.serializeable.icons;

import de.codingair.warpsystem.transfer.serializeable.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SActionObject implements Serializable {
    private int action;
    private String command;
    private String category;

    private String world;
    private double x, y, z;
    private float yaw, pitch;

    private String server;

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.action);

        switch(this.action) {
            case 0:
                out.writeBoolean(this.command != null);
                if(this.command != null) out.writeUTF(this.command);
                break;
            case 1:
                out.writeBoolean(this.category != null);
                if(this.category != null) out.writeUTF(this.category);
                break;
            case 2:
                out.writeBoolean(this.world != null);
                if(this.world != null) {
                    out.writeUTF(this.world);
                    out.writeDouble(this.x);
                    out.writeDouble(this.y);
                    out.writeDouble(this.z);
                    out.writeFloat(this.yaw);
                    out.writeFloat(this.pitch);
                }
                break;
            case 3:
                out.writeBoolean(this.server != null);
                if(this.server != null) out.writeUTF(this.server);
                break;
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.action = in.readInt();

        switch(this.action) {
            case 0:
                if(in.readBoolean()) this.command = in.readUTF();
                break;
            case 1:
                if(in.readBoolean()) this.category = in.readUTF();
                break;
            case 2:
                if(in.readBoolean()) {
                    this.world = in.readUTF();
                    this.x = in.readDouble();
                    this.y = in.readDouble();
                    this.z = in.readDouble();
                    this.yaw = in.readFloat();
                    this.pitch = in.readFloat();
                }
                break;
            case 3:
                if(in.readBoolean()) this.server = in.readUTF();
                break;
        }
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
