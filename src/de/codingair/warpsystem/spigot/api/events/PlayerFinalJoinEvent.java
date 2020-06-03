package de.codingair.warpsystem.spigot.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.UUID;

public class PlayerFinalJoinEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final UUID uniqueId;

    public PlayerFinalJoinEvent(Player player, UUID id) {
        super(player);
        this.uniqueId = id;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
