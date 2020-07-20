package de.codingair.warpsystem.spigot.features.playerwarps.commands;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemBaseComponent;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemCommandBuilder;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PWMultiCommandComponent;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CPlayerWarp extends WarpSystemCommandBuilder {
    public CPlayerWarp(List<String> aliases) {
        super("playerwarp", new WarpSystemBaseComponent(WarpSystem.PERMISSION_USE_PLAYER_WARPS) {
            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                List<PlayerWarp> warps = PlayerWarpManager.getManager().getOwnWarps((Player) sender);

                if(warps.size() == 1) {
                    PlayerWarp warp = warps.get(0);

                    if(PlayerWarpManager.getManager().isEconomy() && warp.isExpired()) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_is_expired"));
                        return false;
                    }

                    if(warp.canTeleport((Player) sender)) {
                        warp.perform((Player) sender);
                    } else {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_is_private"));
                    }
                } else new PWList((Player) sender).open();
                return false;
            }
        }.setOnlyPlayers(true), aliases.toArray(new String[0]));

        getBaseComponent().addChild(new PWMultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<PlayerWarp> l = new ArrayList<>(PlayerWarpManager.getManager().getOwnWarps((Player) sender));

                for(PlayerWarp warp : l) {
                    if(warp.isExpired()) continue;
                    String name = warp.getName(false).replace(" ", "_");
                    suggestions.add(name);
                }

                l.clear();
                l = new ArrayList<>(PlayerWarpManager.getManager().getForeignAvailableWarps((Player) sender));
                for(PlayerWarp warp : l) {
                    String name = warp.getName(false).replace(" ", "_");
                    suggestions.add(warp.getOwner().getName() + "." + name);
                }
                l.clear();
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                PlayerWarp warp = PlayerWarpManager.getManager().getWarp((Player) sender, argument);

                if(warp == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    return false;
                }

                if(PlayerWarpManager.getManager().isEconomy() && warp.isExpired()) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_is_expired"));
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
