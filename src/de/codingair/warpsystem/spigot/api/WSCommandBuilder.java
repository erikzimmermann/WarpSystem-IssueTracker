package de.codingair.warpsystem.spigot.api;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.configuration.file.FileConfiguration;

public class WSCommandBuilder extends CommandBuilder {
    private static FileConfiguration config = null;

    public WSCommandBuilder(String name, BaseComponent baseComponent) {
        super(WarpSystem.getInstance(), c().getString(name + "." + "Name", name.toLowerCase()), "A WarpSystem-Command", baseComponent, true, c().getStringList(name + "." + "Aliases").toArray(new String[0]));
    }

    public static FileConfiguration c() {
        if(config == null) config = WarpSystem.getInstance().getFileManager().loadFile("Commands", "/").getConfig();
        return config;
    }
}
