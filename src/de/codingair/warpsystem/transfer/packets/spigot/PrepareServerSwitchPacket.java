package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareServerSwitchPacket extends RequestPacket<Integer> {
    private String player;
    private String server;
    private String message = null;

    public PrepareServerSwitchPacket() {
    }

    public PrepareServerSwitchPacket(String player, String server, Callback<Integer> callback) {
        super(callback);
        this.player = player;
        this.server = server;
    }

    public PrepareServerSwitchPacket(String player, String server, String message, Callback<Integer> callback) {
        super(callback);
        this.player = player;
        this.server = server;
        this.message = message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(player);
        out.writeUTF(server);
        out.writeBoolean(message != null);
        if(message != null) out.writeUTF(message);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        player = in.readUTF();
        server = in.readUTF();
        if(in.readBoolean()) message = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public String getMessage() {
        return message;
    }
}
