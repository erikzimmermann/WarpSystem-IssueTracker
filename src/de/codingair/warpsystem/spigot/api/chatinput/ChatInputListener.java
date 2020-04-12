package de.codingair.warpsystem.spigot.api.chatinput;

import de.codingair.codingapi.server.events.PlayerWalkEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatInputListener implements Listener {
    private ChatInputGUI gui;

    public ChatInputListener(ChatInputGUI gui) {
        this.gui = gui;
    }

    @EventHandler
    public void onWalk(PlayerWalkEvent e) {
        if(!e.getPlayer().equals(gui.getPlayer())) return;
        gui.close();
        if(gui.getCancelSound() != null) gui.getCancelSound().play(e.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(!e.getPlayer().equals(gui.getPlayer())) return;

        gui.onInput(e.getMessage());
        e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(!e.getPlayer().equals(gui.getPlayer())) return;

        String msg = e.getMessage();
        if(msg.startsWith("$c.")) {
            msg = msg.substring(3);
        }

        gui.onInput(msg);
        e.setCancelled(true);
        e.getRecipients().clear();
    }

}
