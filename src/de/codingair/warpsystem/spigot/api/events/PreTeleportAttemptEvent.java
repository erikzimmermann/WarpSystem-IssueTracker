package de.codingair.warpsystem.spigot.api.events;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PreTeleportAttemptEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Destination destination;
    private final Callback teleportFinisher;
    private String hotBarMessage;
    private boolean waitForCallback = false;

    public PreTeleportAttemptEvent(Player who, Callback teleportFinisher, Destination destination) {
        super(who);
        this.teleportFinisher = teleportFinisher;
        this.destination = destination;
    }

    public Destination getDestination() {
        return destination;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getHotbarMessage() {
        return hotBarMessage;
    }

    public void setHotbarMessage(String hotBarMessage) {
        this.hotBarMessage = hotBarMessage;
    }

    public boolean isWaitForCallback() {
        return waitForCallback;
    }

    public void setWaitForCallback(boolean waitForCallback) {
        this.waitForCallback = waitForCallback;
    }

    public Callback getTeleportFinisher() {
        return teleportFinisher;
    }
}
