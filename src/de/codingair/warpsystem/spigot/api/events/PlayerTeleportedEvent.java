package de.codingair.warpsystem.spigot.api.events;

import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

public class PlayerTeleportedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private Location from;
    private Origin origin;
    private boolean runAfterEffects;

    public PlayerTeleportedEvent(Player who, Location from, Origin origin, boolean runAfterEffects) {
        super(who);
        this.from = from;
        this.origin = origin;
        this.runAfterEffects = runAfterEffects;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Location getFrom() {
        return from.clone();
    }

    public Origin getOrigin() {
        return origin;
    }

    public boolean isRunAfterEffects() {
        return runAfterEffects;
    }

    public void setRunAfterEffects(boolean runAfterEffects) {
        this.runAfterEffects = runAfterEffects;
    }
}
