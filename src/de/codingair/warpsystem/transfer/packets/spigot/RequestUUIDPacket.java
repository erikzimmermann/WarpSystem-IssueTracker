package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class RequestUUIDPacket extends RequestPacket<UUID> {
    private String name;

    public RequestUUIDPacket() {
    }

    public RequestUUIDPacket(String name, Callback<UUID> callback) {
        super(callback);
        this.name = name;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        super.read(in);
    }

    public String getName() {
        return name;
    }
}
