package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportPlayerToPlayerPacket implements Packet {
    private String gate, player, target;

    public TeleportPlayerToPlayerPacket() {
    }

    public TeleportPlayerToPlayerPacket(String gate, String player, String target) {
        this.gate = gate;
        this.player = player;
        this.target = target;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.gate);
        out.writeUTF(this.player);
        out.writeUTF(this.target);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.gate = in.readUTF();
        this.player = in.readUTF();
        this.target = in.readUTF();
    }

    public String getGate() {
        return gate;
    }

    public String getPlayer() {
        return player;
    }

    public String getTarget() {
        return target;
    }
}
