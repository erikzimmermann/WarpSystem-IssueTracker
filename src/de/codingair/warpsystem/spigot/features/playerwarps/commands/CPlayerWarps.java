package de.codingair.warpsystem.spigot.features.playerwarps.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemBaseComponent;
import de.codingair.warpsystem.spigot.base.utils.commands.WarpSystemCommandBuilder;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CPlayerWarps extends WarpSystemCommandBuilder {
    public CPlayerWarps() {
        super("playerwarps", new WarpSystemBaseComponent(WarpSystem.PERMISSION_USE_PLAYER_WARPS) {
            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {

            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, delete, edit, list>");
                return false;
            }
        }.setOnlyPlayers(true), "pwarps", "pws");

        //list, create, edit, delete, setAsOwnRespawn

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " delete §e<warp>");
                return false;
            }
        });

        getComponent("delete").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<PlayerWarp> l = PlayerWarpManager.getManager().getWarps((Player) sender);

                for(PlayerWarp warp : l) {
                    suggestions.add(warp.getName(false));
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                PlayerWarp warp = PlayerWarpManager.getManager().getWarp(argument);

                if(warp == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    return false;
                }

                if(!warp.isOwner((Player) sender)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_no_access"));
                    return false;
                }

                SimpleMessage message = new SimpleMessage(Lang.getPrefix() + Lang.get("Warp_Delete_Info").replace("%NAME%", warp.getName()), WarpSystem.getInstance());

                List<String> lore = Lang.getStringList("Warp_Delete_Button_Info");
                List<String> prepared = new ArrayList<>();
                for(String s : lore) {
                    prepared.add(s
                            .replace("%REFUND%", cut(PlayerWarpManager.getManager().calculateRefund(warp)) + "")
                            .replace("%NAME%", warp.getName())
                    );
                }

                message.replace("%HERE%", new ChatButton(Lang.get("Warp_Delete_Info_Here"), prepared) {
                    @Override
                    public void onClick(Player player) {
                        double refund = PlayerWarpManager.getManager().delete(warp);
                        if(refund == -1) return;
                        MoneyAdapterType.getActive().deposit((Player) sender, refund);

                        sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_Deleted_Info").replace("%NAME%", warp.getName(true)).replace("%PRICE%", cut(refund) + ""));
                        message.destroy();
                    }
                });

                message.setTimeOut(60);

                message.send(sender);
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " edit §e<warp>");
                return false;
            }
        });

        getComponent("edit").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                List<PlayerWarp> l = PlayerWarpManager.getManager().getWarps((Player) sender);

                for(PlayerWarp warp : l) {
                    suggestions.add(warp.getName(false));
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                PlayerWarp warp = PlayerWarpManager.getManager().getWarp(argument);

                if(warp == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
                    return false;
                }

                if(!warp.isOwner((Player) sender)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_no_access"));
                    return false;
                }

                new PWEditor((Player) sender, warp).open();
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(!PlayerWarpManager.hasPermission((Player) sender)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_Maximum_of_Warps").replace("%AMOUNT%", PlayerWarpManager.getManager().getWarps((Player) sender).size() + ""));
                    return false;
                }

                AnvilGUI.openAnvil(WarpSystem.getInstance(), (Player) sender, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;
                        String input = e.getInput();

                        if(input == null) {
                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            return;
                        }

                        if(PlayerWarpManager.getManager().exists(input)) {
                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                            return;
                        }

                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        if(e.isSubmitted()) {
                            e.setPost(() -> new PWEditor(e.getPlayer(), e.getSubmittedText()).open());
                        }
                    }
                }, new ItemBuilder(XMaterial.NAME_TAG).setName(Lang.get("Name") + "...").getItem());
                return false;
            }
        });

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(!PlayerWarpManager.hasPermission((Player) sender)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Warp_Maximum_of_Warps").replace("%AMOUNT%", PlayerWarpManager.getManager().getWarps((Player) sender).size() + ""));
                    return false;
                }

                if(PlayerWarpManager.getManager().exists(argument)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                    return false;
                }

                new PWEditor((Player) sender, argument).open();
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("list") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                new PWList((Player) sender).open();
                return false;
            }
        });
    }

    public static Number cut(double n) {
        if(n == (int) n) return (int) n;
        else return ((double) (int) (n * 100)) / 100;
    }
}
