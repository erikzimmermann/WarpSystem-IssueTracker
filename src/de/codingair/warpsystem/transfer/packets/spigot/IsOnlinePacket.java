package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IsOnlinePacket extends RequestPacket<Boolean> {
    private String name;

    public IsOnlinePacket() {
    }

    public IsOnlinePacket(Callback<Boolean> callback, String name) {
        super(callback);
        this.name = name;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(name);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        this.name = in.readUTF();
    }

    public String getName() {
        return name;
    }
}
