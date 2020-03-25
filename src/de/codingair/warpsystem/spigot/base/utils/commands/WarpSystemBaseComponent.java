package de.codingair.warpsystem.spigot.base.utils.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.command.CommandSender;

public abstract class WarpSystemBaseComponent extends BaseComponent {
    public WarpSystemBaseComponent() {
    }

    public WarpSystemBaseComponent(String permission) {
        super(permission);
    }

    @Override
    public void noPermission(CommandSender sender, String label, CommandComponent child) {
        sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
    }

    @Override
    public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
        sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
    }
}
