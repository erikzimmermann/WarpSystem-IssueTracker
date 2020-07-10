package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GetOnlineCountPacket extends RequestPacket<Integer> {

    public GetOnlineCountPacket() {
    }

    public GetOnlineCountPacket(Callback<Integer> callback) {
        super(callback);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
    }
}
