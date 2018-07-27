package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.transfer.packets.utils.RequestPacket;
import de.codingair.warpsystem.transfer.serializeable.SGlobalWarp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PublishGlobalWarpPacket extends RequestPacket<Boolean> {
    public SGlobalWarp warp;

    public PublishGlobalWarpPacket() {
        super(null);
    }

    public PublishGlobalWarpPacket(SGlobalWarp warp, Callback<Boolean> callback) {
        super(callback);
        this.warp = warp;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        this.warp.write(out);
        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.warp = new SGlobalWarp();
        this.warp.read(in);
        super.read(in);
    }
}
