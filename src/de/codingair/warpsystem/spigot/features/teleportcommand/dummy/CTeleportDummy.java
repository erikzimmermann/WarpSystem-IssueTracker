package de.codingair.warpsystem.spigot.features.teleportcommand.dummy;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.command.CommandSender;

public class CTeleportDummy extends CommandBuilder {
    public CTeleportDummy() {
        super("teleport", new BaseComponent() {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) { }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) { }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);
    }
}
