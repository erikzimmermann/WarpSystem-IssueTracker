package de.codingair.warpsystem.spigot.features.randomteleports.packets;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RandomTPPacket extends RequestPacket<Boolean> {
    private String player, server, world;

    public RandomTPPacket() {
    }

    public RandomTPPacket(Callback<Boolean> callback, String player, String server, String world) {
        super(callback);
        this.player = player;
        this.server = server;
        this.world = world;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        byte options = (byte) (world != null ? 1 : 0);

        out.writeByte(options);
        out.writeUTF(this.player);
        out.writeUTF(this.server);
        out.writeUTF(this.world);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        byte options = in.readByte();

        this.player = in.readUTF();
        this.server = in.readUTF();
        if((options & 1) != 0) this.world = in.readUTF();
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
}
