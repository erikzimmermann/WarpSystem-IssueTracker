package de.codingair.warpsystem.spigot.features.randomteleports.packets;

import com.google.common.base.Preconditions;
import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QueueRTPUsagePacket implements Packet {
    private List<UUID> ids;
    private String server;

    public QueueRTPUsagePacket() {
    }

    public QueueRTPUsagePacket(List<UUID> ids) {
        Preconditions.checkState(ids.size() <= 50);
        this.ids = ids;
        this.server = null;
    }

    public QueueRTPUsagePacket(UUID id, String server) {
        ids = new ArrayList<>();
        ids.add(id);
        this.server = server;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte options = (byte) ids.size();
        options |= (server != null ? 1 : 0) << 7;
        out.writeByte(options);

        if(server != null) out.writeUTF(server);

        for(UUID id : ids) {
            out.writeLong(id.getMostSignificantBits());
            out.writeLong(id.getLeastSignificantBits());
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        ids = new ArrayList<>();
        int size = in.readByte();
        boolean server = (size & (1 << 7)) != 0;
        size = size & 0b00111111;

        if(server) this.server = in.readUTF();
        for(int i = 0; i < size; i++) {
            ids.add(new UUID(in.readLong(), in.readLong()));
        }
    }

    public List<UUID> getIds() {
        return ids;
    }

    public UUID getIdOnce() {
        return ids.remove(0);
    }

    public String getServer() {
        return server;
    }
}
