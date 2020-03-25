package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import org.bukkit.command.CommandSender;

public abstract class PWMultiCommandComponent extends MultiCommandComponent {
    public PWMultiCommandComponent() {
    }

    public PWMultiCommandComponent(String permission) {
        super(permission);
    }

    @Override
    public boolean matchTabComplete(CommandSender sender, String suggestion, String argument) {
        String[] a = argument.split("\\.", -1);
        argument = a[a.length - 1].toLowerCase();

        a = suggestion.split("\\.", -1);
        suggestion = a[a.length - 1].toLowerCase();

        return suggestion.startsWith(argument);
    }
}
