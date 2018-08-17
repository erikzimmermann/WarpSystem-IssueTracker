package de.codingair.warpsystem.transfer.serializeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SGlobalWarp implements Serializable {
    private String name;
    private String server;
    private SLocation loc;

    public SGlobalWarp() {
    }

    public SGlobalWarp(String name, SLocation loc) {
        this.name = name;
        this.server = null;
        this.loc = loc;
    }

    public SGlobalWarp(String name, String server, SLocation loc) {
        this.name = name;
        this.server = server;
        this.loc = loc;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeBoolean(this.server != null);
        if(this.server != null) out.writeUTF(this.server);
        this.loc.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        if(in.readBoolean()) this.server = in.readUTF();
        this.loc = new SLocation();
        this.loc.read(in);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public SLocation getLoc() {
        return loc;
    }

    public void setLoc(SLocation loc) {
        this.loc = loc;
    }
}
