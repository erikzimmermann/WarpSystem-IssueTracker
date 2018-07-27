package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.warpsystem.transfer.packets.utils.AnswerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BooleanPacket extends AnswerPacket<Boolean> {

    public BooleanPacket() {
    }

    public BooleanPacket(boolean b) {
        super(b);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(getValue());
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        setValue(in.readBoolean());
        super.read(in);
    }
}
