package de.codingair.warpsystem.spigot.features.globalwarps.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.player.gui.inventory.guis.ConfirmGUI;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class CGlobalWarps extends WSCommandBuilder implements BungeeFeature {
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
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<create, delete, list>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(WarpSystem.getInstance().isOnBungeeCord()) sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<create, delete, list>");
                else sender.sendMessage(Lang.getPrefix() + Lang.get("Connect_BungeeCord"));
                return false;
            }
        });

        WarpSystem.getInstance().getBungeeFeatureList().add(this);
    }

    @Override
    public void onConnect() {
        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " create " + WarpSystem.opt().cmdArg() + "<name>");
                return false;
            }
        });

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                suggestions.addAll(GlobalWarpManager.getInstance().getGlobalWarps().keySet());
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
                            String name = GlobalWarpManager.getInstance().getCaseCorrectlyName(argument);
                            SimpleMessage simpleMessage = new SimpleMessage(Lang.getPrefix() + Lang.get("Warp_Confirm_Overwrite").replace("%WARP%", name), WarpSystem.getInstance());

                            simpleMessage.replace("%YES%", new ChatButton(Lang.get("Warp_Confirm_Overwrite_Yes"), Lang.get("Click_Hover")) {
                                @Override
                                public void onClick(Player player) {
                                    ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).updatePosition(name, player.getLocation(), new Callback<Boolean>() {
                                        @Override
                                        public void accept(Boolean overwritten) {
                                            if(overwritten) {
                                                sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_Overwritten"));
                                            } else {
                                                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", args[1]));
                                            }
                                        }
                                    });
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
                        }
                    }
                });
                return false;
            }
        });

        getComponent("create", null).addChild(new CommandComponent("true") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Player player = (Player) sender;
                String name = GlobalWarpManager.getInstance().getCaseCorrectlyName(args[1]);

                if(name != null) {
                    ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).updatePosition(name, player.getLocation(), new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean overwritten) {
                            if(overwritten) {
                                sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_Overwritten"));
                            } else {
                                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", args[1]));
                            }
                        }
                    });
                } else getComponent("create", null).runCommand(sender, args[1], args);
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " delete " + WarpSystem.opt().cmdArg() + "<name>");
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
                                ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).delete(name, new Callback<Boolean>() {
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

        getComponent("delete", null).addChild(new CommandComponent("true") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                String name = GlobalWarpManager.getInstance().getCaseCorrectlyName(args[1]);

                if(name != null) {
                    ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).delete(name, new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean deleted) {
                            if(deleted) {
                                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Deleted").replace("%GLOBAL_WARP%", name));
                            } else {
                                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Not_Exists").replace("%GLOBAL_WARP%", args[1]));
                            }
                        }
                    });
                } else getComponent("delete", null).runCommand(sender, args[1], args);
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("list") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                new GGlobalWarpList((Player) sender) {
                    @Override
                    public void onClick(String warp, ClickType clickType) {
                        TeleportOptions options = new TeleportOptions(new Destination(warp, DestinationType.GlobalWarp), warp);
                        options.setOrigin(Origin.GlobalWarp);
                        options.setSkip(true);

                        WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, options);
                    }

                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void buildItemDescription(List<String> lore) {
                        lore.add("");
                        lore.add(Lang.get("GlobalWarp_List_Leftclick"));
                    }
                }.open();
                return false;
            }
        });
    }

    @Override
    public void onDisconnect() {
        getBaseComponent().removeChild("create");
        getBaseComponent().removeChild("delete");
        getBaseComponent().removeChild("list");
    }
}
