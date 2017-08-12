package de.codingair.warpsystem.remastered.commands;

import de.CodingAir.v1_6.CodingAPI.Server.Sound;
import de.codingair.warpsystem.remastered.Language.Example;
import de.codingair.warpsystem.remastered.Language.Lang;
import de.codingair.warpsystem.remastered.WarpSystem;
import de.codingair.warpsystem.remastered.gui.affiliations.Category;
import de.codingair.warpsystem.remastered.gui.guis.GWarps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CWarps implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players", new Example("ENG", "This action is only for players!"), new Example("GER", "Diese Aktion ist nur für Spieler!")));
            return false;
        }

        if(!WarpSystem.activated) return false;

        Player p = (Player) sender;
        Category category = null;

        if(args.length != 0) {
            category = WarpSystem.getInstance().getIconManager().getCategory(args[0]);
        }

        new GWarps(p, category, false).open();
        Sound.LEVEL_UP.playSound(p);
        return false;
    }
}
