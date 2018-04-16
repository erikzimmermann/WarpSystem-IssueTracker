package de.codingair.warpsystem.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.server.fancymessages.FancyMessage;
import de.codingair.codingapi.server.fancymessages.MessageTypes;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.importfilter.ImportType;
import de.codingair.warpsystem.importfilter.Result;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarpSystem extends CommandBuilder {
    public CWarpSystem() {
        super("WarpSystem", new BaseComponent(WarpSystem.PERMISSION_MODIFY) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                Player p = (Player) sender;
                getInfoMessage().send(p);
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                if(player) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players", new Example("ENG", "&cThis is only for players!"), new Example("GER", "&cDies ist nur für Spieler!")));
                }
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " info, fileReload, import");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " info, fileReload, import");
                return false;
            }
        }, true);

        getBaseComponent().addChild(new CommandComponent("info") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Player p = (Player) sender;
                getInfoMessage().send(p);
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new CommandComponent("fileReload") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                try {
                    WarpSystem.getInstance().getFileManager().loadAll();
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Success_Files_Reloaded", new Example("ENG", "&aAll files are reloaded."), new Example("GER", "&aAlle Dateien wurden neu geladen.")));
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("import") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " import §e<CategoryWarps, Essentials>");
                return false;
            }
        });

        getComponent("import").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                suggestions.add("Essentials");
                suggestions.add("CategoryWarps");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                ImportType type = null;

                switch(argument.toLowerCase()) {
                    case "categorywarps": {
                        type = ImportType.CATEGORY_WARPS;
                        break;
                    }

                    case "essentials": {
                        type = ImportType.ESSENTIALS;
                        break;
                    }
                }

                if(type == null) {
                    sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " import §e<CategoryWarps, Essentials>");
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Start", new Example("ENG", "&7Importing data from other systems..."), new Example("GER", "&7Dateien von anderen System werden importiert...")));

                    Result result;
                    if((result = type.importData()).isFinished()) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Finish", new Example("ENG", "&7All files are imported &asuccessfully&7."), new Example("GER", "&7Alle Dateien wurden &aerfolgreich &7importiert.")));
                    } else {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Finish_With_Errors", new Example("ENG", "&7Could &cnot &7import all files."), new Example("GER", "&7Es konnten &cnicht alle Dateien &7importiert werden.")) + " §8[" + result.name() + "]");
                    }
                }
                return false;
            }
        });
    }

    private static FancyMessage getInfoMessage() {
        FancyMessage fancyMessage = new FancyMessage(MessageTypes.INFO_MESSAGE, true, "§6§nWarpSystem", "", "§3Author: §bCodingAir", "§3Version: §b" + WarpSystem.getInstance().getDescription().getVersion(), "", "§eAvailable on SpigotMc!");
        fancyMessage.setAlignment(TextAlignment.CENTER);
        fancyMessage.setCentered(true);
        return fancyMessage;
    }
}