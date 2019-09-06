package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareTeleportRequestPacket implements Packet {
    private String sender, senderDisplayName, receiver;
    private boolean tpToSender;
    private boolean notifySender = true;

    public PrepareTeleportRequestPacket() {
    }

    public PrepareTeleportRequestPacket(String sender, String senderDisplayName, String receiver, boolean tpToSender) {
        this.sender = sender;
        this.senderDisplayName = senderDisplayName;
        this.receiver = receiver;
        this.tpToSender = tpToSender;
    }

    public PrepareTeleportRequestPacket(String sender, String senderDisplayName, String receiver, boolean tpToSender, boolean notifySender) {
        this.sender = sender;
        this.senderDisplayName = senderDisplayName;
        this.receiver = receiver;
        this.tpToSender = tpToSender;
        this.notifySender = notifySender;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(sender);
        out.writeUTF(senderDisplayName);
        out.writeUTF(receiver);
        out.writeBoolean(tpToSender);
        out.writeBoolean(notifySender);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.sender = in.readUTF();
        this.senderDisplayName = in.readUTF();
        this.receiver = in.readUTF();
        this.tpToSender = in.readBoolean();
        this.notifySender = in.readBoolean();
    }

    public String getSender() {
        return sender;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isTpToSender() {
        return tpToSender;
    }

    public boolean isNotifySender() {
        return notifySender;
    }
}
