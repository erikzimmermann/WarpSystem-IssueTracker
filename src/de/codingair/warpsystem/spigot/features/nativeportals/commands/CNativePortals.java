package de.codingair.warpsystem.spigot.features.nativeportals.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Example;
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
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission", new Example("GER", "&cDu hast keine Berechtigungen für diese Aktion!"), new Example("ENG", "&cYou don't have permissions for that action!"), new Example("FRE", "&cDésolé mais vous ne possédez la permission pour exécuter cette action!")));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("NativePortals_Help", new Example("ENG", "&7Use: &e/%LABEL% <create, edit, delete>"), new Example("GER", "&7Benutze: &e/%LABEL% <create, edit, delete>")).replace("%LABEL%", label));
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("NativePortals_Help", new Example("ENG", "&7Use: &e/%LABEL% <create, edit, delete>"), new Example("GER", "&7Benutze: &e/%LABEL% <create, edit, delete>")).replace("%LABEL%", label));
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
                sender.sendMessage(Lang.getPrefix() + Lang.get("Go_To_NativePortal", new Example("ENG", "&7You have 30 seconds to go into a native portal."), new Example("GER", "&7Du hast nun 30 Sekunden Zeit, um in ein Ur-Portal zu gehen.")));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                NativePortalManager.getInstance().setGoingToDelete((Player) sender, 0);
                NativePortalManager.getInstance().setGoingToEdit((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Go_To_NativePortal", new Example("ENG", "&7You have 30 seconds to go into a native portal."), new Example("GER", "&7Du hast nun 30 Sekunden Zeit, um in ein Ur-Portal zu gehen.")));
                return false;
            }
        });
    }
}
