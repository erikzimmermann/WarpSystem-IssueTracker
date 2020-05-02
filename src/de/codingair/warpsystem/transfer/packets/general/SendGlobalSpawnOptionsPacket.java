package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendGlobalSpawnOptionsPacket implements Packet {
    private String spawn, respawn;

    public SendGlobalSpawnOptionsPacket() {
        this.spawn = null;
        this.respawn = null;
    }

    public SendGlobalSpawnOptionsPacket(String spawn, String respawn) {
        this.spawn = spawn;
        this.respawn = respawn;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte options = (byte) (this.spawn != null ? 1 : 0);
        if(this.respawn != null) options |= (1 << 1);
        out.writeByte(options);

        if(this.spawn != null) out.writeUTF(this.spawn);
        if(this.respawn != null) out.writeUTF(this.respawn);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte options = in.readByte();

        if((options & 1) != 0) this.spawn = in.readUTF();
        if((options & (1 << 1)) != 0) this.respawn = in.readUTF();
    }

    public String getSpawn() {
        return spawn;
    }

    public String getRespawn() {
        return respawn;
    }
}
