package de.codingair.warpsystem.bungee.base.utils;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketVanishInfo implements Packet {
    private String player;
    private boolean vanished;

    public PacketVanishInfo() {
    }

    public PacketVanishInfo(String player, boolean vanished) {
        this.player = player;
        this.vanished = vanished;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeBoolean(this.vanished);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.vanished = in.readBoolean();
    }

    public String getPlayer() {
        return player;
    }

    public boolean isVanished() {
        return vanished;
    }
}
