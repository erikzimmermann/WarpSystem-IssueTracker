package de.codingair.warpsystem.spigot.features.playerwarps.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import org.bukkit.command.CommandSender;

public class CTempWarps extends CommandBuilder {
    public CTempWarps() {
        super("tempwarps", "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_USE_TEMP_WARPS) {
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
                sender.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Reference_To_PlayerWarps"));
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Reference_To_PlayerWarps"));
                return false;
            }
        }, true, "tws", "twarps");
    }
}
