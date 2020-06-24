package de.codingair.warpsystem.spigot.base.setupassistant.utils;

import de.codingair.codingapi.server.commands.builder.special.NaturalCommandComponent;
import de.codingair.warpsystem.spigot.base.setupassistant.SetupAssistantManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class NavigationCommand extends NaturalCommandComponent {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean runCommand(CommandSender sender, String label, String[] args) {
        SetupAssistant a = SetupAssistantManager.getInstance().getAssistant();
        if(a.getPlayer().equals(sender)) {
            a.process(args[args.length - 1]);
        }
        return false;
    }
}
