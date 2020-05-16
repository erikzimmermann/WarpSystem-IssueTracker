package de.codingair.warpsystem.spigot.features.randomteleports.commands;

import de.codingair.codingapi.server.commands.builder.special.NaturalCommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.bungee.features.randomtp.RandomTPListener;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import de.codingair.warpsystem.spigot.features.randomteleports.packets.RandomTPPacket;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RTP_Go_Command extends NaturalCommandComponent {
    public RTP_Go_Command(String permission) {
        super(permission);
    }

    private boolean checkOther(CommandSender sender) {
        return sender.hasPermission(WarpSystem.PERMISSION_RANDOM_TELEPORT_SELECTION_OTHER);
    }

    @Override
    public boolean runCommand(CommandSender sender, String label, String[] args) {
        if(args.length >= 1 && args[0].equalsIgnoreCase("go")) {
            StringBuilder builder = new StringBuilder();
            int endOfCMD = 0;
            for(int i = 1; i < args.length; i++) {
                endOfCMD = i;

                String s = args[i];
                builder.append(args[i]);

                if(!s.contains("]")) builder.append(" ");
                else break;
            }
            endOfCMD++;

            String player = args.length >= endOfCMD + 1 ? args[endOfCMD] : null;

            if(player != null && !checkOther(sender)) {
                getBase().noPermission(sender, label, this);
                return false;
            }

            if(player == null && !(sender instanceof Player)) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " go §e[server-1, server-2, ...; world-1, world-2, ...] <player>");
                return false;
            }

            if(player == null) player = sender.getName();
            String finalPlayer = player;

            String cmd = builder.toString().trim();

            if(!cmd.startsWith("[") || !cmd.endsWith("]")) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " go §e[server-1, server-2, ...; world-1, world-2, ...]" + (checkOther(sender) ? " [player]" : ""));
                return false;
            } else cmd = cmd.substring(1, cmd.length() - 1).replace(" ", "").toLowerCase();

            String[] data = cmd.split(";");

            Player p = Bukkit.getPlayer(player);
            if(p == null) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                return false;
            }

            if(!RandomTeleporterManager.getInstance().canTeleport(p)) {
                if(player.equalsIgnoreCase(sender.getName())) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Teleports_Left"));
                else {
                    if(sender instanceof Player) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Other_No_Teleports_Left").replace("%PLAYER%", finalPlayer));
                    } else {
                        //console
                        p.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Teleports_Left"));
                    }

                }
                return false;
            }

            if(data.length == 1) {
                //only local world
                //test@test1, test@test2, test@test3
                String[] worlds = data[0].replaceAll("\\p{Blank}*[a-z]*@", "").split(",");

                World target = Bukkit.getWorld(worlds[(int) (Math.random() * worlds.length)]);

                if(target == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
                    return false;
                }

                RandomTeleporterManager.getInstance().tryToTeleport(player, target, false, new Callback<Integer>() {
                    @Override
                    public void accept(Integer result) {
                        if(!finalPlayer.equalsIgnoreCase(sender.getName())) {
                            if(result == 0) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported_Other").replace("%PLAYER%", finalPlayer));
                            else if(result == 1) sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                            else if(result == 2) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
                            else if(result == 4) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Other_No_Teleports_Left").replace("%PLAYER%", finalPlayer));
                        }

                        if(result == 3) sender.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
                    }
                });
            } else if(data.length == 2) {
                //on server
                if(!WarpSystem.getInstance().isOnBungeeCord()) {
                    sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " go §e[world-1, world-2, ...] [player]");
                    return false;
                }

                List<String> worlds = new ArrayList<>(Arrays.asList(data[1].split(",")));
                String targetWorld = worlds.get((int) (Math.random() * worlds.size()));
                data = targetWorld.split("@");
                String targetServer = data[0];
                targetWorld = data[1];

                if(!targetServer.equalsIgnoreCase(WarpSystem.getInstance().getCurrentServer())) {
                    WarpSystem.getInstance().getDataHandler().send(new RandomTPPacket(new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean success) {
                            if(success) {
                                if(!finalPlayer.equalsIgnoreCase(sender.getName())) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported_Other").replace("%PLAYER%", finalPlayer));
                            } else sender.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
                        }
                    }, player, targetServer, targetWorld));
                    return false;
                }

                World target = Bukkit.getWorld(targetWorld);

                if(target == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
                    return false;
                }

                RandomTeleporterManager.getInstance().tryToTeleport(player, target, false, new Callback<Integer>() {
                    @Override
                    public void accept(Integer result) {
                        if(!finalPlayer.equalsIgnoreCase(sender.getName())) {
                            if(result == 0) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported_Other").replace("%PLAYER%", finalPlayer));
                            else if(result == 1) sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                            else if(result == 2) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
                            else if(result == 4) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Other_No_Teleports_Left").replace("%PLAYER%", finalPlayer));
                        }

                        if(result == 3) sender.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
                    }
                });
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> sug = new ArrayList<>();
        if(args.length == 1) {
            //add old
            sug.add("go");
        } else if(args.length >= 2 && args[0].equalsIgnoreCase("go")) {
            StringBuilder builder = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                builder.append(args[i]);
                builder.append(" ");
            }
            String cmd = "go " + builder.toString();
            cmd = cmd.substring(0, cmd.length() - 1);

            if(WarpSystem.getInstance().isOnBungeeCord()) {
                if(commandSender instanceof Player) {
                    sug.add(RandomTPListener.ID);
                    if(checkOther(commandSender)) sug.add(RandomTPListener.ID_OTHER);
                    sug.add(cmd);
                }
            } else {
                applySuggestions(commandSender, cmd, sug);
            }
        }
        return sug;
    }

    private void applySuggestions(CommandSender sender, String command, List<String> suggestions) {
        command = command.replace("go ", "");

        boolean editingLast = !command.endsWith(" ");
        String[] args = command.split(" ", -1);

        int start = command.indexOf('[');
        int end = command.lastIndexOf(']');

        if(start == -1 || end == -1) {
            //data
            command = command.replace(" ", ""); //remove first bracket
            String startSug = start == -1 || args.length <= 1 ? "[" : "";

            args = command.split(",", -1);
            String last = args[args.length - 1];

            int max = Bukkit.getWorlds().size();
            int count = count(command, ',') - (editingLast ? 1 : 0);

            for(World world : Bukkit.getWorlds()) {
                String w = world.getName();
                if(!command.contains(w + ",") && !command.contains(w + "]")) {
                    if(max > 1 && count + 1 < max) {
                        add(suggestions, last, startSug + w + ",");
                    }

                    add(suggestions, last, startSug + w + "]");
                }
            }
        } else if(checkOther(sender) && command.length() > end + 1) {
            //player
            String rest = command.substring(end + 2);

            if(rest.contains(" ")) return;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(rest.isEmpty() || player.getName().toLowerCase().startsWith(rest)) suggestions.add(player.getName());
            }
        }
    }

    private int count(String s, char c) {
        int i = 0;
        for(char c1 : s.toCharArray()) {
            if(c1 == c) i++;
        }
        return i;
    }

    private void add(List<String> suggestions, String last, String argument) {
        if(last.isEmpty() || argument.startsWith(last)) {
            suggestions.add(argument);
        }
    }
}
