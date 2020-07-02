package de.codingair.warpsystem.spigot.base.setupassistant.bungee;

import de.codingair.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToggleSetupAssistantPacket implements Packet {
    private String name;

    public ToggleSetupAssistantPacket() {
        this.name = "";
    }

    public ToggleSetupAssistantPacket(String name) {
        this.name = name == null ? "" : name;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
    }

    public String getName() {
        return name.isEmpty() ? null : name;
    }
}
