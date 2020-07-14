package de.codingair.warpsystem.spigot.base.utils.cooldown;

import com.google.common.base.Preconditions;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.transfer.serializeable.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class Cooldown implements Serializable, de.codingair.codingapi.tools.io.utils.Serializable {
    private UUID player;
    private long end;
    private int hashId; //hashId - only Spigot (BungeeCord only for global origin cooldown)

    public Cooldown() {
    }

    public Cooldown(UUID player) {
        this.player = player;
    }

    public Cooldown(UUID player, long end, int hashId) {
        this.player = player;
        this.end = end;
        this.hashId = hashId;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        Preconditions.checkNotNull(player);
        out.writeLong(player.getMostSignificantBits());
        out.writeLong(player.getLeastSignificantBits());
        out.writeLong(end);
        out.writeInt(hashId);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        player = new UUID(in.readLong(), in.readLong());
        end = in.readLong();
        hashId = in.readInt();
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        throw new IllegalStateException("Not supported.");
    }

    public boolean read(DataWriter d, long time) throws Exception {
        end = d.getLong("end") + time;
        hashId = d.getInteger("hash");
        return true;
    }

    @Override
    public void write(DataWriter d) {
        throw new IllegalStateException("Not supported.");
    }

    public void write(DataWriter d, long time) {
        d.put("end", end - time);
        d.put("hash", hashId);
    }

    @Override
    public void destroy() {
        player = null;
        end = 0;
        hashId = 0;
    }

    public long getRemainingTime() {
        return Math.max(end - System.currentTimeMillis(), 0);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Cooldown cooldown = (Cooldown) o;
        return end == cooldown.end &&
                hashId == cooldown.hashId &&
                Objects.equals(player, cooldown.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, end, hashId);
    }

    @Override
    public String toString() {
        return "Cooldown{" +
                "player=" + player +
                ", end=" + end +
                ", hashId=" + hashId +
                '}';
    }

    public UUID getPlayer() {
        return player;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public boolean isGlobally() {
        return hashId == 0;
    }

    public int getHashId() {
        return hashId;
    }
}
