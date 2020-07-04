package de.codingair.warpsystem.spigot.api;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.configuration.file.FileConfiguration;

public class WSCommandBuilder extends CommandBuilder {
    private static FileConfiguration config = null;

    public WSCommandBuilder(String name, BaseComponent baseComponent) {
        this(name, baseComponent, false);
    }

    public WSCommandBuilder(String name, BaseComponent baseComponent, boolean important) {
        super(WarpSystem.getInstance(), c().getString(name + "." + "Name", name.toLowerCase()), "A WarpSystem-Command", baseComponent, true, important(name, important), normal(name, important));
    }

    private static String[] important(String name, boolean important) {
        if(!important) return null;
        return c().getStringList(name + "." + "Aliases").toArray(new String[0]);
    }

    private static String[] normal(String name, boolean important) {
        if(important) return null;
        return c().getStringList(name + "." + "Aliases").toArray(new String[0]);
    }

    private static FileConfiguration c() {
        if(config == null) config = WarpSystem.getInstance().getFileManager().loadFile("Commands", "/").getConfig();
        return config;
    }
}
