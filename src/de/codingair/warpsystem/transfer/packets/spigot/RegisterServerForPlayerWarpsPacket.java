package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegisterServerForPlayerWarpsPacket implements Packet {
    private boolean timeDependent;

    public RegisterServerForPlayerWarpsPacket(boolean timeDependent) {
        this.timeDependent = timeDependent;
    }

    public RegisterServerForPlayerWarpsPacket() {
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte b = (byte) (timeDependent ? 1 : 0);
        out.writeByte(b);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte b = in.readByte();
        timeDependent = (b & 1) != 0;
    }

    public boolean isTimeDependent() {
        return timeDependent;
    }
}
