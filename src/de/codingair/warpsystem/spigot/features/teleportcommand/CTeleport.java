package de.codingair.warpsystem.spigot.features.teleportcommand;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CTeleport extends CommandBuilder {
    public CTeleport() {
        super("teleport", new BaseComponent(WarpSystem.PERMISSION_TELEPORT_COMMAND) {
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
                            Player target = Bukkit.getPlayer(args[0]);
                            tp(p, p, target);
                        } else {
                            //Teleport 0 to 1
                            Player player = Bukkit.getPlayer(args[0]);
                            Player target = Bukkit.getPlayer(args[1]);
                            tp(p, player, target);
                        }
                    } else if((args.length == 3 && !args[2].isEmpty()) || (args.length == 4 && !args[3].isEmpty())) {
                        //player to coords

                        if(args.length == 3) {
                            //Teleport sender to coords
                            double x = args[0].replace(",", ".").contains(".") ? Double.parseDouble(args[0].replace(",", ".")) : Integer.parseInt(args[0]);
                            double y = args[1].replace(",", ".").contains(".") ? Double.parseDouble(args[1].replace(",", ".")) : Integer.parseInt(args[1]);
                            double z = args[2].replace(",", ".").contains(".") ? Double.parseDouble(args[2].replace(",", ".")) : Integer.parseInt(args[2]);
                            tp(p, p, x, y, z);
                        } else {
                            //Teleport 0 to coords
                            Player player = Bukkit.getPlayer(args[0]);

                            double x = args[1].replace(",", ".").contains(".") ? Double.parseDouble(args[1].replace(",", ".")) : Integer.parseInt(args[1]);
                            double y = args[2].replace(",", ".").contains(".") ? Double.parseDouble(args[2].replace(",", ".")) : Integer.parseInt(args[2]);
                            double z = args[3].replace(",", ".").contains(".") ? Double.parseDouble(args[3].replace(",", ".")) : Integer.parseInt(args[3]);
                            tp(p, player, x, y, z);
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
        }.setOnlyPlayers(true), true);

        setHighestPriority(true);

        setOwnTabCompleter((commandSender, command, s, args) -> {
            List<String> suggestions = new ArrayList<>();
            int deep = args.length - 1;

            if(args[deep].isEmpty()) {
                if(deep == 1 && Character.isDigit(args[0].charAt(0)) && Bukkit.getPlayer(args[0]) == null) return suggestions;
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

            return suggestions;
        });
    }

    private static void tp(Player gate, Player player, double x, double y, double z) {
        if(player == null) {
            gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        String destination = "x=" + x + ", y=" + y + ", z=" + z;

        if(gate != player) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", player.getName()).replace("%warp%", destination));

        Location location = player.getLocation();
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(0);
        location.setPitch(0);

        de.codingair.warpsystem.spigot.base.WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.TeleportCommand, new Destination(new LocationAdapter(location)),
                destination, 0, true,
                gate == player ? Lang.get("Teleported_To") :
                        Lang.get("Teleported_To_By").replace("%gate%", gate.getName())
                , false, null);
    }

    private static void tp(Player gate, Player player, Player target) {
        if(player == null || target == null) {
            gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        if(gate != player) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", player.getName()).replace("%warp%", target.getName()));

        WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.TeleportCommand, new Destination(new LocationAdapter(target.getLocation())),
                target.getName(), 0, true,
                gate == player ? Lang.get("Teleported_To") :
                        Lang.get("Teleported_To_By").replace("%gate%", gate.getName())
                , false, null);
    }
}
