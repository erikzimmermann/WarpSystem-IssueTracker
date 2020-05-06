package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StartTeleportToPlayerPacket extends RequestPacket<Integer> {
    private String player, to, toDisplayName, teleportRequestSender;

    public StartTeleportToPlayerPacket() {
    }

    public StartTeleportToPlayerPacket(Callback<Integer> callback, String player, String to, String toDisplayName, String teleportRequestSender) {
        super(callback);
        this.player = player;
        this.to = to;
        this.toDisplayName = toDisplayName;
        this.teleportRequestSender = teleportRequestSender;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(player);
        out.writeUTF(to);
        out.writeUTF(toDisplayName);
        out.writeUTF(teleportRequestSender);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
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
