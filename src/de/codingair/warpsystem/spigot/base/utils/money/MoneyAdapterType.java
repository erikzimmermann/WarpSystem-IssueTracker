package de.codingair.warpsystem.spigot.base.utils.money;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.money.adapters.Essentials;
import de.codingair.warpsystem.spigot.base.utils.money.adapters.Vault;
import org.bukkit.Bukkit;

import java.util.List;

public enum MoneyAdapterType {
    ESSENTIALS(Bukkit.getPluginManager().isPluginEnabled("Essentials") ? new Essentials() : null),
    VAULT(Bukkit.getPluginManager().isPluginEnabled("Vault") ? new Vault() : null);

    private Adapter adapter;

    MoneyAdapterType(Adapter adapter) {
        this.adapter = adapter;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public static Adapter getActive() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        List<String> priority = file.getConfig().getStringList("WarpSystem.Economy.priority");

        if(priority != null && priority.size() > 0) {
            for(String s : priority) {
                for(MoneyAdapterType value : values()) {
                    if(value.name().equalsIgnoreCase(s)) {
                        if(value.getAdapter() != null) return value.getAdapter();
                        else break;
                    }
                }
            }
        }

        for(MoneyAdapterType moneyAdapterType : values()) {
            if(moneyAdapterType.getAdapter() != null) return moneyAdapterType.getAdapter();
        }

        return null;
    }

    public static boolean canEnable() {
        return getActive() != null;
    }
}
