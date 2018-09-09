package de.codingair.warpsystem.spigot.features.warps.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Example;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
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
        
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        try {
            setHighestPriority(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Dominate_In_Commands.Highest_Priority.Warp", true));
        } catch(Exception e) {
            e.printStackTrace();
        }

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    for(Category c : manager.getCategories()) {
                        if(!c.hasPermission() || sender.hasPermission(c.getPermission())) suggestions.add(c.getNameWithoutColor());
                    }
                } else {
                    for(Warp warp : manager.getWarps()) {
                        Category c = warp.getCategory();

                        if(!warp.getNameWithoutColor().isEmpty()) {
                            if((c == null || !c.hasPermission() || sender.hasPermission(c.getPermission())) && (!warp.hasPermission() || sender.hasPermission(warp.getPermission())))
                                suggestions.add(warp.getNameWithoutColor().replace(" ", "_") + (warp.isInCategory() ? "@" + warp.getCategory().getNameWithoutColor() : ""));
                        }
                    }
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    Category category = manager.getCategory(argument);

                    if(category != null && category.hasPermission() && !sender.hasPermission(category.getPermission())) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category", new Example("ENG", "&cYou are not allowed to open this category!"), new Example("GER", "&cSie dürfen diese Kategorie nicht öffnen!")));
                        return false;
                    }

                    CWarps.run(sender, category);
                } else {
                    if(args.length == 0 || argument == null || argument.isEmpty()) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_HELP", new Example("ENG", "&7Use: &e" + label + " <Warp-Name>"), new Example("GER", "&7Benutze: &e/" + label + " <Warp-Name>")));
                        return false;
                    }

                    argument = argument.replace("_", " ");
                    Category category = null;
                    if(argument.contains("@")) {
                        String[] a = argument.split("@");

                        category = manager.getCategory(a[1]);
                        argument = a[0];
                    }

                    if(category != null && category.hasPermission() && !sender.hasPermission(category.getPermission())) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category", new Example("ENG", "&cYou are not allowed to open this category!"), new Example("GER", "&cSie dürfen diese Kategorie nicht öffnen!")));
                        return false;
                    }

                    Warp warp = manager.getWarp(argument, category);

                    if(warp == null) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS", new Example("ENG", "&cThis warp does not exist."), new Example("GER", "&cDieser Warp existiert nicht.")));
                        return false;
                    }

                    if(warp.hasPermission() && !sender.hasPermission(warp.getPermission())) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp", new Example("ENG", "&cYou are not allowed to use this warp!"), new Example("GER", "&cSie dürfen diesen Warp nicht benutzen!")));
                        return false;
                    }

                    warp.perform((Player) sender, false, Action.RUN_COMMAND);
                }
                return false;
            }
        });
    }
}
