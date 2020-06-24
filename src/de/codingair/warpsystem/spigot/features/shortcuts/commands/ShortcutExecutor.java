package de.codingair.warpsystem.spigot.features.shortcuts.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShortcutExecutor extends CommandBuilder {
    public ShortcutExecutor(Shortcut shortcut) {
        super(WarpSystem.getInstance(), shortcut.getDisplayName().toLowerCase(), "A WarpSystem-Command", new BaseComponent(shortcut.getPermission()) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(shortcut.isActive()) shortcut.perform((Player) sender);
                return false;
            }
        }.setOnlyPlayers(true), false);
    }
}
