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
        String player = a[0].toLowerCase();
        argument = a[a.length - 1].toLowerCase();

        a = suggestion.split("\\.", -1);
        String suggestedPlayer = a[0].toLowerCase();
        suggestion = a[a.length - 1].toLowerCase();

        return suggestedPlayer.startsWith(player) || suggestion.startsWith(argument);
    }
}
