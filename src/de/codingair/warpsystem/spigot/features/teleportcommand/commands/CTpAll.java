package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.ad.features.utils.Feature;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.command.CommandSender;

public class CTpAll extends CommandBuilder {
    public CTpAll() {
        super("tpall", "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPALL) {
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
                Lang.PREMIUM_CHAT(sender);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Lang.PREMIUM_CHAT(sender);
                WarpSystem.getInstance().getAdvertisementManager().sendDisableMessage(sender, Feature.TELEPORT_COMMANDS);
                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);
    }
}
