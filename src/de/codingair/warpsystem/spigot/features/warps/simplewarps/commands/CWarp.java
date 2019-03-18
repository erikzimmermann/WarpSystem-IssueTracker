package de.codingair.warpsystem.spigot.features.warps.simplewarps.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.commands.CWarps;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarp extends CommandBuilder {
    public CWarp() {
        super("Warp", new BaseComponent() {
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
                if(FeatureType.WARPS.isActive() && WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    if(sender.hasPermission(WarpSystem.PERMISSION_USE_WARPS)) {
                        CWarps.run(sender, null);
                    } else noPermission(sender, label, this);
                } else {
                    if(sender.hasPermission(WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) {
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
                    } else noPermission(sender, label, this);
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(FeatureType.WARPS.isActive() && WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    if(sender.hasPermission(WarpSystem.PERMISSION_USE_WARPS)) {
                        CWarps.run(sender, null);
                    } else noPermission(sender, label, this);
                } else {
                    if(sender.hasPermission(WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) {
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
                    } else noPermission(sender, label, this);
                }

                return false;
            }
        }.setOnlyPlayers(true), true);
        
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);
        CWarpHook hook = new CWarpHook();

        try {
            setHighestPriority(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Dominate_In_Commands.Highest_Priority.Warp", true));
        } catch(Exception e) {
            e.printStackTrace();
        }

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                if(FeatureType.WARPS.isActive() && WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    if(sender.hasPermission(WarpSystem.PERMISSION_USE_WARPS)) {
                        for(Icon c : manager.getCategories()) {
                            if(!c.hasPermission() || sender.hasPermission(c.getPermission())) suggestions.add(c.getNameWithoutColor());
                        }
                    }
                } else {
                    if(sender.hasPermission(WarpSystem.PERMISSION_USE_SIMPLE_WARPS) || sender.hasPermission(WarpSystem.PERMISSION_SIMPLE_WARPS_DIRECT_TELEPORT)) {
                        hook.addArguments(sender, suggestions);
                    }
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(FeatureType.WARPS.isActive() && WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    if(sender.hasPermission(WarpSystem.PERMISSION_USE_WARPS)) {
                        Icon category = manager.getCategory(argument);

                        if(category != null && category.hasPermission() && !sender.hasPermission(category.getPermission())) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Category"));
                            return false;
                        }

                        CWarps.run(sender, category);
                    } else getBaseComponent().noPermission(sender, label, this);
                } else {
                    if(sender.hasPermission(WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) {
                        if(args.length == 0 || argument == null || argument.isEmpty()) {
                            sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
                            return false;
                        }

                        if(hook.runCommand(sender, label, argument, args)) return false;
                        sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    } else if(sender.hasPermission(WarpSystem.PERMISSION_SIMPLE_WARPS_DIRECT_TELEPORT)) {
                        sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " <warp> §e<player>");
                    } else getBaseComponent().noPermission(sender, label, this);
                }
                return false;
            }
        });

        getBaseComponent().getChild(null).addChild(new MultiCommandComponent(WarpSystem.PERMISSION_SIMPLE_WARPS_DIRECT_TELEPORT) {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                if(!WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(player.getName().equals(sender.getName())) continue;
                        suggestions.add(player.getName());
                    }
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                SimpleWarpManager m = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIMPLE_WARPS);
                if(!m.existsWarp(args[0])) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    return false;
                }

                Player player = Bukkit.getPlayer(argument);

                if(player == null || !player.isOnline()) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                    return false;
                }

                SimpleWarp warp = m.getWarp(args[0]);

                WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.DirectSimpleWarp,  new Destination(warp.getName(), DestinationType.SimpleWarp), warp.getName(), TeleportManager.NO_PERMISSION, 0, true,
                        Lang.get("Teleported_To_By").replace("%gate%", sender.getName()), false, null);
                return false;
            }
        });
    }
}
