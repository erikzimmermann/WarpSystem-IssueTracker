package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                int i = 0;

                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().equals(sender.getName())) continue;
                    if(TeleportCommandManager.getInstance().deniesForceTps(player)) continue;

                    WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.CustomTeleportCommands, ((Player) sender).getLocation(), sender.getName(), true);
                    i++;
                }

                sender.sendMessage(Lang.getPrefix() + Lang.get("Teleport_all").replace("%AMOUNT%", i + "").replace("%MAX%", (Bukkit.getOnlinePlayers().size() - 1) + ""));
                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);
    }
}
