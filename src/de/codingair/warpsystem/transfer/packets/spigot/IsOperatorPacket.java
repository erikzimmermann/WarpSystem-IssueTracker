package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IsOperatorPacket implements Packet {
    private String player;
    private boolean operator;

    public IsOperatorPacket() {
    }

    public IsOperatorPacket(String player, boolean operator) {
        this.player = player;
        this.operator = operator;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeBoolean(this.operator);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.operator = in.readBoolean();
    }

    public String getPlayer() {
        return player;
    }

    public boolean isOperator() {
        return operator;
    }
}
