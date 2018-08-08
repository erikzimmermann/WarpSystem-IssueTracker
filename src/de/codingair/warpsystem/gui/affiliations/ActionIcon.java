package de.codingair.warpsystem.gui.affiliations;

import de.codingair.codingapi.bungeecord.BungeeCordHelper;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.gui.guis.GWarps;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import de.codingair.warpsystem.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.transfer.serializeable.icons.SActionIcon;
import de.codingair.warpsystem.transfer.serializeable.icons.SActionObject;
import de.codingair.warpsystem.transfer.serializeable.icons.SIcon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class ActionIcon extends Icon implements Serializable {
    static final long serialVersionUID = 1L;

    String permission;
    List<ActionObject> actions;

    public ActionIcon() {
    }

    public ActionIcon(String name, ItemStack item, int slot) {
        super(name, item, slot);
        actions = new ArrayList<>();
        permission = null;
    }

    public ActionIcon(String name, ItemStack item, int slot, String permission) {
        super(name, item, slot);
        actions = new ArrayList<>();
        this.permission = permission;
    }

    public ActionIcon(String name, ItemStack item, int slot, List<ActionObject> actions) {
        this(name, item, slot);
        this.actions = actions;
    }

    public ActionIcon(String name, ItemStack item, int slot, String permission, List<ActionObject> actions) {
        this(name, item, slot, permission);
        this.actions = actions;
    }

    public ActionIcon(String name, ItemStack item, int slot, ActionObject... actions) {
        this(name, item, slot, Arrays.asList(actions));
    }

    public ActionIcon(String name, ItemStack item, int slot, String permission, ActionObject... actions) {
        this(name, item, slot, permission, Arrays.asList(actions));
    }

    public ActionIcon(SActionIcon icon) {
        super(icon);
        this.permission = icon.getPermission();
        this.actions = new ArrayList<>();
        for(SActionObject s : icon.getActions()) {
            this.actions.add(ActionIconHelper.translate(s));
        }
    }

    @Override
    public SIcon getSerializable() {
        SActionIcon s = new SActionIcon(super.getSerializable());

        s.setPermission(this.permission);
        List<SActionObject> actions = new ArrayList<>();
        for(ActionObject action : this.actions) {
            actions.add(ActionIconHelper.translate(action));
        }
        s.setActions(actions);

        return s;
    }

    public void perform(Player p, boolean editor, Action... without) {
        List<Action> withouts = Arrays.asList(without);

        for(ActionObject action : this.actions) {
            if(withouts.contains(action.getAction())) continue;

            switch(action.getAction()) {
                case OPEN_CATEGORY: {
                    Category category = action.getValue();
                    new GWarps(p, category, editor).open();
                    break;
                }

                case RUN_COMMAND: {
                    String command = action.getValue();
                    if(command.startsWith("/")) command = command.substring(1);
                    p.performCommand(command);
                    break;
                }

                case SWITCH_SERVER: {
                    if(WarpSystem.getInstance().getTeleportManager().isTeleporting(p)) {
                        p.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting", new Example("ENG", "&cYou are already teleporting!"), new Example("GER", "&cDu wirst bereits teleportiert!")));
                        return;
                    }

                    String server = action.getValue();
                    WarpSystem.getInstance().getTeleportManager().teleport(p, server, getName());
                    break;
                }

                case TELEPORT_TO_WARP: {
                    if(!(this instanceof Warp)) return;

                    WarpSystem.getInstance().getTeleportManager().teleport(p, (Warp) this);
                    break;
                }
            }
        }
    }

    public void perform(Player p) {
        perform(p, false);
    }

    public List<ActionObject> getActions() {
        return actions;
    }

    public ActionObject getAction(Action action) {
        for(ActionObject actionObject : this.actions) {
            if(actionObject.getAction().equals(action)) return actionObject;
        }

        return null;
    }

    private void checkList() {
        if(!(this.actions instanceof ArrayList)) {
            List<ActionObject> actionObjects = new ArrayList<>();
            actionObjects.addAll(this.actions);
            this.actions = actionObjects;
        }
    }

    public void addAction(ActionObject action) {
        checkList();
        this.actions.add(action);
    }

    public void removeAction(Action action) {
        checkList();
        ActionObject object = null;

        for(ActionObject actionObject : actions) {
            if(actionObject.getAction().equals(action)) {
                object = actionObject;
                break;
            }
        }

        if(object != null) actions.remove(object);
    }

    public void addAllActions(Collection<ActionObject> actions) {
        checkList();
        this.actions.addAll(actions);
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean hasPermission() {
        return permission != null;
    }
}
