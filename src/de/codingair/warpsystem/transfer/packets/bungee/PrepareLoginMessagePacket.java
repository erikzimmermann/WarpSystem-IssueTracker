package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareLoginMessagePacket implements Packet {
    private String player;
    private String message;

    public PrepareLoginMessagePacket() {
    }

    public PrepareLoginMessagePacket(String player, String message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(player);
        out.writeUTF(message);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        player = in.readUTF();
        message = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }
}
