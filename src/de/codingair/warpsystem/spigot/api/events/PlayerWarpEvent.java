package de.codingair.warpsystem.spigot.api.events;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.events.utils.Warp;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerWarpEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private Warp warp;
    private Origin origin;
    private String displayName;
    private String message;
    private int seconds;
    private double costs;
    private Callback<TeleportResult> teleportResultCallback;

    public PlayerWarpEvent(Player player, Warp warp, Origin origin, String displayName, String message, int seconds, double costs) {
        super(player);
        this.warp = warp;
        this.origin = origin;
        this.displayName = displayName;
        this.message = message;
        this.seconds = seconds;
        this.costs = costs;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Warp getWarp() {
        return warp;
    }

    public Origin getOrigin() {
        return origin;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds < 0 ? 0 : seconds;
    }

    public double getCosts() {
        return costs;
    }

    public void setCosts(double costs) {
        this.costs = costs < 0 ? 0 : costs;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Callback<TeleportResult> getTeleportResultCallback() {
        return teleportResultCallback;
    }

    public void setTeleportResultCallback(Callback<TeleportResult> teleportResultCallback) {
        this.teleportResultCallback = teleportResultCallback;
    }
}
