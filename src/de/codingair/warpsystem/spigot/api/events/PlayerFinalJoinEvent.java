package de.codingair.warpsystem.spigot.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.UUID;

public class PlayerFinalJoinEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private UUID uniqueId;

    public PlayerFinalJoinEvent(Player player, UUID uniqueId) {
        super(player);
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
