package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportSpawnPacket implements Packet {
    private String player;
    private boolean respawn;

    public TeleportSpawnPacket(String player, boolean respawn) {
        this.player = player;
        this.respawn = respawn;
    }

    public TeleportSpawnPacket() {
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeBoolean(this.respawn);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.respawn = in.readBoolean();
    }

    public String getPlayer() {
        return player;
    }

    public boolean isRespawn() {
        return respawn;
    }
}
