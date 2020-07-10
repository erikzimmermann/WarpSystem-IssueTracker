package de.codingair.warpsystem.spigot.base.utils.money.adapters;

import de.codingair.warpsystem.spigot.base.utils.money.Adapter;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import org.bukkit.entity.Player;

public class GemsEconomyCurrency implements Adapter {
    private final GemsEconomyAPI instance;

    public GemsEconomyCurrency() {
        instance = new GemsEconomyAPI();
    }

    @Override
    public double getMoney(Player player) {
        return instance.getBalance(id(player));
    }

    @Override
    public void withdraw(Player player, double amount) {
        instance.withdraw(id(player), amount);
    }

    @Override
    public void deposit(Player player, double amount) {
        instance.deposit(id(player), amount);
    }
}
