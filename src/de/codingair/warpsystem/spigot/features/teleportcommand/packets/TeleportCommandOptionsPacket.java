package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportCommandOptionsPacket implements Packet {
    private int options = 0;

    public TeleportCommandOptionsPacket() {
    }

    public TeleportCommandOptionsPacket(boolean bungeeCord, boolean back, boolean tp, boolean tpAll, boolean tpToggle, boolean tpa, boolean tpaHere, boolean tpaAll, boolean tpaToggle) {
        if(bungeeCord) options = options | 1;
        if(back) options = options | (1 << 1);
        if(tp) options = options | (1 << 2);
        if(tpAll) options = options | (1 << 3);
        if(tpToggle) options = options | (1 << 4);
        if(tpa) options = options | (1 << 5);
        if(tpaHere) options = options | (1 << 6);
        if(tpaAll) options = options | (1 << 7);
        if(tpaToggle) options = options | (1 << 8);
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
