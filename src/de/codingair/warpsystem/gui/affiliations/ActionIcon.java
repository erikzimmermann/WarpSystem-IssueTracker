package de.codingair.warpsystem.gui.affiliations;

import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import de.codingair.warpsystem.spigot.utils.money.AdapterType;
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

    public String getPermission() {
        return permission;
    }

    public List<ActionObject> getActions() {
        return actions;
    }
}
