package de.codingair.warpsystem.gui.affiliations;

import de.codingair.codingapi.bungeecord.BungeeCordHelper;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.gui.guis.GWarps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class ActionIcon extends Icon implements Serializable {
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

    public void perform(Player p, boolean editor, Action... without) {
        List<Action> withouts = Arrays.asList(without);

        for (ActionObject action : this.actions) {
            if(withouts.contains(action.getAction())) continue;

            switch (action.getAction()) {
                case OPEN_CATEGORY: {
                    Category category = action.getValue();
                    new GWarps(p, category, editor).open();
                    break;
                }

                case RUN_COMMAND: {
                    String command = action.getValue();
                    p.performCommand(command);
                    break;
                }

                case SWITCH_SERVER: {
                    String server = action.getValue();
                    BungeeCordHelper.connect(p, server, WarpSystem.getInstance());
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
