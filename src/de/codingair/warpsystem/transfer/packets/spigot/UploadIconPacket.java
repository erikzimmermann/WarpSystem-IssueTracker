package de.codingair.warpsystem.transfer.packets.spigot;

import de.codingair.warpsystem.transfer.packets.utils.Packet;
import de.codingair.warpsystem.transfer.serializeable.icons.SIcon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UploadIconPacket implements Packet {
    public SIcon[] icon;

    public UploadIconPacket() {
    }

    public UploadIconPacket(SIcon... icon) {
        this.icon = icon;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(icon.length);
        for(int i = 0; i < icon.length; i++) {
            out.writeUTF(icon[i].getClass().getName());
            icon[i].write(out);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        try {
            int size = in.readInt();
            this.icon = new SIcon[size];

            for(int i = 0; i < size; i++) {
                SIcon icon = (SIcon) Class.forName(in.readUTF()).newInstance();
                icon.read(in);
                this.icon[i] = icon;
            }

        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
