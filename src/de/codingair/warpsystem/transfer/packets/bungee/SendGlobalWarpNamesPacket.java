package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SendGlobalWarpNamesPacket implements Packet {
    private HashMap<String, String> names = new HashMap<>();
    private boolean start;

    public SendGlobalWarpNamesPacket() {
    }

    public SendGlobalWarpNamesPacket(HashMap<String, String> names) {
        this.names = names;
        this.start = false;
    }

    public SendGlobalWarpNamesPacket(HashMap<String, String> names, boolean start) {
        this.names = names;
        this.start = start;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.names.size());
        for(String name : this.names.keySet()) {
            out.writeUTF(name);
        }
        for(String server : this.names.values()) {
            out.writeUTF(server);
        }
        out.writeBoolean(this.start);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int size = in.readInt();
        for(int i = 0; i < size; i++) {
            this.names.put(in.readUTF(), null);
        }
        for(String name : this.names.keySet()) {
            this.names.replace(name, in.readUTF());
        }
        this.start = in.readBoolean();
    }

    public HashMap<String, String> getNames() {
        return names;
    }

    public boolean isStart() {
        return start;
    }
}
