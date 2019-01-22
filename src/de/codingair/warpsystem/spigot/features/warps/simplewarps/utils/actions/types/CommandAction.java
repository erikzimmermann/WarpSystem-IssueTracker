package de.codingair.warpsystem.spigot.features.warps.simplewarps.utils.actions.types;

import de.codingair.warpsystem.spigot.features.warps.simplewarps.utils.actions.Action;
import org.bukkit.entity.Player;

public class CommandAction implements Action {
    private String command;

    public CommandAction(String command) {
        this.command = command;
    }

    public CommandAction() {
    }

    @Override
    public void onRun(Player player) {
        player.performCommand(this.command);
    }

    @Override
    public void byString(String s) {
        this.command = s;
    }

    @Override
    public String toString() {
        return "CommandAction/" + this.command;
    }
}
