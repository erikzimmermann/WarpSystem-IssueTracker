package de.codingair.warpsystem.spigot.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTeleportAcceptEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public PlayerTeleportAcceptEvent(Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
