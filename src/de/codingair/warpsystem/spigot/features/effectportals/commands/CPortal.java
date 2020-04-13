package de.codingair.warpsystem.spigot.features.effectportals.commands;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.tools.TimeList;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.effectportals.EffectPortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.guis.GEffectPortalList;
import de.codingair.warpsystem.spigot.features.effectportals.managers.EffectPortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class CPortal extends CommandBuilder {
    public static TimeList<String> aboutToEdit = new TimeList<>();
    public static TimeList<String> aboutToDelete = new TimeList<>();

    public CPortal() {
        super("portal", "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_MODIFY_PORTALS) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, edit, delete, list>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, edit, delete, list>");

                return false;
            }
        }.setOnlyPlayers(true), true, "portals");

        try {
            setHighestPriority(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Dominate_In_Commands.Highest_Priority.Portal", true));
        } catch(Exception e) {
            e.printStackTrace();
        }


        //LIST
        getBaseComponent().addChild(new CommandComponent("list") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                new GEffectPortalList((Player) sender) {
                    @Override
                    public void onClick(EffectPortal value, ClickType clickType) {
                        ((Player) sender).teleport(value.getLocation());
                        Sound.ENDERMAN_TELEPORT.playSound((Player) sender);
                        new EffectPortalEditor((Player) sender, value).start();
                    }

                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void buildItemDescription(List<String> lore) {
                        lore.add("");
                        lore.add("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Edit"));
                    }
                }.open();
                return false;
            }
        });


        //CREATE

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(EffectPortalManager.getInstance().getPortal(((Player) sender).getLocation()) != null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Location_already_in_use"));
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

                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        if(e.isSubmitted()) {
                            String name = e.getSubmittedText();

                            e.setPost(() -> new EffectPortalEditor((Player) sender, name).start());
                        }
                    }
                }, new ItemBuilder(Material.PAPER).setName(Lang.get("Name") + "...").getItem());
                return false;
            }
        });

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(EffectPortalManager.getInstance().getPortal(((Player) sender).getLocation()) != null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Location_already_in_use"));
                    return false;
                }

                new EffectPortalEditor((Player) sender, argument).start();
                return false;
            }
        });


        //EDIT

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                aboutToDelete.remove(sender.getName());

                aboutToEdit.add(sender.getName(), 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_GO_TO_PORTAL"));
                return false;
            }
        });


        //DELETE

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                aboutToEdit.remove(sender.getName());

                aboutToDelete.add(sender.getName(), 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_GO_TO_PORTAL"));
                return false;
            }
        });
    }
}
