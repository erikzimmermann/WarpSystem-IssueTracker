package de.codingair.warpsystem.bungee.base.commands;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.language.Lang;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CWarpSystem extends Command {
    public CWarpSystem() {
        super("warpsystembungee", WarpSystem.PERMISSION_MODIFY_SYSTEM, "wsb");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                long time = System.currentTimeMillis();
                sender.sendMessage(new TextComponent(Lang.getPrefix() + "§7System is §creloading§7..."));
                WarpSystem.getInstance().getFileManager().reloadAll();
                WarpSystem.getInstance().getDataManager().reload();
                sender.sendMessage(new TextComponent(Lang.getPrefix() + "§7...§adone§7! (" + (System.currentTimeMillis() - time) + "ms)"));
                return;
            }
        }

        sender.sendMessage(new TextComponent(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /wsb §e<reload>"));
    }
}
