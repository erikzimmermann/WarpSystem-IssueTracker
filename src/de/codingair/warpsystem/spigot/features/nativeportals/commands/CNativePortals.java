package de.codingair.warpsystem.spigot.features.nativeportals.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.GEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.managers.NativePortalManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CNativePortals extends CommandBuilder {
    public CNativePortals() {
        super("NativePortals", new BaseComponent(WarpSystem.PERMISSION_MODIFY_NATIVE_PORTALS) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, edit, delete>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, edit, delete>");
                return false;
            }
        }, true);

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                new GEditor((Player) sender).open();
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                NativePortalManager.getInstance().setGoingToEdit((Player) sender, 0);
                NativePortalManager.getInstance().setGoingToDelete((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Go_To_NativePortal"));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                NativePortalManager.getInstance().setGoingToDelete((Player) sender, 0);
                NativePortalManager.getInstance().setGoingToEdit((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Go_To_NativePortal"));
                return false;
            }
        });
    }
}
