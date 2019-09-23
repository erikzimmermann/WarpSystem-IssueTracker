package de.codingair.warpsystem.spigot.features.nativeportals.commands;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.MultiCommandComponent;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.nativeportals.NativePortal;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.NPEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.managers.NativePortalManager;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CNativePortals extends CommandBuilder {
    public CNativePortals() {
        super("NativePortals", "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_MODIFY_NATIVE_PORTALS) {
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
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, edit, delete>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<create, edit, delete>");
                return false;
            }
        }, true, "nativeportal", "np", "nps");

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
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

                            NativePortal nativePortal = new NativePortal(PortalType.WATER, new Destination(), name, new ArrayList<>());
                            NativePortal clone = nativePortal.clone();

                            clone.setEditMode(true);

                            e.setPost(() -> {
                                new NPEditor((Player) sender, nativePortal, clone).open();
                            });
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
                NativePortal nativePortal = new NativePortal(PortalType.WATER, new Destination(), argument, new ArrayList<>());
                NativePortal clone = nativePortal.clone();

                clone.setEditMode(true);

                new NPEditor((Player) sender, nativePortal, clone).open();
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                NativePortalManager.getInstance().setGoingToEdit((Player) sender, 0);
                NativePortalManager.getInstance().setGoingToDelete((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Go_To_NativePortal"));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                NativePortalManager.getInstance().setGoingToDelete((Player) sender, 0);
                NativePortalManager.getInstance().setGoingToEdit((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Go_To_NativePortal"));
                return false;
            }
        });
    }
}
