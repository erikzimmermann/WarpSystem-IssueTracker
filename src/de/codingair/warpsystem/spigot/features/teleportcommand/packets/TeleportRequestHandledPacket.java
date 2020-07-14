package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportRequestHandledPacket implements Packet {
    private String sender, recipient;
    private boolean accepted;

    public TeleportRequestHandledPacket() {
    }

    public TeleportRequestHandledPacket(String sender, String recipient, boolean accepted) {
        this.sender = sender;
        this.recipient = recipient;
        this.accepted = accepted;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.sender);
        out.writeUTF(this.recipient);
        out.writeBoolean(this.accepted);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.sender = in.readUTF();
        this.recipient = in.readUTF();
        this.accepted = in.readBoolean();
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isAccepted() {
        return accepted;
    }
}