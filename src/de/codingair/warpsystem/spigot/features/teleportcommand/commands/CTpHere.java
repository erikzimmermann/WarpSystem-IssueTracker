package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TabCompleterListener;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CTpHere extends WSCommandBuilder {
    public CTpHere(CTeleport teleportCommand) {
        super("TpHere", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpHere <§eplayer§7>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tpHere <§eplayer§7>");
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public boolean matchTabComplete(CommandSender sender, String suggestion, String argument) {
                return WarpSystem.getInstance().isOnBungeeCord() || super.matchTabComplete(sender, suggestion, argument);
            }

            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                Player p = (Player) sender;
                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    suggestions.add(TabCompleterListener.ID);

                    StringBuilder builder = new StringBuilder("tpa");
                    for(String arg : args) {
                        builder.append(" ").append(arg);
                    }
                    suggestions.add(builder.toString());

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(!p.canSee(player)) {
                            suggestions.add("-" + player.getName());
                        }
                    }
                } else {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(player.getName().equals(sender.getName())) continue;
                        suggestions.add(ChatColor.stripColor(player.getName()));
                    }
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                teleportCommand.getBaseComponent().runCommand(sender, label, new String[] {args[0], sender.getName()});
                return false;
            }
        });
    }
}
