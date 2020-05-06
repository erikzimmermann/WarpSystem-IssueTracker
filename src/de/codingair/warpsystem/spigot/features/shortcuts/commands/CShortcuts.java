package de.codingair.warpsystem.spigot.features.shortcuts.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.shortcuts.guis.GEditor;
import de.codingair.warpsystem.spigot.features.shortcuts.managers.ShortcutManager;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CShortcuts extends WSCommandBuilder {
    public CShortcuts() {
        super("Shortcuts", new BaseComponent(WarpSystem.PERMISSION_MODIFY_SHORTCUTS) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, edit, delete, list>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create edit, delete, list>");
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " create §e<name>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Shortcut shortcut = new Shortcut(null, argument.toLowerCase());
                Shortcut clone = shortcut.clone();
                clone.addAction(new WarpAction(new Destination()));

                new GEditor((Player) sender, shortcut, clone).open();
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " edit §e<name>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("edit").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Shortcut shortcut : ShortcutManager.getInstance().getShortcuts()) {
                    suggestions.add(shortcut.getDisplayName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Shortcut shortcut = ShortcutManager.getInstance().getShortcut(argument);

                if(shortcut == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_does_not_exist"));
                    return false;
                }

                Shortcut clone = shortcut.clone();
                if(clone.getDestination() == null) clone.addAction(new WarpAction(new Destination()));

                new GEditor((Player) sender, shortcut, clone).open();
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " delete §e<name>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("delete").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Shortcut shortcut : ShortcutManager.getInstance().getShortcuts()) {
                    suggestions.add(shortcut.getDisplayName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Shortcut shortcut = ShortcutManager.getInstance().getShortcut(argument);

                if(shortcut == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_does_not_exist"));
                    return false;
                }

                ShortcutManager.getInstance().remove(shortcut, true);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_was_removed").replace("%SHORTCUT%", shortcut.getDisplayName()));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("list") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                List<String> message = new ArrayList<>();

                if(ShortcutManager.getInstance().getShortcuts().isEmpty()) {
                    message.add(" ");
                    message.add("  §3§lShortcuts: §c-");
                    message.add(" ");

                    sender.sendMessage(message.toArray(new String[0]));
                    return false;
                }

                for(Shortcut shortcut : ShortcutManager.getInstance().getShortcuts()) {
                    if(shortcut.getDestination() == null || shortcut.getDestination().getId() == null)
                        message.add("  §7\"§e" + shortcut.getDisplayName() + "§7\" » §c-");
                    else message.add("  §7\"§e" + shortcut.getDisplayName() + "§7\" » §b" + shortcut.getDestination().getId() + " §7(" + shortcut.getDestination().getType() + ")");
                }

                Collections.sort(message);

                message.add(0, " ");
                message.add(1, " ");
                message.add(2, "  §3§l§nShortcuts");
                message.add(3, " ");
                message.add(" ");

                sender.sendMessage(message.toArray(new String[0]));
                return false;
            }
        });
    }
}
