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

    public SendGlobalWarpNamesPacket() {
    }

    public SendGlobalWarpNamesPacket(HashMap<String, String> names) {
        this.names = names;
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
    }

    public HashMap<String, String> getNames() {
        return names;
    }
}
