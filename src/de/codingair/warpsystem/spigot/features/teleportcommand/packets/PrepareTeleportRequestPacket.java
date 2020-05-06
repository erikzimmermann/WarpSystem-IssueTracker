package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareTeleportRequestPacket extends RequestPacket<Long> {
    private String sender, recipient;
    private boolean tpToSender;

    public PrepareTeleportRequestPacket() {
        recipient = null;
    }

    public PrepareTeleportRequestPacket(Callback<Long> callback, String sender, String recipient, boolean tpToSender) {
        super(callback);
        this.sender = sender;
        this.recipient = recipient;
        this.tpToSender = tpToSender;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        byte options = (byte) (tpToSender ? 1 : 0);
        if(recipient != null) options |= (1 << 1);

        out.writeByte(options);
        out.writeUTF(sender);
        if(recipient != null) out.writeUTF(recipient);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        byte options = in.readByte();
        this.sender = in.readUTF();
        this.tpToSender = (options & 1) != 0;
        if((options & (1 << 1)) != 0) this.recipient = in.readUTF();
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public boolean isTpToSender() {
        return tpToSender;
    }
}
