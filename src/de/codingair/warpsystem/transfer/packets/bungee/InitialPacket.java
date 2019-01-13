package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InitialPacket implements Packet {
    private String version;

    public InitialPacket() {
    }

    public InitialPacket(String version) {
        this.version = version;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.version);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.version = in.readUTF();
    }

    public String getVersion() {
        return version;
    }
}
