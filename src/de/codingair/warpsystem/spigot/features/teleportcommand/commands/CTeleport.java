package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TabCompleterListener;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.packets.PrepareTeleportPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CTeleport extends WSCommandBuilder {
    public CTeleport() {
        super("Teleport", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tp <§eplayer§7> [§eplayer§7] §c" + Lang.get("Or") + " §7/tp [§eplayer§7] <§ex§7> <§ey§7> <§ez§7>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(!(sender instanceof Player)) return false;

                Player p = (Player) sender;

                try {
                    if(args.length == 1 && args[0].equals("/" + label)) {
                        //HELP
                        p.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tp <§eplayer§7> [§eplayer§7] §c" + Lang.get("Or") + " §7/tp [§eplayer§7] <§ex§7> <§ey§7> <§ez§7>");
                    } else if((args.length == 1 && !args[0].isEmpty()) || (args.length == 2 && !args[1].isEmpty())) {
                        //player [to player]
                        if(args.length == 1) {
                            //Teleport sender to 0
                            tp(p.getName(), p.getName(), args[0]);
                        } else {
                            //Teleport 0 to 1
                            tp(p.getName(), args[0], args[1]);
                        }
                    } else if((args.length == 3 && !args[2].isEmpty()) || (args.length == 4 && !args[3].isEmpty())) {
                        //player to coords

                        if(args.length == 3) {
                            //Teleport sender to coords
                            double x = 0;
                            double y = 0;
                            double z = 0;

                            args[0] = args[0].replace(",", ".");
                            args[1] = args[1].replace(",", ".");
                            args[2] = args[2].replace(",", ".");

                            if(args[0].contains("~")) {
                                x = ((Player) sender).getLocation().getX();
                                args[0] = args[0].replace("~", "");
                            }

                            if(args[1].contains("~")) {
                                y = ((Player) sender).getLocation().getY();
                                args[1] = args[1].replace("~", "");
                            }

                            if(args[2].contains("~")) {
                                z = ((Player) sender).getLocation().getZ();
                                args[2] = args[2].replace("~", "");
                            }

                            if(!args[0].isEmpty()) x += args[0].contains(".") ? Double.parseDouble(args[0]) : Integer.parseInt(args[0]);
                            if(!args[1].isEmpty()) y += args[1].contains(".") ? Double.parseDouble(args[1]) : Integer.parseInt(args[1]);
                            if(!args[2].isEmpty()) z += args[2].contains(".") ? Double.parseDouble(args[2]) : Integer.parseInt(args[2]);

                            tp(p.getName(), p.getName(), x, y, z);
                        } else {
                            //Teleport 0 to coords
                            double x = 0;
                            double y = 0;
                            double z = 0;

                            args[1] = args[1].replace(",", ".");
                            args[2] = args[2].replace(",", ".");
                            args[3] = args[3].replace(",", ".");

                            if(args[1].contains("~")) {
                                x = ((Player) sender).getLocation().getX();
                                args[1] = args[1].replace(",", ".").replace("~", "");
                            }

                            if(args[2].contains("~")) {
                                y = ((Player) sender).getLocation().getY();
                                args[2] = args[2].replace(",", ".").replace("~", "");
                            }

                            if(args[3].contains("~")) {
                                z = ((Player) sender).getLocation().getZ();
                                args[3] = args[3].replace(",", ".").replace("~", "");
                            }

                            if(!args[1].isEmpty()) x += args[1].contains(".") ? Double.parseDouble(args[1]) : Integer.parseInt(args[1]);
                            if(!args[2].isEmpty()) y += args[2].contains(".") ? Double.parseDouble(args[2]) : Integer.parseInt(args[2]);
                            if(!args[3].isEmpty()) z += args[3].contains(".") ? Double.parseDouble(args[3]) : Integer.parseInt(args[3]);

                            tp(p.getName(), args[0], x, y, z);
                        }
                    } else {
                        //HELP
                        p.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tp <§eplayer§7> [§eplayer§7] §c" + Lang.get("Or") + " §7/tp [§eplayer§7] <§ex§7> <§ey§7> <§ez§7>");
                    }
                } catch(NumberFormatException ex) {
                    //HELP
                    p.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /tp <§eplayer§7> [§eplayer§7] §c" + Lang.get("Or") + " §7/tp [§eplayer§7] <§ex§7> <§ey§7> <§ez§7>");
                }

                return false;
            }
        }.setOnlyPlayers(true));

        setHighestPriority(true);

        setOwnTabCompleter((commandSender, command, s, args) -> {
            if(!WarpSystem.hasPermission(commandSender, WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP)) {
                return new ArrayList<>();
            }

            if(commandSender instanceof Player) {
                Player p = (Player) commandSender;
                Block b = p.getTargetBlock((Set<Material>) null, 10);
                if(b.getType() == XMaterial.COMMAND_BLOCK.parseMaterial()) {
                    return new ArrayList<>();
                }
            }

            List<String> suggestions = new ArrayList<>();

            if(WarpSystem.getInstance().isOnBungeeCord()) {
                suggestions.add(TabCompleterListener.ID);

                StringBuilder builder = new StringBuilder(s);
                for(String arg : args) {
                    builder.append(" ").append(arg);
                }
                suggestions.add(builder.toString());
            } else {
                int deep = args.length - 1;

                if(args[deep].isEmpty()) {
                    if(deep == 1 && Character.isDigit(args[0].charAt(0)) && Bukkit.getPlayerExact(args[0]) == null) return suggestions;
                    if(deep == 0 || deep == 1) {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            suggestions.add(player.getName());
                        }
                    }
                } else {
                    if(deep == 0 || deep == 1) {
                        String last = args[deep];

                        for(Player player : Bukkit.getOnlinePlayers()) {
                            if(!player.getName().toLowerCase().startsWith(last.toLowerCase())) continue;
                            suggestions.add(player.getName());
                        }
                    }
                }
            }

            return suggestions;
        });
    }

    private static Number cut(double n) {
        double d = ((double) (int) (n * 100)) / 100;
        if(d == (int) d) return (int) d;
        else return d;
    }

    private static void tp(String gate, String player, double x, double y, double z) {
        Player gateP = Bukkit.getPlayerExact(gate);
        Player playerP = Bukkit.getPlayerExact(player);

        String destination = "x=" + cut(x) + ", y=" + cut(y) + ", z=" + cut(z);

        if(playerP == null) {
            //try on proxy
            if(WarpSystem.getInstance().isOnBungeeCord() && TeleportCommandManager.getInstance().isBungeeCord()) {
                WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportPacket(new Callback<Long>() {
                    @Override
                    public void accept(Long result) {
                        int handled = (int) (result >> 32);
                        int sent = result.intValue();

                        if(handled == 0) gateP.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                        else if(sent == 0) gateP.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", player));
                    }
                }, gate, player, x, y, z));
            } else gateP.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        if(gateP != playerP && TeleportCommandManager.getInstance().deniesForceTps(playerP)) {
            gateP.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", playerP.getName()));
            return;
        }

        if(gateP != playerP) gateP.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", playerP.getName()).replace("%warp%", destination));

        Location location = playerP.getLocation();
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(0);
        location.setPitch(0);

        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(location)), destination);
        options.setOrigin(Origin.TeleportCommand);
        options.setSkip(true);
        options.setMessage(gateP == playerP ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gateP.getName()));

        WarpSystem.getInstance().getTeleportManager().teleport(playerP, options);
    }

    private static void tp(String gate, String player, String target) {
        Player gateP = Bukkit.getPlayerExact(gate);
        Player playerP = Bukkit.getPlayerExact(player);
        Player targetP = Bukkit.getPlayerExact(target);

        if(playerP == null || targetP == null) {
            //try on proxy
            if(WarpSystem.getInstance().isOnBungeeCord() && TeleportCommandManager.getInstance().isBungeeCord()) {
                WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportPacket(new Callback<Long>() {
                    @Override
                    public void accept(Long result) {
                        int handled = (int) (result >> 32);
                        int sent = result.intValue();

                        if(handled == 0) gateP.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                        else if(sent == 0) gateP.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", player));
                    }
                }, gate, player, target));
            } else gateP.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        if(gateP != playerP && TeleportCommandManager.getInstance().deniesForceTps(playerP)) {
            gateP.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", playerP.getName()));
            return;
        }

        if(gateP != playerP) gateP.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", playerP.getName()).replace("%warp%", targetP.getName()));

        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(targetP.getLocation())), targetP.getName());
        options.setOrigin(Origin.TeleportCommand);
        options.setSkip(true);
        options.setMessage(gateP == playerP ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gateP.getName()));

        WarpSystem.getInstance().getTeleportManager().teleport(playerP, options);
    }
}
