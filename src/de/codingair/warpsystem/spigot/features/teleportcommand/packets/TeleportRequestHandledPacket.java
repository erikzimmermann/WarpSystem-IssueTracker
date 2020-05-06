package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportRequestHandledPacket implements Packet {
    private String sender, recipient;

    public TeleportRequestHandledPacket() {
    }

    public TeleportRequestHandledPacket(String sender, String recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.sender);
        out.writeUTF(this.recipient);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.sender = in.readUTF();
        this.recipient = in.readUTF();
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }
}