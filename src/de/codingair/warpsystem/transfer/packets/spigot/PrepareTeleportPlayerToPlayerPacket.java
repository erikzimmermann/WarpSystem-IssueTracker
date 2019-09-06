package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareTeleportPlayerToPlayerPacket extends RequestPacket<Integer> {
    private String player;
    private String destinationPlayer;

    public PrepareTeleportPlayerToPlayerPacket() {
    }

    public PrepareTeleportPlayerToPlayerPacket(String player, String destinationPlayer, Callback<Integer> callback) {
        super(callback);
        this.player = player;
        this.destinationPlayer = destinationPlayer;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(player);
        out.writeUTF(destinationPlayer);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        this.player = in.readUTF();
        this.destinationPlayer = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getDestinationPlayer() {
        return destinationPlayer;
    }
}
