package de.codingair.warpsystem.commands;

import de.CodingAir.v1_6.CodingAPI.Server.FancyMessages.FancyMessage;
import de.CodingAir.v1_6.CodingAPI.Server.FancyMessages.MessageTypes;
import de.CodingAir.v1_6.CodingAPI.Utils.TextAlignment;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import de.codingair.warpsystem.WarpSystem;
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
            sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " info, fileReload");
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

            return list;
        }

        return null;
    }
}
