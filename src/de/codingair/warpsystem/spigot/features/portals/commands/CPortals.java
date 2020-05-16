package de.codingair.warpsystem.spigot.features.portals.commands;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CPortals extends WSCommandBuilder {
    public CPortals() {
        super("Portals", new BaseComponent(WarpSystem.PERMISSION_MODIFY_PORTALS) {
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
        }.setOnlyPlayers(true));

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

                        if(PortalManager.getInstance().existsPortal(input)) {
                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                            return;
                        }

                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        if(e.isSubmitted()) {
                            String name = e.getSubmittedText();

                            Portal portal = new Portal(new Destination(), name, new ArrayList<>(), new ArrayList<>());
                            portal.setSpawn(new Location(((Player) sender).getLocation()));
                            e.setPost(() -> new PortalEditor((Player) sender, portal).open());
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
                if(PortalManager.getInstance().existsPortal(argument)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                    return false;
                }

                Portal portal = new Portal(new Destination(), argument, new ArrayList<>(), new ArrayList<>());
                portal.setSpawn(new Location(((Player) sender).getLocation()));
                new PortalEditor((Player) sender, portal).open();
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                PortalManager.getInstance().setGoingToEdit((Player) sender, 0);
                PortalManager.getInstance().setGoingToDelete((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Go_To_Portal"));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                PortalManager.getInstance().setGoingToDelete((Player) sender, 0);
                PortalManager.getInstance().setGoingToEdit((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Go_To_Portal"));
                return false;
            }
        });
    }
}
