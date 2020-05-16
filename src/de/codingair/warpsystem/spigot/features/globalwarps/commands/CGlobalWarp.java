package de.codingair.warpsystem.spigot.features.globalwarps.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CGlobalWarp extends WSCommandBuilder implements BungeeFeature {
    public CGlobalWarp() {
        super("GlobalWarp", new BaseComponent(WarpSystem.PERMISSION_USE_GLOBAL_WARPS) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(WarpSystem.getInstance().isOnBungeeCord()) sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
                else sender.sendMessage(Lang.getPrefix() + Lang.get("Connect_BungeeCord"));
                return false;
            }
        }.setOnlyPlayers(true));

        WarpSystem.getInstance().getBungeeFeatureList().add(this);
    }

    @Override
    public void onConnect() {
        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                suggestions.addAll(GlobalWarpManager.getInstance().getGlobalWarps().keySet());
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(GlobalWarpManager.getInstance().exists(argument)) {
                    TeleportOptions options = new TeleportOptions(new Destination(argument, DestinationType.GlobalWarp), GlobalWarpManager.getInstance().getCaseCorrectlyName(argument));
                    options.setOrigin(Origin.GlobalWarp);
                    WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, options);
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                }
                return false;
            }
        });

        getBaseComponent().getChild(null).addChild(new MultiCommandComponent(WarpSystem.PERMISSION_GLOBAL_WARPS_DIRECT_TELEPORT) {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getName().equals(sender.getName())) continue;
                    suggestions.add(player.getName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Player player = Bukkit.getPlayer(argument);

                if(player == null || !player.isOnline()) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                    return false;
                }

                if(GlobalWarpManager.getInstance().exists(args[0])) {
                    String dest = GlobalWarpManager.getInstance().getCaseCorrectlyName(args[0]);
                    TeleportOptions options = new TeleportOptions(new Destination(args[0], DestinationType.GlobalWarp), dest);
                    options.setOrigin(Origin.GlobalWarp);
                    options.setMessage(Lang.get("Teleported_To_By").replace("%gate%", sender.getName()));
                    options.setPermission(TeleportManager.NO_PERMISSION);

                    if(!player.getName().equals(sender.getName())) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", player.getName()).replace("%warp%", dest));
                        options.setSkip(true);
                    }

                    WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                }
                return false;
            }
        }.setOnlyPlayers(false));
    }

    @Override
    public void onDisconnect() {
        getBaseComponent().removeChild((String) null);
    }
}
