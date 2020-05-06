package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportCommandOptionsPacket implements Packet {
    private int options = 0;

    public TeleportCommandOptionsPacket() {
    }

    public TeleportCommandOptionsPacket(boolean back, boolean tp, boolean tpAll, boolean tpToggle, boolean tpa, boolean tpaHere, boolean tpaAll, boolean tpaToggle) {
        if(back) options |= 1;
        if(tp) options |= (1 << 1);
        if(tpAll) options |= (1 << 2);
        if(tpToggle) options |= (1 << 3);
        if(tpa) options |= (1 << 4);
        if(tpaHere) options |= (1 << 5);
        if(tpaAll) options |= (1 << 6);
        if(tpaToggle) options |= (1 << 7);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.options);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.options = in.readInt();
    }

    public int getOptions() {
        return options;
    }
}
