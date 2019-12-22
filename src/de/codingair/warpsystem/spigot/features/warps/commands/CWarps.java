package de.codingair.warpsystem.spigot.features.warps.commands;

import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarps extends CommandBuilder {
    public CWarps() {
        super("warps", "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_USE_WARP_GUI) {
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
                for(Icon c : manager.getPages()) {
                    if(c.getName() == null) continue;
                    if(!c.hasPermission() || sender.hasPermission(c.getPermission())) suggestions.add(c.getNameWithoutColor());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Icon category = manager.getPage(argument);
                CommandSender target = sender;

                if(category != null && category.hasPermission() && !sender.hasPermission(category.getPermission())) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Page"));
                    return false;
                } else if(category == null && sender.hasPermission(WarpSystem.PERMISSION_WARP_GUI_OTHER)) {
                    Player other = Bukkit.getPlayer(argument);

                    if(other == null) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                        return false;
                    }

                    target = other;
                }

                run(target, category);
                return false;
            }
        });

        getComponent((String) null).addChild(new MultiCommandComponent(WarpSystem.PERMISSION_WARP_GUI_OTHER) {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Icon category = manager.getPage(argument);

                if(category != null && category.hasPermission() && !sender.hasPermission(category.getPermission())) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Page"));
                    return false;
                }

                Player other = Bukkit.getPlayer(argument);

                if(other == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                    return false;
                }

                run(other, category);
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
