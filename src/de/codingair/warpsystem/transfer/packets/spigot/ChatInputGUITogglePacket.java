package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatInputGUITogglePacket implements Packet {
    private String name;
    private byte using;

    public ChatInputGUITogglePacket() {
    }

    public ChatInputGUITogglePacket(String name, boolean using) {
        this.name = name;
        this.using = (byte) (using ? 1 : 0);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeByte(this.using);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.using = in.readByte();
    }

    public String getName() {
        return name;
    }

    public boolean isUsing() {
        return this.using == 1;
    }
}
