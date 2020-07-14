package de.codingair.warpsystem.spigot.base.utils.cooldown;

import com.google.common.base.Preconditions;
import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CooldownDataPacket implements Packet {
    private Cooldown[] cooldown;

    public CooldownDataPacket() {
    }

    public CooldownDataPacket(Cooldown[] cooldown) {
        Preconditions.checkState(cooldown.length >= 1);
        this.cooldown = cooldown;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(cooldown[0].getPlayer().getMostSignificantBits());
        out.writeLong(cooldown[0].getPlayer().getLeastSignificantBits());
        out.writeInt(cooldown.length);
        for(Cooldown c : cooldown) {
            out.writeLong(c.getEnd());
            out.writeInt(c.getHashId());
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        UUID id = new UUID(in.readLong(), in.readLong());

        int size = in.readInt();
        this.cooldown = new Cooldown[size];
        for(int i = 0; i < size; i++) {
            cooldown[i] = new Cooldown(id, in.readLong(), in.readInt());
        }
    }

    public Cooldown[] getCooldown() {
        return cooldown;
    }
}
