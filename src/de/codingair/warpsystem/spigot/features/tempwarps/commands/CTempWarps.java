package de.codingair.warpsystem.spigot.features.tempwarps.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.api.players.PermissionPlayer;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.tempwarps.guis.GTempWarpList;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CTempWarps extends CommandBuilder {
    public CTempWarps() {
        super("TempWarps", new BaseComponent(WarpSystem.PERMISSION_USE_TEMP_WARPS) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, delete, edit, list, info, renew>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, delete, edit, list, info, renew>");
                return false;
            }
        }, true);

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                List<TempWarp> list = TempWarpManager.getManager().getWarps((Player) sender);
                int current = list.size();
                list.clear();
                if(!TempWarpManager.hasPermission((Player) sender)) {
                    if(current == 0) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    } else sender.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Maximum_of_Warps").replace("%AMOUNT%", current + ""));
                    return false;
                }

                TempWarpManager.getManager().create((Player) sender);
                return false;
            }
        });

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                List<TempWarp> list = TempWarpManager.getManager().getWarps((Player) sender);
                int current = list.size();
                list.clear();
                if(!TempWarpManager.hasPermission((Player) sender)) {
                    if(current == 0) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    } else sender.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Maximum_of_Warps").replace("%AMOUNT%", current + ""));
                    return false;
                }

                if(TempWarpManager.getManager().isProtectedRegions() && isProtected((Player) sender)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_Create_Protected"));
                    return false;
                }

                TempWarpManager.getManager().create((Player) sender, argument);
                return false;
            }
        });


        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " delete §e<warp>");
                return false;
            }
        });

        getComponent("delete").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<TempWarp> warps = TempWarpManager.getManager().getWarps((Player) sender);

                for(TempWarp warp : warps) {
                    suggestions.add(warp.getIdentifier());
                }

                warps.clear();
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                TempWarpManager.getManager().delete((Player) sender, argument);
                return false;
            }
        });


        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " edit §e<warp>");
                return false;
            }
        });

        getComponent("edit").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<TempWarp> warps = TempWarpManager.getManager().getWarps((Player) sender);

                for(TempWarp warp : warps) {
                    suggestions.add(warp.getIdentifier());
                }

                warps.clear();
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                TempWarpManager.getManager().edit((Player) sender, argument);
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("list") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                new GTempWarpList((Player) sender).open();
//                List<TempWarp> list = TempWarpManager.getManager().getWarps((Player) sender);
//                drawList((Player) sender, sender.getName(), list);
//                list.clear();
                return false;
            }
        });

        getComponent("list").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    suggestions.add(player.getName());
                }

                suggestions.remove(sender.getName());
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Player player = Bukkit.getPlayer(argument);

                if(player == null) {
                    if(sender.hasPermission(WarpSystem.PERMISSION_ADMIN)) {
                        if(WarpSystem.getInstance().getUUIDManager().isCached(argument)) {
                            List<TempWarp> warps = TempWarpManager.getManager().getWarps(WarpSystem.getInstance().getUUIDManager().getCached(argument));
                            drawList((Player) sender, argument, warps);
                            warps.clear();
                        } else {
                            SimpleMessage message = new SimpleMessage(Lang.getPrefix() + Lang.get("Ask_to_download_uuid"), WarpSystem.getInstance());
                            message.replace("%YES%", new ChatButton("§a" + Lang.get("Yes")) {
                                @Override
                                public void onClick(Player clicked) {
                                    WarpSystem.getInstance().getUUIDManager().downloadFromMojang(argument, new Callback<UUID>() {
                                        @Override
                                        public void accept(UUID id) {
                                            if(id == null) {
                                                sender.sendMessage(Lang.getPrefix() + Lang.get("Player_does_not_exist"));
                                            } else {
                                                List<TempWarp> warps = TempWarpManager.getManager().getWarps(id);
                                                drawList((Player) sender, argument, warps);
                                                warps.clear();
                                            }
                                        }
                                    });
                                }
                            }.setHover(Lang.get("Click_Hover")));

                            message.setTimeOut(60);
                            message.send((Player) sender);
                        }
                    } else {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                    }
                } else {
                    if(player.getName().equals(sender.getName())) {
                        getComponent("list").runCommand(sender, label, null);
                    } else {
                        List<TempWarp> warps = TempWarpManager.getManager().getWarps(player);
                        drawList((Player) sender, player.getName(), warps);
                        warps.clear();
                    }
                }

                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("info") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " info §e<identifier>");
                return false;
            }
        });

        getComponent("info").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<TempWarp> list = TempWarpManager.getManager().getWarps();
                for(TempWarp warp : list) {
                    suggestions.add(warp.getIdentifier());
                }
                list.clear();
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                TempWarp warp = TempWarpManager.getManager().getWarp(argument);

                if(warp == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    return false;
                }

                drawInfo((Player) sender, warp);
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("renew") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " renew §e<warp>");
                return false;
            }
        });

        getComponent("renew").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<TempWarp> warps = TempWarpManager.getManager().getWarps((Player) sender);

                for(TempWarp warp : warps) {
                    if(warp.isExpired()) suggestions.add(warp.getIdentifier());
                }

                warps.clear();
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                TempWarpManager.getManager().reactivate((Player) sender, argument);
                return false;
            }
        });
    }

    private boolean isProtected(Player player) {
        PermissionPlayer check = new PermissionPlayer(player);
        BlockBreakEvent event = new BlockBreakEvent(player.getLocation().getBlock(), check);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    private static void drawList(Player player, String owner, List<TempWarp> list) {
        if(list.isEmpty()) {
            if(player.getName().equals(owner)) {
                player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_No_Warps"));
            } else {
                player.sendMessage(Lang.getPrefix() + Lang.get("TempWarp_No_Warps_Other"));
            }
            return;
        }


        List<String> l = new ArrayList<>();

        l.add(" ");
        l.add("§7§l§m---------------------------------------------");
        l.add(" ");

        if(player.getName().equals(owner)) {
            l.add("  " + Lang.get("TempWarp_You_have_n_Warps").replace("%AMOUNT%", list.size() + ""));
        } else {
            l.add("  " + Lang.get("TempWarp_Player_have_n_Warps").replace("%AMOUNT%", list.size() + "").replace("%PLAYER%", owner));
        }

        l.add(" ");
        for(TempWarp warp : list) {
            l.add("  §7\"§f" + warp.getName() + "§7\" §8(" + (warp.isPublic() ? "§a" + Lang.get("Public") : "§c" + Lang.get("Private")) + "§8)§7: §b" + (warp.getLeftTime() <= 0 ? "§c" + Lang.get("Expired") + " » " + Lang.get("TempWarp_List_deleted_in").replace("%TIME_LEFT%", "" + TempWarpManager.getManager().convertInTimeFormat(TempWarpManager.getManager().getInactiveTime() * 1000 + warp.getLeftTime(), TimeUnit.MILLISECONDS)) : TempWarpManager.getManager().convertInTimeFormat(warp.getLeftTime(), TimeUnit.MILLISECONDS) + " §7" + Lang.get("Remaining")));
        }

        l.add(" ");
        l.add("§7§l§m---------------------------------------------");

        player.sendMessage(l.toArray(new String[0]));

        l.clear();
    }

    private static void drawInfo(Player player, TempWarp tempWarp) {
        List<String> l = new ArrayList<>();

        l.add(" ");
        l.add("§7§l§m---------------------------------------------");
        l.add(" ");

        //Name, Public/Private, Owner (UUID and Name), BornDate, EndDate, Location, TeleportCosts

        l.add("  §6TempWarp§7: \"§r" + tempWarp.getName() + "§7\"");
        l.add("  §6World§7: §b" + (tempWarp.isAvailable() ? tempWarp.getLocation().getWorld().getName() : tempWarp.getLocation().getWorldName() + " §8(§cMissing§8)"));
        l.add("  §6State§7: " + (tempWarp.isPublic() ? "§aPublic" : "§cPrivate"));
        l.add("  §6Teleport-Costs§7: §c" + tempWarp.getTeleportCosts() + " Coin(s)");
        l.add(" ");
        l.add("  §6Owner§7: \"§b" + tempWarp.getLastKnownName() + "§7\" §8(§7" + tempWarp.getOwner().toString() + "§8)");
        l.add(" ");
        l.add("  §6Created§7: §a" + TempWarpManager.getManager().convertInTimeFormat(new Date().getTime() - tempWarp.getBornDate().getTime(), TimeUnit.MILLISECONDS) + " ago");
        l.add("  §6End in§7: §c" + TempWarpManager.getManager().convertInTimeFormat(tempWarp.getLeftTime(), TimeUnit.MILLISECONDS));

        l.add(" ");
        l.add("§7§l§m---------------------------------------------");

        if(!tempWarp.isAvailable()) {
            l.add(" ");
            l.add(TempWarpManager.ERROR_NOT_AVAILABLE(tempWarp.getIdentifier()));
            l.add(" ");

        }

        player.sendMessage(l.toArray(new String[0]));

        l.clear();
    }
}
