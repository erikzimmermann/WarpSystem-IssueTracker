package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PerformCommandOnSpigotPacket implements Packet {
    private String player;
    private String command;

    public PerformCommandOnSpigotPacket() {
    }

    public PerformCommandOnSpigotPacket(String player, String command) {
        this.player = player;
        this.command = command;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeUTF(this.command);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.command = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getCommand() {
        return command;
    }
}
