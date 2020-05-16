package de.codingair.warpsystem.spigot.features.randomteleports.packets;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RandomTPWorldsPacket implements Packet {
    private List<String> worlds;

    public RandomTPWorldsPacket() {
    }

    public RandomTPWorldsPacket(List<String> worlds) {
        this.worlds = worlds;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(worlds.size());
        for(String world : worlds) {
            out.writeUTF(world);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int worlds = in.readUnsignedByte();
        this.worlds = new ArrayList<>();
        for(int i = 0; i < worlds; i++) {
            this.worlds.add(in.readUTF());
        }
    }

    public List<String> getWorlds() {
        return worlds;
    }
}
