package de.codingair.warpsystem.transfer.serializeable.icons;

import de.codingair.warpsystem.transfer.serializeable.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SActionIcon extends SIcon implements Serializable {
    private String permission;
    private List<SActionObject> actions;

    public SActionIcon() {
    }

    public SActionIcon(SActionIcon s) {
        this.permission = s.permission;
        this.actions = s.actions;
    }

    public SActionIcon(SIcon s) {
        super(s);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(this.permission != null);
        if(this.permission != null) out.writeUTF(this.permission);

        out.writeInt(this.actions == null ? 0 : this.actions.size());
        if(this.actions != null) for(SActionObject action : this.actions) {
            action.write(out);
        }

        super.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        if(in.readBoolean()) this.permission = in.readUTF();
        int size = in.readInt();
        this.actions = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            SActionObject action = new SActionObject();
            action.read(in);
            this.actions.add(action);
        }

        super.read(in);
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<SActionObject> getActions() {
        return actions;
    }

    public void setActions(List<SActionObject> actions) {
        this.actions = actions;
    }
}
