package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import de.codingair.warpsystem.transfer.packets.spigot.GetOnlineCountPacket;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class CTpaAll extends CommandBuilder {
    public CTpaAll() {
        super("tpaall", "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA_ALL) {
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

                        if(WarpSystem.getInstance().isOnBungeeCord()) {
                            WarpSystem.getInstance().getDataHandler().send(new GetOnlineCountPacket(new Callback<Integer>() {
                                @Override
                                public void accept(Integer count) {
                                    int sent = result.intValue();
                                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_All").replace("%RECEIVED%", sent + "").replace("%MAX%", (count - 1) + ""));
                                    TextComponent tc = new TextComponent(Lang.getPrefix() + "§6" + (count - 1 - handled) + "§7 player(s) on §6different servers§7!");
                                    tc.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                                    Lang.PREMIUM_CHAT(tc, sender, true);
                                }
                            }));
                        } else {
                            int sent = result.intValue();
                            sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_All").replace("%RECEIVED%", sent + "").replace("%MAX%", handled + ""));
                        }
                    }
                }, null);
                return false;
            }
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);
    }
}
