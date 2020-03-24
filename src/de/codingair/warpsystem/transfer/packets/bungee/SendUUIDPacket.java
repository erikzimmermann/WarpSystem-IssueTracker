package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.AnswerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SendUUIDPacket extends AnswerPacket<UUID> {
    public SendUUIDPacket() {
    }

    public SendUUIDPacket(UUID value) {
        super(value);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte b = (byte) (getValue() != null ? 1 : 0);
        out.writeByte(b);

        if(getValue() != null) {
            out.writeLong(getValue().getMostSignificantBits());
            out.writeLong(getValue().getLeastSignificantBits());
        }
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte b = in.readByte();

        if((b & 1) != 0) {
            long most = in.readLong();
            long least = in.readLong();
            setValue(new UUID(most, least));
        } else setValue(null);
        super.read(in);
    }
}
