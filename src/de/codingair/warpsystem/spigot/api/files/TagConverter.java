package de.codingair.warpsystem.spigot.api.files;

import de.codingair.codingapi.files.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class TagConverter {
    protected ConfigFile from, to;
    protected HashMap<String, String> convert = new HashMap<>();

    public TagConverter(ConfigFile from, ConfigFile to) {
        this.from = from;
        this.to = to;
    }

    protected void convert() {
        FileConfiguration fromConfig = this.from.getConfig();
        FileConfiguration toConfig = this.to.getConfig();
        convert.forEach((from, to) -> toConfig.set(to, fromConfig.get(from)));
        convert.clear();
    }
}
