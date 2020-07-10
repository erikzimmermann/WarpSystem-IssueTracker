package de.codingair.warpsystem.spigot.features.simplewarps.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import de.codingair.warpsystem.spigot.features.warps.commands.CWarps;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarp extends WSCommandBuilder {
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
                if(FeatureType.WARP_GUI.isActive() && WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_USE_WARP_GUI)) {
                        CWarps.run(sender, null);
                    } else noPermission(sender, label, this);
                } else {
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) {
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<warp>");
                    } else noPermission(sender, label, this);
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(FeatureType.WARP_GUI.isActive() && WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_USE_WARP_GUI)) {
                        CWarps.run(sender, null);
                    } else noPermission(sender, label, this);
                } else {
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) {
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<warp>");
                    } else noPermission(sender, label, this);
                }

                return false;
            }
        }.setOnlyPlayers(true));

        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARP_GUI);
        CWarpHook hook = new CWarpHook();

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                if(FeatureType.WARP_GUI.isActive() && WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_USE_WARP_GUI)) {
                        for(Icon c : manager.getPages()) {
                            if(!c.hasPermission() || sender.hasPermission(c.getPermission())) suggestions.add(c.getNameWithoutColor());
                        }
                    }
                } else {
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_USE_SIMPLE_WARPS) || sender.hasPermission(WarpSystem.PERMISSION_SIMPLE_WARPS_DIRECT_TELEPORT)) {
                        hook.addArguments(sender, suggestions);
                    }
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(FeatureType.WARP_GUI.isActive() && WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false)) {
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_USE_WARP_GUI)) {
                        Icon category = manager.getPage(argument);

                        if(category != null && category.hasPermission() && !sender.hasPermission(category.getPermission())) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Page"));
                            return false;
                        }

                        CWarps.run(sender, category);
                    } else getBaseComponent().noPermission(sender, label, this);
                } else {
                    if(WarpSystem.hasPermission(sender, WarpSystem.PERMISSION_USE_SIMPLE_WARPS)) {
                        if(args.length == 0 || argument == null || argument.isEmpty()) {
                            sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<warp>");
                            return false;
                        }

                        if(hook.runCommand(sender, label, argument, args)) return false;
                        sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    } else if(sender.hasPermission(WarpSystem.PERMISSION_SIMPLE_WARPS_DIRECT_TELEPORT)) {
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " <warp> " + WarpSystem.opt().cmdArg() + "<player>");
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

                TeleportOptions options = new TeleportOptions(new Destination(warp.getName(), DestinationType.SimpleWarp), warp.getName());
                options.setOrigin(Origin.SimpleWarp);
                options.setMessage(Lang.getPrefix() + Lang.get("Teleported_To_By").replace("%gate%", sender.getName()));
                options.setPermission(TeleportManager.NO_PERMISSION);

                if(!player.getName().equals(sender.getName())) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", player.getName()).replace("%warp%", warp.getName()));
                    options.setSkip(true);
                }

                WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                return false;
            }
        }.setOnlyPlayers(false));
    }
}
