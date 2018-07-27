package de.codingair.warpsystem.transfer.serializeable.icons;

import de.codingair.warpsystem.transfer.serializeable.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SWarp extends SActionIcon implements Serializable {
    private String category;

    public SWarp() {
    }

    public SWarp(SActionIcon s) {
        super(s);
    }

    public SWarp(SIcon s) {
        super(s);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(this.category != null);
        if(this.category != null) out.writeUTF(this.category);

        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        if(in.readBoolean()) this.category = in.readUTF();

        super.read(in);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
