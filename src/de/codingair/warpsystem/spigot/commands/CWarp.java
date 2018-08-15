package de.codingair.warpsystem.spigot.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarp extends CommandBuilder {
    public CWarp() {
        super("Warp", new BaseComponent(WarpSystem.PERMISSION_USE) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_System", new Example("ENG", "&cYou are not allowed to use warps!"), new Example("GER", "&cSie dürfen keine Warps benutzen!")));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players", new Example("ENG", "This action is only for players!"), new Example("GER", "Diese Aktion ist nur für Spieler!")));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    CWarps.run(sender, null);
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_HELP", new Example("ENG", "&7Use: &e" + label + " <Warp-Name>"), new Example("GER", "&7Benutze: &e/" + label + " <Warp-Name>")));
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    CWarps.run(sender, null);
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_HELP", new Example("ENG", "&7Use: &e" + label + " <Warp-Name>"), new Example("GER", "&7Benutze: &e/" + label + " <Warp-Name>")));
                }

                return false;
            }
        }.setOnlyPlayers(true), true);

        try {
            setHighestPriority(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Dominate_In_Commands.Highest_Priority.Warp", true));
        } catch(Exception e) {
            e.printStackTrace();
        }

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    for(Category c : WarpSystem.getInstance().getIconManager().getCategories()) {
                        suggestions.add(c.getNameWithoutColor());
                    }
                } else {
                    for(Warp warp : WarpSystem.getInstance().getIconManager().getWarps()) {
                        if(!warp.getNameWithoutColor().isEmpty()) {
                            suggestions.add(warp.getNameWithoutColor().replace(" ", "_") + (warp.isInCategory() ? "@" + warp.getCategory().getName() : ""));
                        }
                    }
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    CWarps.run(sender, WarpSystem.getInstance().getIconManager().getCategory(argument));
                } else {
                    if(args.length == 0 || argument == null || argument.isEmpty()) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_HELP", new Example("ENG", "&7Use: &e" + label + " <Warp-Name>"), new Example("GER", "&7Benutze: &e/" + label + " <Warp-Name>")));
                        return false;
                    }

                    argument = argument.replace("_", " ");
                    Category category = null;
                    if(argument.contains("@")) {
                        String[] a = argument.split("@");

                        category = WarpSystem.getInstance().getIconManager().getCategory(a[1]);
                        argument = a[0];
                    }

                    Warp warp = WarpSystem.getInstance().getIconManager().getWarp(argument, category);

                    if(warp == null) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS", new Example("ENG", "&cThis warp does not exist."), new Example("GER", "&cDieser Warp existiert nicht.")));
                        return false;
                    }

                    WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, warp);
                }
                return false;
            }
        });
    }
}
