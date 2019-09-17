package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IsOperatorPacket implements Packet {
    private String player;

    public IsOperatorPacket() {
    }

    public IsOperatorPacket(String player) {
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }
}
