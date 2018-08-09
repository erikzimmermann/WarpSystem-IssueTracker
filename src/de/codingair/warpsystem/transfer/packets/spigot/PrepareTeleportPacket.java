package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareTeleportPacket extends RequestPacket<Integer> {
    private String player;
    private String teleportName;
    private String displayName;
    private double costs;

    public PrepareTeleportPacket() {
    }

    public PrepareTeleportPacket(String player, String teleportName, String displayName, double costs, Callback<Integer> callback) {
        super(callback);
        this.player = player;
        this.teleportName = teleportName;
        this.displayName = displayName;
        this.costs = costs;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeUTF(this.teleportName);
        out.writeUTF(this.displayName);
        out.writeDouble(this.costs);
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.teleportName = in.readUTF();
        this.displayName = in.readUTF();
        this.costs = in.readDouble();
        super.read(in);
    }

    public String getPlayer() {
        return player;
    }

    public String getTeleportName() {
        return teleportName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCosts() {
        return costs;
    }

    public enum Result {
        TELEPORTED(0),
        WARP_NOT_EXISTS(1),
        SERVER_NOT_AVAILABLE(2),
        PLAYER_ALREADY_ON_SERVER(3),
        ;

        private int id;

        Result(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Result getById(int id) {
            for(Result value : values()) {
                if(value.getId() == id) return value;
            }

            return null;
        }
    }
}
