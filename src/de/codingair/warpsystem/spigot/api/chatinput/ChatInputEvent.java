package de.codingair.warpsystem.spigot.api.chatinput;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ChatInputEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private ChatInputGUI gui;
    private String text;
    private boolean close = true;
    private String notifier;
    private Runnable post;

    public ChatInputEvent(ChatInputGUI gui, String text) {
        super(gui.getPlayer());
        this.gui = gui;
        this.text = text;
        this.notifier = gui.getTitle();
    }

    public ChatInputGUI getGui() {
        return gui;
    }

    public String getText() {
        return text;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public String getNotifier() {
        return notifier;
    }

    public void setNotifier(String notifier) {
        this.notifier = notifier;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Runnable getPost() {
        return post;
    }

    public void setPost(Runnable post) {
        this.post = post;
    }
}
