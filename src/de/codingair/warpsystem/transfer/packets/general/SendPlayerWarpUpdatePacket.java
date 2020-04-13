package de.codingair.warpsystem.transfer.packets.general;

import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpUpdate;
import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendPlayerWarpUpdatePacket implements Packet {
    private PlayerWarpUpdate update;
    private boolean clearable = false;

    public SendPlayerWarpUpdatePacket(PlayerWarpUpdate update) {
        this.update = update;
    }

    public SendPlayerWarpUpdatePacket() {
    }

    @Override
    public void write(DataOutputStream o) throws IOException {
        if(clearable) {
            update.write(o);
            update.destroy();
        } else {
            update.write(o);
        }
    }

    @Override
    public void read(DataInputStream i) throws IOException {
        update = new PlayerWarpUpdate();
        update.read(i);
    }

    public PlayerWarpUpdate getUpdate() {
        return update;
    }

    public boolean isClearable() {
        return clearable;
    }

    public void setClearable(boolean clearable) {
        this.clearable = clearable;
    }
}
