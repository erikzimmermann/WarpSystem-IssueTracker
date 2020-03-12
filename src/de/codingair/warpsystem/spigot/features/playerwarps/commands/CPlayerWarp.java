package de.codingair.warpsystem.spigot.features.playerwarps.commands;

import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemBaseComponent;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemCommandBuilder;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CPlayerWarp extends WarpSystemCommandBuilder {
    public CPlayerWarp() {
        super("playerwarp", new WarpSystemBaseComponent(WarpSystem.PERMISSION_USE_PLAYER_WARPS) {
            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                new PWList((Player) sender).open();
                return false;
            }
        }.setOnlyPlayers(true), "pwarp", "pw");

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<PlayerWarp> l = new ArrayList<>(PlayerWarpManager.getManager().getOwnWarps((Player) sender));

                for(PlayerWarp warp : l) {
                    suggestions.add(warp.getName(false).replace(" ", "_"));
                }

                l.clear();
                l = new ArrayList<>(PlayerWarpManager.getManager().getForeignAvailableWarps((Player) sender));
                for(PlayerWarp warp : l) {
                    String name = warp.getName(false).replace(" ", "_");
                    suggestions.add((suggestions.contains(name) ? warp.getOwner().getName() + "." : "") + name);
                }
                l.clear();
            }

            @Override
            public boolean matchTabComplete(CommandSender sender, String suggestion, String argument) {
                String[] a = argument.split("\\.", -1);
                argument = a[a.length - 1].toLowerCase();

                a = suggestion.split("\\.", -1);
                suggestion = a[a.length - 1].toLowerCase();

                return suggestion.startsWith(argument);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                PlayerWarp warp = PlayerWarpManager.getManager().getWarp((Player) sender, argument);

                if(warp == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    return false;
                }

                if(warp.canTeleport((Player) sender)) {
                    warp.perform((Player) sender);
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_is_private"));
                }

                return false;
            }
        });
    }
}
