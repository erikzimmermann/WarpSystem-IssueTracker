package de.codingair.warpsystem.spigot.language;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.WarpSystem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Lang {
    public static String getCurrentLanguage() {
        return getConfig().getString("Default_Language", "ENG");
    }

    public static boolean isAvailable(String lang) {
        return getConfig().getString(lang.toUpperCase() + ".Menu_Help", null) != null;
    }

    public static void setCurrentLanguage(String lang) {
        save(() -> getConfig().set("Default_Language", lang.toUpperCase()));
    }

    public static int getLanguageId(String lang) {
        int id = 0;

        for(String key : getLanguages()) {
            if(key.equalsIgnoreCase(lang)) return id;
            id++;
        }

        return -999;
    }

    public static List<String> getLanguages() {
        List<String> languages = new ArrayList<>();

        for(String key : getConfig().getKeys(false)) {
            if(key.equalsIgnoreCase("Default_Language") || key.equalsIgnoreCase("Prefix")) continue;
            languages.add(key);
        }

        return languages;
    }

    public static String getLanguage(int id) {
        return getLanguages().toArray(new String[getLanguages().size()])[id];
    }

    public static String getPrefix() {
        String prefix = getConfig().getString("Prefix", "&8» &r");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);

        return prefix;
    }

    public static String get(String key, Example... examples) {
        for(Example example : examples) {
            String s = getConfig().getString(example.getLanguage() + "." + key, null);
            if(s == null) {
                save(() -> getConfig().set(example.getLanguage() + "." + key, example.getText()));
            }
        }

        String text = getConfig().getString(getCurrentLanguage() + "." + key, null);

        if(text == null) {
            WarpSystem.getInstance().getLogger().log(Level.WARNING, "Unknown translation key: '" + getCurrentLanguage() + "." + key + "' >> Check the Language.yml at '" + getCurrentLanguage() + "." + key + "'");
            return null;
        }

        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    private static FileConfiguration getConfig() {
        try {
            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Language");
            return file.getConfig();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void save(Runnable task) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Language");
        file.loadConfig();
        task.run();
        file.saveConfig();
    }
}
