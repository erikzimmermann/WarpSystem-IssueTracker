package de.codingair.warpsystem.spigot.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CGlobalWarp extends CommandBuilder {
    public CGlobalWarp() {
        super("GlobalWarp", new BaseComponent() {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission", new Example("GER", "&cDu hast keine Berechtigungen für diese Aktion!"), new Example("ENG", "&cYou don't have permissions for that action!"), new Example("FRE", "&cDésolé mais vous ne possédez la permission pour exécuter cette action!")));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Help", new Example("ENG", "&7Use: &e/" + label + " <create, delete>"), new Example("GER", "&7Benutze: &e/" + label + " <create, delete>")));
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Help", new Example("ENG", "&7Use: &e/" + label + " <create, delete>"), new Example("GER", "&7Benutze: &e/" + label + " <create, delete>")));
                return false;
            }
        }, true);

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Create_Help", new Example("ENG", "&7Use: &e/" + label + " create <name>"), new Example("GER", "&7Benutze: &e/" + label + " create <Name>")));
                return false;
            }
        });

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Player player = (Player) sender;
                WarpSystem.getInstance().getGlobalWarpManager().create(argument, player.getLocation(), new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean created) {
                        if(created) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Created", new Example("ENG", "&7The GlobalWarp '&b" + argument + "&7' was &acreated successfully&7."), new Example("GER", "&7Der GlobalWarp '&b" + argument + "&7' wurde &aerfolgreich erstellt&7.")));
                        } else {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Create_Name_Already_Exists", new Example("ENG", "&7The GlobalWarp '&b" + argument + "&7' &calready exists&7."), new Example("GER", "&7Der GlobalWarp '&b" + argument + "&7' &cexistiert bereits&7.")));
                        }
                    }
                });
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Delete_Help", new Example("ENG", "&7Use: &e/" + label + " delete <name>"), new Example("GER", "&7Benutze: &e/" + label + " delete <Name>")));
                return false;
            }
        });

        getComponent("delete").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                suggestions.addAll(WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps());
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                WarpSystem.getInstance().getGlobalWarpManager().delete(argument, new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean deleted) {
                        if(deleted) {
                            String name = WarpSystem.getInstance().getGlobalWarpManager().getCaseCorrectlyName(argument);

                            sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Deleted", new Example("ENG", "&7The GlobalWarp '&b" + name + "&7' was &cdeleted&7."), new Example("GER", "&7Der GlobalWarp '&b" + argument + "&7' wurde &cgelöscht&7.")));
                        } else {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Create_Name_Already_Exists", new Example("ENG", "&7The GlobalWarp '&b" + argument + "&7' &cdoes not exist&7."), new Example("GER", "&7Der GlobalWarp '&b" + argument + "&7' &cexistiert nicht&7.")));
                        }
                    }
                });
                return false;
            }
        });
    }
}
