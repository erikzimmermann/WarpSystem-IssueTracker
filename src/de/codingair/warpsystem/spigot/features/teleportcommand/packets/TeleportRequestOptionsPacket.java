package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportRequestOptionsPacket implements Packet {
    private boolean bungeeCord;

    public TeleportRequestOptionsPacket() {
    }

    public TeleportRequestOptionsPacket(boolean bungeeCord) {
        this.bungeeCord = bungeeCord;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(this.bungeeCord);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.bungeeCord = in.readBoolean();
    }

    public boolean isBungeeCord() {
        return bungeeCord;
    }
}
