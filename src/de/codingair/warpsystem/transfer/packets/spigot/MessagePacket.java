package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessagePacket implements Packet {
    private String player;
    private String message;

    public MessagePacket() {
    }

    public MessagePacket(String player, String message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeUTF(this.message);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.message = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }
}
