package de.codingair.warpsystem.commands;

import de.codingair.codingapi.server.fancymessages.FancyMessage;
import de.codingair.codingapi.server.fancymessages.MessageTypes;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.importfilter.ImportType;
import de.codingair.warpsystem.importfilter.Result;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CWarpSystem implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FancyMessage fancyMessage = new FancyMessage(MessageTypes.INFO_MESSAGE, true, "§6§nWarpSystem", "", "§3Author: §bCodingAir", "§3Version: §b" + WarpSystem.getInstance().getDescription().getVersion(),
                "", "§eAvailable on SpigotMc!");
        fancyMessage.setAlignment(TextAlignment.CENTER);
        fancyMessage.setCentered(true);

        if(!sender.hasPermission(WarpSystem.PERMISSION_MODIFY) && sender instanceof Player) {
            Player p = (Player) sender;
            fancyMessage.send(p);
            return false;
        }

        if(args.length == 0) {
            sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " info, fileReload, import");
            return false;
        }

        switch(args[0].toLowerCase()) {
            case "info": {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players", new Example("ENG", "&cThis is only for players!"), new Example("GER", "&cDies ist nur für Spieler!")));
                    return false;
                }

                Player p = (Player) sender;
                fancyMessage.send(p);
                break;
            }

            case "filereload": {
                WarpSystem.getInstance().getFileManager().reloadAll();
                sender.sendMessage(Lang.getPrefix() + Lang.get("Success_Files_Reloaded", new Example("ENG", "&aAll files are reloaded."), new Example("GER", "&aAlle Dateien wurden neu geladen.")));

                break;
            }

            case "import": {
                if(args.length != 2) {
                    sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " import <CategoryWarps, Essentials>");
                    return false;
                }

                ImportType type = null;

                switch(args[1].toLowerCase()) {
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
                    sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " import <CategoryWarps, Essentials>");
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Start", new Example("ENG", "&7Importing data from other systems..."), new Example("GER", "&7Dateien von anderen System werden importiert...")));

                    Result result;
                    if((result = type.importData()).isFinished()) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Finish", new Example("ENG", "&7All files are imported &asuccessfully&7."), new Example("GER", "&7Alle Dateien wurden &aerfolgreich &7importiert.")));
                    } else {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Finish_With_Errors", new Example("ENG", "&7Could &cnot &7import all files."), new Example("GER", "&7Es konnten &cnicht alle Dateien &7importiert werden.")) + " §8["+result.name()+"]");
                    }
                }

                break;
            }

            default: {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " info, fileReload");
                break;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();

            list.add("Info");
            list.add("FileReload");
            list.add("Import");

            return list;
        } else if(args.length == 2) {
            List<String> list = new ArrayList<>();

            list.add("CategoryWarps");
            list.add("Essentials");

            return list;
        }

        return null;
    }
}
