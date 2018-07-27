package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendGlobalWarpNamesPacket implements Packet {
    private List<String> names = new ArrayList<>();

    public SendGlobalWarpNamesPacket() {
    }

    public SendGlobalWarpNamesPacket(List<String> names) {
        this.names = names;
    }

    public SendGlobalWarpNamesPacket(String... names) {
        this.names = Arrays.asList(names);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.names.size());
        for(String name : this.names) {
            out.writeUTF(name);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int size = in.readInt();
        for(int i = 0; i < size; i++) {
            this.names.add(in.readUTF());
        }
    }

    public List<String> getNames() {
        return names;
    }
}
