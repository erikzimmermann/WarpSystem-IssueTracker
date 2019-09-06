package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CTpaToggle extends CommandBuilder {
    public CTpaToggle() {
        super("TpaToggle", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA_TOGGLE) {
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
                if(TeleportCommandManager.getInstance().toggleDenyTpaRequest((Player) sender))
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_toggled_disabling"));
                else
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_toggled_enabling"));
                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);
    }
}
