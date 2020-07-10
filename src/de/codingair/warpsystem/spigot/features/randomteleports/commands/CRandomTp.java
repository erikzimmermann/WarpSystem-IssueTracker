package de.codingair.warpsystem.spigot.features.randomteleports.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CRandomTp extends WSCommandBuilder {
    public CRandomTp() {
        super("RandomTp", new BaseComponent(WarpSystem.PERMISSION_USE_RANDOM_TELEPORTER) {
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
                runCommand(sender, label, args);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(sender.hasPermission(WarpSystem.PERMISSION_MODIFY_RANDOM_TELEPORTER)) {
                    if(RandomTeleporterManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<buy, blocks, info, go>");
                    else
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<blocks, info, go>");
                } else {
                    if(RandomTeleporterManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<buy, info, go>");
                    else {
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<info, go>");
                    }
                }
                return false;
            }
        }.setOnlyPlayers(true));

        if(RandomTeleporterManager.getInstance().isBuyable()) {
            getBaseComponent().addChild(new CommandComponent("buy") {
                @Override
                public boolean runCommand(CommandSender sender, String label, String[] args) {
                    //TextComponent

                    if(!RandomTeleporterManager.getInstance().canBuy((Player) sender)) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Bought_Too_Much").replace("%AMOUNT%", RandomTeleporterManager.getInstance().getMaxTeleportAmount((Player) sender) + ""));
                        return false;
                    } else if(RandomTeleporterManager.getInstance().getFreeTeleportAmount((Player) sender) == -1) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Unlimited"));
                        return false;
                    }

                    double bank = MoneyAdapterType.getActive().getMoney((Player) sender);
                    double costs = RandomTeleporterManager.getInstance().getCosts();

                    if(bank >= costs) {
                        SimpleMessage sm = new SimpleMessage(Lang.getPrefix() + Lang.get("RandomTP_Buy").replace("%AMOUNT%", (costs + "").endsWith(".0") ? (costs + "").substring(0, (costs + "").length() - 2) : (costs + "")), WarpSystem.getInstance());

                        sm.replace("%YES%", new ChatButton(Lang.get("RandomTP_Buy_Yes")) {
                            @Override
                            public void onClick(Player player) {
                                sm.destroy();

                                double bank = MoneyAdapterType.getActive().getMoney((Player) sender);

                                if(bank >= costs) {
                                    MoneyAdapterType.getActive().withdraw(player, costs);
                                    UUID u = WarpSystem.getInstance().getUUIDManager().get((Player) sender);
                                    RandomTeleporterManager.getInstance().setBoughtTeleports(u, RandomTeleporterManager.getInstance().getBoughtTeleports(u) + 1);
                                    sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Buy_Finished").replace("%AMOUNT%", (costs + "").endsWith(".0") ? (costs + "").substring(0, (costs + "").length() - 2) : (costs + "")));
                                } else {
                                    sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Not_Enough_Money").replace("%AMOUNT%", (costs + "").endsWith(".0") ? (costs + "").substring(0, (costs + "").length() - 2) : (costs + "")));
                                }
                            }
                        }.setHover(Lang.get("Click_Hover")));

                        sm.replace("%NO%", new ChatButton(Lang.get("RandomTP_Buy_No")) {
                            @Override
                            public void onClick(Player player) {
                                sm.destroy();
                                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Buy_Cancelled"));
                            }
                        }.setHover(Lang.get("Click_Hover")));

                        sm.send((Player) sender);
                    } else {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Not_Enough_Money").replace("%AMOUNT%", (costs + "").endsWith(".0") ? (costs + "").substring(0, (costs + "").length() - 2) : (costs + "")));
                    }
                    return false;
                }
            });
        }

        getBaseComponent().addChild(new CommandComponent("blocks", WarpSystem.PERMISSION_MODIFY_RANDOM_TELEPORTER) {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " blocks " + WarpSystem.opt().cmdArg() + "<add>");
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

        getComponent("blocks").addChild(new CommandComponent("remove") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Block_Destroy_Info"));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("info") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                int free = RandomTeleporterManager.getInstance().getFreeTeleportAmount((Player) sender);
                int bought = RandomTeleporterManager.getInstance().getBoughtTeleports((Player) sender);
                int teleports = RandomTeleporterManager.getInstance().getTeleports((Player) sender);
                int max = RandomTeleporterManager.getInstance().getMaxTeleportAmount((Player) sender);

                if(free == -1) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Unlimited"));
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info")
                            .replace("%LEFT%", Math.max(free + bought - teleports, 0) + "")
                            .replace("%ALL%", (free + bought) + ""));

                    if(RandomTeleporterManager.getInstance().isBuyable()) {
                        if(max == -1) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Buyable_Unlimited"));
                        } else {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Buyable")
                                    .replace("%LEFT%", Math.max(max - free - bought, 0) + "")
                                    .replace("%ALL%", (max - free) + ""));
                        }
                    }
                }
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("go") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " go " + WarpSystem.opt().cmdArg() + "[server-1, server-2, ...; world-1, world-2, ...] <player>");
                    return false;
                }

                RandomTeleporterManager.getInstance().tryToTeleport((Player) sender);
                return false;
            }
        }.setOnlyPlayers(false).addChild(new RTP_Go_Command(WarpSystem.PERMISSION_RANDOM_TELEPORT_SELECTION_SELF)));
    }
}
