package de.codingair.warpsystem.spigot.features.warps.simplewarps.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.serializable.SerializableLocation;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.warps.simplewarps.managers.SimpleWarpManager;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CSetWarp extends CommandBuilder {
    public CSetWarp() {
        super("SetWarp", new BaseComponent(WarpSystem.PERMISSION_MODIFY_SIMPLE_WARPS) {
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

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                SimpleWarpManager hManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIMPLE_WARPS);
                for(SimpleWarp value : hManager.getWarps().values()) {
                    suggestions.add(value.getName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                SimpleWarpManager hManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIMPLE_WARPS);
                if(hManager.existsWarp(argument)) {
                    SimpleMessage simpleMessage = new SimpleMessage(Lang.getPrefix() + Lang.get("Warp_Confirm_Overwrite").replace("%WARP%", hManager.getWarp(argument).getName()), WarpSystem.getInstance());

                    simpleMessage.replace("%YES%", new ChatButton(Lang.get("Warp_Confirm_Overwrite_Yes"), Lang.get("Click_Hover")) {
                        @Override
                        public void onClick(Player player) {
                            hManager.getWarp(argument).setLocation(Location.getByLocation(((Player) sender).getLocation()));
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_Overwritten"));
                            simpleMessage.destroy();
                        }
                    });

                    simpleMessage.replace("%NO%", new ChatButton(Lang.get("Warp_Confirm_Overwrite_No"), Lang.get("Click_Hover")) {
                        @Override
                        public void onClick(Player player) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_Not_Overwritten"));
                            simpleMessage.destroy();
                        }
                    });

                    simpleMessage.send((Player) sender);
                    return false;
                }

                //create
                SimpleWarp simpleWarp = new SimpleWarp((Player) sender, argument, null);
                hManager.addWarp(simpleWarp);

                sender.sendMessage(Lang.getPrefix() + Lang.get("SimpleWarp_Created").replace("%WARP%", ChatColor.translateAlternateColorCodes('&', argument)));

                SimpleMessage message = new SimpleMessage(Lang.getPrefix() + Lang.get("SimpleWarp_Created_Edit_Info"), WarpSystem.getInstance());

                TextComponent tc = new TextComponent(Lang.get("SimpleWarp_Created_Edit_Info_EDIT"));
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/editwarp " + argument));
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[]{new TextComponent(Lang.get("Click_Hover"))}));

                message.replace("%EDIT%", tc);

                message.send((Player) sender);
                return false;
            }
        });
    }
}
