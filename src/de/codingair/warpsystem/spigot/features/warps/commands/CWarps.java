package de.codingair.warpsystem.spigot.features.warps.commands;

import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarps extends CommandBuilder {
    public CWarps() {
        super("Warps", new BaseComponent(WarpSystem.PERMISSION_USE_WARPS) {
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
                run(sender, null);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                run(sender, null);
                return false;
            }
        }.setOnlyPlayers(true), true);

        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        try {
            setHighestPriority(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Dominate_In_Commands.Highest_Priority.Warps", true));
        } catch(Exception e) {
            e.printStackTrace();
        }

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Icon c : manager.getCategories()) {
                    if(c.getName() == null) continue;
                    if(!c.hasPermission() || sender.hasPermission(c.getPermission())) suggestions.add(c.getNameWithoutColor());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Icon category = manager.getCategory(argument);

                if(category != null && category.hasPermission() && !sender.hasPermission(category.getPermission())) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category"));
                    return false;
                }

                run(sender, category);
                return false;
            }
        });
    }

    public static void run(CommandSender sender, Icon category) {
        Player p = (Player) sender;

        if(!WarpSystem.activated) return;

        if(WarpSystem.maintenance && !p.hasPermission(WarpSystem.PERMISSION_ByPass_Maintenance)) {
            p.sendMessage(Lang.getPrefix() + Lang.get("Warning_Maintenance"));
            return;
        }

        new GWarps(p, category, false).open();
        Sound.LEVEL_UP.playSound(p);
    }
}
