package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class GlobalWarpTeleportPacket extends RequestPacket<Integer> {
    private String player;
    private String id;
    private String displayName;
    private double costs, randomOffsetX, randomOffsetY, randomOffsetZ;
    private String message;

    public GlobalWarpTeleportPacket() {
    }

    public GlobalWarpTeleportPacket(String player, String id, double randomOffsetX, double randomOffsetY, double randomOffsetZ, String displayName, String message, double costs, Callback<Integer> callback) {
        super(callback);
        this.player = player;
        this.id = id;

        this.displayName = displayName;
        this.costs = costs;
        this.randomOffsetX = randomOffsetX;
        this.randomOffsetY = randomOffsetY;
        this.randomOffsetZ = randomOffsetZ;
        this.message = message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte options = (byte) (displayName == null || displayName.equals(id) ? 0 : 1);
        options |= (costs > 0 ? 1 : 0) << 1;
        options |= (randomOffsetX > 0 ? 1 : 0) << 2;
        options |= (randomOffsetY > 0 ? 1 : 0) << 3;
        options |= (randomOffsetZ > 0 ? 1 : 0) << 4;
        options |= (message != null ? 1 : 0) << 5;
        out.writeByte(options);

        out.writeUTF(this.player);
        out.writeUTF(this.id);

        if(displayName != null && !displayName.equals(id)) out.writeUTF(this.displayName);
        if(costs > 0) out.writeDouble(this.costs);
        if(randomOffsetX > 0) out.writeDouble(this.randomOffsetX);
        if(randomOffsetY > 0) out.writeDouble(this.randomOffsetY);
        if(randomOffsetZ > 0) out.writeDouble(this.randomOffsetZ);
        if(message != null) out.writeUTF(this.message);

        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte options = in.readByte();

        this.player = in.readUTF();
        this.id = in.readUTF();

        if((options & 1) != 0) this.displayName = in.readUTF();
        if((options & (1 << 1)) != 0) this.costs = in.readDouble();
        if((options & (1 << 2)) != 0) this.randomOffsetX = in.readDouble();
        if((options & (1 << 3)) != 0) this.randomOffsetY = in.readDouble();
        if((options & (1 << 4)) != 0) this.randomOffsetZ = in.readDouble();
        if((options & (1 << 5)) != 0) this.message = in.readUTF();

        super.read(in);
    }

    public String getPlayer() {
        return player;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCosts() {
        return costs;
    }

    public double getRandomOffsetX() {
        return randomOffsetX;
    }

    public double getRandomOffsetY() {
        return randomOffsetY;
    }

    public double getRandomOffsetZ() {
        return randomOffsetZ;
    }

    public String getMessage() {
        return message;
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

        public static Result getById(int id) {
            for(Result value : values()) {
                if(value.getId() == id) return value;
            }

            return null;
        }

        public int getId() {
            return id;
        }
    }
}
