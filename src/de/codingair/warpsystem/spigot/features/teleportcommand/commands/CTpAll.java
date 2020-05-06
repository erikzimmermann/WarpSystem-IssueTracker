package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import de.codingair.warpsystem.transfer.packets.spigot.GetOnlineCountPacket;
import net.md_5.bungee.api.chat.TextComponent;
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
                int iHandled = 0;
                int iSent = 0;

                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().equals(sender.getName())) continue;
                    iHandled++;
                    if(TeleportCommandManager.getInstance().deniesForceTps(player)) continue;

                    WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.CustomTeleportCommands, ((Player) sender).getLocation(), sender.getName(), true);
                    iSent++;
                }

                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    int finalISent = iSent;
                    WarpSystem.getInstance().getDataHandler().send(new GetOnlineCountPacket(new Callback<Integer>() {
                        @Override
                        public void accept(Integer count) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Teleport_all").replace("%AMOUNT%", finalISent + "").replace("%MAX%", (count - 1) + ""));
                            TextComponent tc = new TextComponent(Lang.getPrefix() + "§6" + (count - 1 - finalISent) + "§7 player(s) on §6different servers§7!");
                            tc.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                            Lang.PREMIUM_CHAT(tc, sender, true);
                        }
                    }));
                } else sender.sendMessage(Lang.getPrefix() + Lang.get("Teleport_all").replace("%AMOUNT%", iSent + "").replace("%MAX%", iHandled + ""));
                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);
    }
}
