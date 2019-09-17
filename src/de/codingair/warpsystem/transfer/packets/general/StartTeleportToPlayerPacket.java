package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StartTeleportToPlayerPacket implements Packet {
    private String player, to, toDisplayName, teleportRequestSender;

    public StartTeleportToPlayerPacket() {
    }

    public StartTeleportToPlayerPacket(String player, String to, String toDisplayName, String teleportRequestSender) {
        this.player = player;
        this.to = to;
        this.toDisplayName = toDisplayName;
        this.teleportRequestSender = teleportRequestSender;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(player);
        out.writeUTF(to);
        out.writeUTF(toDisplayName);
        out.writeUTF(teleportRequestSender);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        player = in.readUTF();
        to = in.readUTF();
        toDisplayName = in.readUTF();
        teleportRequestSender = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getTo() {
        return to;
    }

    public String getToDisplayName() {
        return toDisplayName;
    }

    public String getTeleportRequestSender() {
        return teleportRequestSender;
    }
}
