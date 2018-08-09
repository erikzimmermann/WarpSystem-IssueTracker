package de.codingair.warpsystem.spigot.utils.money;

import org.bukkit.entity.Player;

public interface Adapter {
    double getMoney(Player player);
    void setMoney(Player player, double amount);
}
