package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.command.CommandSender;

public class CTpaAll extends WSCommandBuilder {
    public CTpaAll() {
        super("TpaAll", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA_ALL) {
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
                TeleportCommandManager.getInstance().invite(sender.getName(), true, new Callback<Long>() {
                    @Override
                    public void accept(Long result) {
                        int handled = (int) (result >> 32);
                        int sent = result.intValue();
                        sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_All").replace("%RECEIVED%", sent + "").replace("%MAX%", handled + ""));
                    }
                }, null);
                return false;
            }
        }.setOnlyPlayers(true));
    }
}
