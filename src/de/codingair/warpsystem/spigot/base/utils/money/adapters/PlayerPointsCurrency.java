package de.codingair.warpsystem.spigot.base.utils.money.adapters;

import de.codingair.warpsystem.spigot.base.utils.money.Adapter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPointsCurrency implements Adapter {
    private final PlayerPointsAPI instance;

    public PlayerPointsCurrency() {
        instance = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
    }

    @Override
    public double getMoney(Player player) {
        return instance.look(id(player));
    }

    @Override
    public void withdraw(Player player, double amount) {
        instance.take(id(player), (int) amount);
    }

    @Override
    public void deposit(Player player, double amount) {
        instance.give(id(player), (int) amount);
    }
}
