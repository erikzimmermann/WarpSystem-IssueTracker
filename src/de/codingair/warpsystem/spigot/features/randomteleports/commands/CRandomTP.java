package de.codingair.warpsystem.spigot.features.randomteleports.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CRandomTP extends CommandBuilder {
    public CRandomTP() {
        super("RandomTP", new BaseComponent(WarpSystem.PERMISSION_USE_RANDOM_TELEPORTER) {
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
                if(sender.hasPermission(WarpSystem.PERMISSION_MODIFY_RANDOM_TELEPORTER)) {
                    if(RandomTeleporterManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<buy, blocks>");
                    else
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<blocks>");
                } else {
                    if(RandomTeleporterManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<buy>");
                    else runCommand(sender, label, args);
                }
            }

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
        }.setOnlyPlayers(true), true);

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

        if(RandomTeleporterManager.getInstance().isBuyable()) {
            getBaseComponent().addChild(new CommandComponent("buy") {
                @Override
                public boolean runCommand(CommandSender sender, String label, String[] args) {
                    //TextComponent

                    if(!RandomTeleporterManager.getInstance().canBuy((Player) sender)) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Bought_Too_Much").replace("%AMOUNT%", RandomTeleporterManager.getInstance().getMaxTeleportAmount((Player) sender) + ""));
                        return false;
                    }

                    double bank = AdapterType.getActive().getMoney((Player) sender);
                    double costs = RandomTeleporterManager.getInstance().getCosts();

                    if(bank >= costs) {
                        SimpleMessage sm = new SimpleMessage(Lang.getPrefix() + Lang.get("RandomTP_Buy").replace("%AMOUNT%", (costs + "").endsWith(".0") ? (costs + "").substring(0, (costs + "").length() - 2) : (costs + "")), WarpSystem.getInstance());

                        sm.replace("%YES%", new ChatButton(Lang.get("RandomTP_Buy_Yes")) {
                            @Override
                            public void onClick(Player player) {
                                sm.destroy();

                                double bank = AdapterType.getActive().getMoney((Player) sender);

                                if(bank >= costs) {
                                    AdapterType.getActive().setMoney(player, bank - costs);
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
    }
}
