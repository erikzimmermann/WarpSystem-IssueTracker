package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeleteGlobalWarpPacket extends RequestPacket<Boolean> {
    private String warp;

    public DeleteGlobalWarpPacket() {
    }

    public DeleteGlobalWarpPacket(String warp, Callback<Boolean> callback) {
        super(callback);
        this.warp = warp;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.warp);
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.warp = in.readUTF();
        super.read(in);
    }

    public String getWarp() {
        return warp;
    }
}
