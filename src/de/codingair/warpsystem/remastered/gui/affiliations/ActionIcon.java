package de.codingair.warpsystem.remastered.gui.affiliations;

import de.CodingAir.v1_6.CodingAPI.BungeeCord.BungeeCordHelper;
import de.CodingAir.v1_6.CodingAPI.Serializable.SerializableLocation;
import de.CodingAir.v1_6.CodingAPI.Tools.Location;
import de.codingair.warpsystem.remastered.WarpSystem;
import de.codingair.warpsystem.remastered.gui.guis.GWarps;
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

    public void perform(Player p, boolean editor) {
        for (ActionObject action : this.actions) {
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
                    //TODO: Teleport-Animation
                    SerializableLocation sLoc = action.getValue();
                    Location loc = Location.getByLocation(sLoc.getLocation());
                    p.teleport(loc);
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

    public void addAction(ActionObject action) {
        this.actions.add(action);
    }

    public void removeAction(ActionObject action) {
        this.actions.remove(action);
    }

    public void addAllActions(Collection<ActionObject> actions) {
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
