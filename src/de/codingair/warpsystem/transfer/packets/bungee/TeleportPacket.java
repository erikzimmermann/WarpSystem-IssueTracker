package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportPacket implements Packet {
    private String player;
    private SGlobalWarp warp;
    private String teleportDisplayName;

    public TeleportPacket() {
    }

    public TeleportPacket(String player, SGlobalWarp warp, String teleportDisplayName) {
        this.player = player;
        this.warp = warp;
        this.teleportDisplayName = teleportDisplayName;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        this.warp.write(out);
        out.writeUTF(this.teleportDisplayName);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.warp = new SGlobalWarp();
        this.warp.read(in);
        this.teleportDisplayName = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public SGlobalWarp getWarp() {
        return warp;
    }

    public String getTeleportDisplayName() {
        return teleportDisplayName;
    }
}
