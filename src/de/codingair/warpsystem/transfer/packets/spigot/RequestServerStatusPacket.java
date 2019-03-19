package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestServerStatusPacket extends RequestPacket<Boolean> {
    private String server;

    public RequestServerStatusPacket() {
    }

    public RequestServerStatusPacket(String server, Callback<Boolean> callback) {
        super(callback);
        this.server = server;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(server);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        server = in.readUTF();
    }

    public String getServer() {
        return server;
    }
}
