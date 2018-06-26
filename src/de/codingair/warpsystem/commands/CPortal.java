package de.codingair.warpsystem.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.TimeList;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.features.portals.PortalEditor;
import de.codingair.warpsystem.gui.guis.GPortalList;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class CPortal extends CommandBuilder {
    public static TimeList<String> aboutToEdit = new TimeList<>();
    public static TimeList<String> aboutToDelete = new TimeList<>();
    private HashMap<String, Node<String, Location>> locations = new HashMap<>();

    public CPortal() {
        super("Portal", new BaseComponent("WarpSystem.Modify.Portals") {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission", new Example("GER", "&cDu hast keine Berechtigungen für diese Aktion!"), new Example("ENG", "&cYou don't have permissions for that action!"), new Example("FRE", "&cDésolé mais vous ne possédez la permission pour exécuter cette action!")));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_HELP", new Example("ENG", "&7Use: &e/" + label + " <create, edit, delete, list>"), new Example("GER", "&7Benutze: &e/" + label + " <create, edit, delete, list>")));
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_HELP", new Example("ENG", "&7Use: &e/" + label + " <create, edit, delete, list>"), new Example("GER", "&7Benutze: &e/" + label + " <create, edit, delete, list>")));

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
                new GPortalList((Player) sender).open();
                return false;
            }
        });


        //CREATE

        getBaseComponent().addChild(new CommandComponent("create") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_CREATE_HELP", new Example("ENG", "&7Use: &e/" + label + " create <Position-Name>"), new Example("GER", "&7Benutze: &e/" + label + " create <Position-Name>")));
                return false;
            }
        });

        getComponent("create").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(locations.containsKey(sender.getName())) {
                    Node<String, Location> first = locations.remove(sender.getName());
                    Node<String, Location> second = new Node<>(args[1], Location.getByLocation(((Player) sender).getLocation().clone()));

                    new PortalEditor((Player) sender, first, second).start();
                } else {
                    locations.put(sender.getName(), new Node<>(args[1], Location.getByLocation(((Player) sender).getLocation().clone())));
                    sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_CREATE_FIRST_SAVED", new Example("ENG", "&7The first position has been saved."), new Example("GER", "&7Die erste Position wurde gespeichert.")));
                    sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_CREATE_NEXT", new Example("ENG", "&7Run this command at the next required position again."), new Example("GER", "&7Führe diesen Befehl erneut an der gewünschten Zielposition aus.")));
                }

                return false;
            }
        });


        //EDIT

        getBaseComponent().addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                aboutToDelete.remove(sender.getName());

                aboutToEdit.add(sender.getName(), 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_GO_TO_PORTAL", new Example("ENG", "&7You have 30 seconds to go into a portal."), new Example("GER", "&7Du hast nun 30 Sekunden Zeit, um in ein Portal zu gehen.")));
                return false;
            }
        });


        //DELETE

        getBaseComponent().addChild(new CommandComponent("delete") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                aboutToEdit.remove(sender.getName());

                aboutToDelete.add(sender.getName(), 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("PORTAL_GO_TO_PORTAL", new Example("ENG", "&7You have 30 seconds to go into a portal."), new Example("GER", "&7Du hast nun 30 Sekunden Zeit, um in ein Portal zu gehen.")));
                return false;
            }
        });
    }
}
