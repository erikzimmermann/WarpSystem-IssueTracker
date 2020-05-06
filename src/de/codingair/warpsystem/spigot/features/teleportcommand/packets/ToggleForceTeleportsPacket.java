package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToggleForceTeleportsPacket implements Packet {
    private String player;
    private boolean autoDenyTp;
    private boolean autoDenyTpa;

    public ToggleForceTeleportsPacket() {
    }

    public ToggleForceTeleportsPacket(String player, boolean autoDenyTp, boolean autoDenyTpa) {
        this.player = player;
        this.autoDenyTp = autoDenyTp;
        this.autoDenyTpa = autoDenyTpa;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);

        byte options = (byte) (this.autoDenyTp ? 1 : 0);
        options |= (autoDenyTpa ? 1 : 0) << 1;

        out.writeByte(options);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        byte options = in.readByte();
        this.autoDenyTp = (options & 1) != 0;
        this.autoDenyTpa = (options & (1 << 1)) != 0;
    }

    public String getPlayer() {
        return player;
    }

    public boolean isAutoDenyTp() {
        return autoDenyTp;
    }

    public boolean isAutoDenyTpa() {
        return autoDenyTpa;
    }
}
