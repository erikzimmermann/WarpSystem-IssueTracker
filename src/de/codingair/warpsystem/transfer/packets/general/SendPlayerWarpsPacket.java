package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.serializeable.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendPlayerWarpsPacket implements Packet {
    private List<Serializable> l;

    public SendPlayerWarpsPacket(List<Serializable> list) {
        this.l = list;
    }

    public SendPlayerWarpsPacket() {
        l = new ArrayList<>();
    }

    @Override
    public void write(DataOutputStream o) throws IOException {
        o.writeShort(l.size());
        for(Serializable s : l) {
            s.write(o);
        }
    }

    @Override
    public void read(DataInputStream i) throws IOException {
        int size = i.readShort();

        boolean spigot = true;

        try {
            Class.forName("org.bukkit.plugin.java.JavaPlugin");
        } catch(Exception e) {
            spigot = false;
        }

        if(spigot) {
            //Spigot
            for(int i1 = 0; i1 < size; i1++) {
                l.add(de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp.readInitially(i));
            }
        } else {
            //BungeeCord
            for(int i1 = 0; i1 < size; i1++) {
                l.add(de.codingair.warpsystem.bungee.features.playerwarps.utils.PlayerWarp.readInitially(i));
            }
        }
    }

    public List<Serializable> getSerializables() {
        return l;
    }
}
