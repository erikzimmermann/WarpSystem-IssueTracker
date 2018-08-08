package de.codingair.warpsystem.transfer.packets.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UpdateGlobalWarpPacket implements Packet {
    private int action;
    private String name;
    private String server;

    public UpdateGlobalWarpPacket() {
    }

    public UpdateGlobalWarpPacket(int action, String name, String server) {
        this.action = action;
        this.name = name;
        this.server = server;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(action);
        out.writeUTF(name);
        out.writeUTF(server);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        action = in.readInt();
        name = in.readUTF();
        server = in.readUTF();
    }

    public Action getAction() {
        return Action.getById(this.action);
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public enum Action {
        ADD(0),
        DELETE(1);

        private int id;

        Action(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Action getById(int id) {
            for(Action a : values()) {
                if(a.getId() == id) return a;
            }

            return null;
        }
    }
}
