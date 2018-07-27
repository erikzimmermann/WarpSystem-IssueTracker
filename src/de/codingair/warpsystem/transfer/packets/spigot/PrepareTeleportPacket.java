package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareTeleportPacket extends RequestPacket<Integer> {
    private String player;
    private String teleportName;

    public PrepareTeleportPacket() {
    }

    public PrepareTeleportPacket(String player, String teleportName, Callback<Integer> callback) {
        super(callback);
        this.player = player;
        this.teleportName = teleportName;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeUTF(this.teleportName);
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.teleportName = in.readUTF();
        super.read(in);
    }

    public String getPlayer() {
        return player;
    }

    public String getTeleportName() {
        return teleportName;
    }
}
