package de.codingair.warpsystem.spigot.features.globalwarps.commands;

import de.codingair.codingapi.player.gui.inventory.guis.ConfirmGUI;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class CGlobalWarps extends CommandBuilder {
    public CGlobalWarps() {
        super("GlobalWarps", new BaseComponent(WarpSystem.PERMISSION_MODIFY_GLOBAL_WARPS) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, delete, list>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, delete, list>");
                return false;
            }
        }, true);

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " create §e<name>");
                return false;
            }
        });

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Player player = (Player) sender;

                if(argument.contains(".")) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Create_No_Dots"));
                    return false;
                }

                ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).create(argument, player.getLocation(), new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean created) {
                        if(created) {
                            player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Created").replace("%GLOBAL_WARP%", argument));
                        } else {
                            player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Create_Name_Already_Exists").replace("%GLOBAL_WARP%", argument));
                        }
                    }
                });
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " delete §e<name>");
                return false;
            }
        });

        getComponent("delete").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                suggestions.addAll(((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().keySet());
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                String name = GlobalWarpManager.getInstance().getCaseCorrectlyName(argument);

                if(name != null) {
                    new ConfirmGUI((Player) sender,
                            Lang.get("Confirm"),
                            Lang.get("Apply_Delete_No"),
                            Lang.get("Delete_confirmation_globalwarp").replace("%GLOBAL_WARP%", name),
                            Lang.get("Apply_Delete_Yes"),
                            WarpSystem.getInstance(), new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean keep) {
                            if(keep) {
                                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Deleted_Cancel").replace("%GLOBAL_WARP%", name));
                            } else {
                                ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).delete(argument, new Callback<Boolean>() {
                                    @Override
                                    public void accept(Boolean deleted) {
                                        if(deleted) {
                                            sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Deleted").replace("%GLOBAL_WARP%", name));
                                        } else {
                                            sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", argument));
                                        }
                                    }
                                });
                            }
                        }
                    }).open();
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", argument));
                }
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("list") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                new GGlobalWarpList((Player) sender, new GGlobalWarpList.Listener() {
                    @Override
                    public void onClickOnGlobalWarp(String warp, InventoryClickEvent e) {
                        WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, Origin.GlobalWarp, new Destination(warp, DestinationType.GlobalWarp), warp, 0, true, true,
                                WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.GlobalWarps", true), false, null);
                    }

                    @Override
                    public void onClose() {
                    }

                    @Override
                    public String getLeftclickDescription() {
                        return Lang.get("GlobalWarp_List_Leftclick");
                    }
                }).open();
                return false;
            }
        });
    }
}
