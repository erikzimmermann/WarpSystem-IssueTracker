package de.codingair.warpsystem.remastered.gui.affiliations;

import de.CodingAir.v1_6.CodingAPI.BungeeCord.BungeeCordHelper;
import de.CodingAir.v1_6.CodingAPI.Tools.Location;
import de.codingair.warpsystem.remastered.WarpSystem;
import de.codingair.warpsystem.remastered.gui.guis.GWarps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ActionIcon extends Icon implements Serializable {

    Category category;
    List<ActionObject> actions;

    public ActionIcon() {
    }

    public ActionIcon(String name, ItemStack item, int slot, Category category, ActionObject... actions) {
        super(name, item, slot);
        this.actions = Arrays.asList(actions);
        this.category = category;
    }

    public void perform(Player p) {
        for (ActionObject action : this.actions) {
            switch (action.getAction()) {
                case OPEN_CATEGORY: {
                    Category category = action.getValue();
                    new GWarps(p, category).open();
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
                    Location loc = action.getValue();
                    p.teleport(loc);
                    break;
                }
            }
        }
    }

    public List<ActionObject> getActions() {
        return actions;
    }

    public boolean isInCategory() {
        return category != null;
    }

    public Category getCategory() {
        return category;
    }
}
