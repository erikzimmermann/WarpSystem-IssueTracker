package de.codingair.warpsystem.spigot.features.randomteleports.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CRandomTP extends CommandBuilder {
    public CRandomTP() {
        super("RandomTP", " WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_USE_RANDOM_TELEPORTER) {
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
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<buy, blocks, info, go>");
                    else
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<blocks, info, go>");
                } else {
                    if(RandomTeleporterManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<buy, info>");
                    else {
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<info>");
                    }
                }
                return false;
            }
        }.setOnlyPlayers(true), true, "rtp");

        if(RandomTeleporterManager.getInstance().isBuyable()) {
            getBaseComponent().addChild(new CommandComponent("buy") {
                @Override
                public boolean runCommand(CommandSender sender, String label, String[] args) {
                    //TextComponent

                    if(!RandomTeleporterManager.getInstance().canBuy((Player) sender)) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Bought_Too_Much").replace("%AMOUNT%", RandomTeleporterManager.getInstance().getMaxTeleportAmount((Player) sender) + ""));
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " blocks §e<add>");
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
                            .replace("%LEFT%", (free + bought - teleports) + "")
                            .replace("%ALL%", (free + bought) + ""));

                    if(RandomTeleporterManager.getInstance().isBuyable()) {
                        if(max == -1) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Buyable_Unlimited"));
                        } else {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Buyable")
                                    .replace("%LEFT%", (max - free - bought) + "")
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
                if(sender.hasPermission(WarpSystem.PERMISSION_MODIFY_RANDOM_TELEPORTER)) Lang.PREMIUM_CHAT(sender);
                else if(RandomTeleporterManager.getInstance().isBuyable())
                    sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<buy, info>");
                else {
                    sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<info>");
                }
                return false;
            }
        });
    }
}
