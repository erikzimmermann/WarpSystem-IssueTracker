package de.codingair.warpsystem.spigot.base.utils.cooldown;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CooldownPacket implements Packet {
    private Cooldown cooldown;

    public CooldownPacket() {
    }

    public CooldownPacket(Cooldown cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        cooldown.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        cooldown = new Cooldown();
        cooldown.read(in);
    }

    public Cooldown getCooldown() {
        return cooldown;
    }
}
