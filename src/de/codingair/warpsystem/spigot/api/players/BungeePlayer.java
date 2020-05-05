package de.codingair.warpsystem.spigot.api.players;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.transfer.packets.spigot.MessagePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BungeePlayer {
    private Player player;
    private String name;
    private String displayName;

    public BungeePlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.displayName = player.getDisplayName();
    }

    public BungeePlayer(String name, String displayName) {
        this.player = Bukkit.getPlayer(name);
        this.name = name;
        this.displayName = displayName;
    }

    public BungeePlayer(String name) {
        this(name, name);
    }

    public void sendMessage(String msg) {
        if(player == null) WarpSystem.getInstance().getDataHandler().send(new MessagePacket(name, msg));
        else player.sendMessage(msg);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean onSpigot() {
        return player != null;
    }

    public Player getSpigotPlayer() {
        return player;
    }
}
