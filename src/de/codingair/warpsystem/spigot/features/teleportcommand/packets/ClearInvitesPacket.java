package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClearInvitesPacket implements Packet {
    private String name;

    public ClearInvitesPacket() {
    }

    public ClearInvitesPacket(String name) {
        this.name = name;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
    }

    public String getName() {
        return name;
    }
}
