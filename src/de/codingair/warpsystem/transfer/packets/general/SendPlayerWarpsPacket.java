package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpData;
import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendPlayerWarpsPacket implements Packet {
    private List<PlayerWarpData> l;
    private boolean clearable = false;

    public SendPlayerWarpsPacket(List<PlayerWarpData> list) {
        this.l = list;
    }

    public SendPlayerWarpsPacket() {
        l = new ArrayList<>();
    }

    @Override
    public void write(DataOutputStream o) throws IOException {
        o.writeShort(l.size());

        if(clearable) {
            for(PlayerWarpData s : l) {
                s.write(o);
                s.destroy();
            }

            l.clear();
        } else {
            for(PlayerWarpData s : l) {
                s.write(o);
            }
        }
    }

    @Override
    public void read(DataInputStream i) throws IOException {
        int size = i.readShort();

        for(int i1 = 0; i1 < size; i1++) {
            PlayerWarpData w = new PlayerWarpData();
            w.read(i);
            l.add(w);
        }
    }

    public List<PlayerWarpData> getData() {
        return l;
    }

    public boolean isClearable() {
        return clearable;
    }

    public void setClearable(boolean clearable) {
        this.clearable = clearable;
    }
}
