package de.codingair.warpsystem.spigot.features.teleportcommand.packets;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareTeleportPacket extends RequestPacket<Long> {
    private String sender, recipient, target;
    private double x, y, z;

    public PrepareTeleportPacket() {
        recipient = null;
    }

    public PrepareTeleportPacket(Callback<Long> callback, String sender, String recipient, String target) {
        super(callback);
        this.sender = sender;
        this.recipient = recipient;
        this.target = target;
    }

    public PrepareTeleportPacket(Callback<Long> callback, String sender, String recipient, double x, double y, double z) {
        super(callback);
        this.sender = sender;
        this.recipient = recipient;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        byte options = (byte) (target == null ? 1 : 0);

        if(target != null) {
            if(!sender.equalsIgnoreCase(target)) options |= (1 << 1);
            if(recipient != null) options |= (1 << 2);
        }

        out.writeByte(options);
        out.writeUTF(sender);

        if(target == null) {
            out.writeDouble(x);
            out.writeDouble(y);
            out.writeDouble(z);
            out.writeUTF(recipient);
        } else {
            if(!sender.equalsIgnoreCase(target)) out.writeUTF(target);
            if(recipient != null) out.writeUTF(recipient);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        byte options = in.readByte();

        this.sender = in.readUTF();

        if((options & 1) != 0) {
            //target is null
            this.recipient = in.readUTF();
        } else {
            if((options & (1 << 1))!= 0) this.target = in.readUTF();
            else this.target = this.sender;
            if((options & (1 << 2)) != 0) this.recipient = in.readUTF();
        }
    }

    public boolean isCoordsPacket() {
        return target == null;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getTarget() {
        return target;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
