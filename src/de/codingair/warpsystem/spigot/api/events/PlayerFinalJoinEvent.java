package de.codingair.warpsystem.spigot.api.events;

import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.UUID;

public class PlayerFinalJoinEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean alreadyTeleported = false;

    private UUID uniqueId;

    public PlayerFinalJoinEvent(Data data) {
        super(data.player);
        this.uniqueId = data.id;
        this.alreadyTeleported = data.isAlreadyTeleported() == null ? TeleportListener.teleport.getIfPresent(player.getName()) != null : data.isAlreadyTeleported();
        TeleportListener.teleport.invalidate(player.getName());
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

    public boolean alreadyTeleported() {
        return alreadyTeleported;
    }

    public static class Data {
        private Player player;
        private UUID id;
        private Boolean alreadyTeleported = null;

        public Data(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public Boolean isAlreadyTeleported() {
            return alreadyTeleported;
        }

        public void setAlreadyTeleported(boolean alreadyTeleported) {
            this.alreadyTeleported = alreadyTeleported;
        }
    }
}
