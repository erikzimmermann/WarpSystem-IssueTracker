package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendDisablePacket implements Packet {
    private String player;
    private String feature;

    public SendDisablePacket() {
    }

    public SendDisablePacket(String player, String feature) {
        this.player = player;
        this.feature = feature;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(player);
        out.writeUTF(feature);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        player = in.readUTF();
        feature = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getFeature() {
        return feature;
    }
}
