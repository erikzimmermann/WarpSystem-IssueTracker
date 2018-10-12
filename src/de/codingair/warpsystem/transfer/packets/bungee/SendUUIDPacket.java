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
        out.writeLong(getValue().getMostSignificantBits());
        out.writeLong(getValue().getLeastSignificantBits());
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        long most = in.readLong();
        long least = in.readLong();
        setValue(new UUID(most, least));
        super.read(in);
    }
}
