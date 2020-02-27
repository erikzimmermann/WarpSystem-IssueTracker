package de.codingair.warpsystem.spigot.features.playerwarps.commands;

import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemBaseComponent;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemCommandBuilder;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CPlayerWarp extends WarpSystemCommandBuilder {
    public CPlayerWarp() {
        super("playerwarp", new WarpSystemBaseComponent(WarpSystem.PERMISSION_USE_PLAYER_WARPS) {
            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {

            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                return false;
            }
        }.setOnlyPlayers(true), "pwarp", "pw");

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<PlayerWarp> l = PlayerWarpManager.getManager().getWarps((Player) sender, true);

                for(PlayerWarp warp : l) {
                    suggestions.add(warp.getName(false));
                }

                l.clear();
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                PlayerWarp warp = PlayerWarpManager.getManager().getWarp(argument);

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
