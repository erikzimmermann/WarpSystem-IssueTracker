package de.codingair.warpsystem.spigot.features.warps.simplewarps.commands;

import de.codingair.codingapi.player.gui.inventory.guis.ConfirmGUI;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CDeleteWarp extends CommandBuilder {
    public CDeleteWarp() {
        super("DeleteWarp", new BaseComponent(WarpSystem.PERMISSION_MODIFY_SIMPLE_WARPS) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
                return false;
            }
        }, true);

        setHighestPriority(true);

        SimpleWarpManager m = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIMPLE_WARPS);

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(SimpleWarp value : m.getWarps().values()) {
                    suggestions.add(value.getName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(!m.existsWarp(argument)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    return false;
                }

                new ConfirmGUI((Player) sender,
                        Lang.get("Delete"),
                        "§a" + Lang.get("No"),
                        Lang.get("SimpleWarp_Confirm_Delete"),
                        "§c" + Lang.get("Yes"),
                        WarpSystem.getInstance(), new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean accepted) {
                        if(!accepted) {
                            SimpleWarp warp = m.getWarp(argument);
                            m.removeWarp(warp);
                            sender.sendMessage(Lang.getPrefix() + Lang.get("SimpleWarp_Deleted").replace("%WARP%", warp.getName()));
                        } else {
                            SimpleWarp warp = m.getWarp(argument);
                            sender.sendMessage(Lang.getPrefix() + Lang.get("SimpleWarp_Not_Deleted").replace("%WARP%", warp.getName()));
                        }
                    }
                }).open();
                return false;
            }
        });
    }
}
