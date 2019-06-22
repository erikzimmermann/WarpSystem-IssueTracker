package de.codingair.warpsystem.spigot.features.effectportals.commands;

import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.TimeList;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.effectportals.PortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.guis.GEffectPortalList;
import de.codingair.warpsystem.spigot.features.effectportals.guis.editor.EffectPortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class CPortal extends CommandBuilder {
    public static TimeList<String> aboutToEdit = new TimeList<>();
    public static TimeList<String> aboutToDelete = new TimeList<>();

    public CPortal() {
        super("Portal", new BaseComponent(WarpSystem.PERMISSION_MODIFY_PORTALS) {
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
        }.setOnlyPlayers(true), true);

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
                        ((Player) sender).teleport(value.getStart());
                        Sound.ENDERMAN_TELEPORT.playSound((Player) sender);
                        new PortalEditor((Player) sender, value).start();
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
                EffectPortal effectPortal = new EffectPortal(Location.getByLocation(((Player) sender).getLocation()), new Destination(), null, args[1], null, 2.2, true, true, null);
//                new PortalEditor((Player) sender, new Node<>(args[1], Location.getByLocation(((Player) sender).getLocation()))).start();
                new EffectPortalEditor((Player) sender, effectPortal).open();
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
