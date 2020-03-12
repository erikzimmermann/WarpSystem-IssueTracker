package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendPlayerWarpOptionsPacket implements Packet {
    private long inactiveTime;

    public SendPlayerWarpOptionsPacket(long inactiveTime) {
        this.inactiveTime = inactiveTime;
    }

    public SendPlayerWarpOptionsPacket() {
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(inactiveTime);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        inactiveTime = in.readLong();
    }

    public long getInactiveTime() {
        return inactiveTime;
    }
}
