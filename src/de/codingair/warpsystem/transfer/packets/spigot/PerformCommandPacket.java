package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PerformCommandPacket extends RequestPacket<Boolean> {
    private String player;
    private String command;

    public PerformCommandPacket() {
    }

    public PerformCommandPacket(String player, String command, Callback<Boolean> callback) {
        super(callback);
        this.player = player;
        this.command = command;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeUTF(this.command);
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.command = in.readUTF();
        super.read(in);
    }

    public String getPlayer() {
        return player;
    }

    public String getCommand() {
        return command;
    }
}
