package de.codingair.warpsystem.spigot.features.randomteleports.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CRandomTP extends CommandBuilder {
    public CRandomTP() {
        super("RandomTP", new BaseComponent(WarpSystem.PERMISSION_USE_RANDOM_TELEPORTER) {
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
                //sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                RandomTeleporterManager.getInstance().tryToTeleport((Player) sender);
                return false;
            }
        }.setOnlyPlayers(true), true);

        getBaseComponent().addChild(new CommandComponent("blocks", WarpSystem.PERMISSION_MODIFY_RANDOM_TELEPORTER) {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " blocks §e<add>");
                return false;
            }
        });

        getComponent("blocks").addChild(new CommandComponent("add") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                RandomTeleporterManager.getInstance().getListener().getAddingNewBlock().remove(sender);
                RandomTeleporterManager.getInstance().getListener().getAddingNewBlock().add((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Adding_New_Block"));
                return false;
            }
        });
    }
}
