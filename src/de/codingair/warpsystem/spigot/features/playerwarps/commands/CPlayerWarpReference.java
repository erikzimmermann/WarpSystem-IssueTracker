package de.codingair.warpsystem.spigot.features.playerwarps.commands;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CPlayerWarpReference extends CommandBuilder {
    public CPlayerWarpReference(String main, String[] aliases) {
        super(WarpSystem.getInstance(), main, "A WarpSystem-Command", new BaseComponent(WarpSystem.PERMISSION_USE_PLAYER_WARPS) {
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
                sendMessage((Player) sender);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sendMessage((Player) sender);
                return false;
            }
        }.setOnlyPlayers(true), true, aliases);
    }

    private static void sendMessage(Player player) {
        SimpleMessage msg = new SimpleMessage(Lang.getPrefix() + Lang.get("TempWarp_Reference_To_PlayerWarps"), WarpSystem.getInstance());

        TextComponent tc = new TextComponent("§e§n/playerwarps§7");
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent(Lang.get("Click_Hover"))}));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/playerwarps "));

        msg.replace("/playerwarps", tc);

        msg.send(player);
    }
}
