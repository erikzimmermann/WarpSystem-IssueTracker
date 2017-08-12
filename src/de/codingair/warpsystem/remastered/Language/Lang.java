package de.codingair.warpsystem.remastered.Language;

import de.CodingAir.v1_6.CodingAPI.Files.ConfigFile;
import de.codingair.warpsystem.remastered.Language.placeholder.Placeholder;
import de.codingair.warpsystem.remastered.WarpSystem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Lang {
    public static String getCurrentLanguage() {
        return getConfig().getString("Default_Language", "ENG");
    }

    public static String getPrefix() {
        String prefix = getConfig().getString("Prefix", "&8» &r");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);

        return prefix;
    }

    public static String get(String key, Example... examples) {
        for (Example example : examples) {
            if(getConfig().getString(example.getLanguage() + "." + key, null) == null) {
                getConfig().set(example.getLanguage() + "." + key, example.getText());
                saveConfig();
            }
        }

        String text = getConfig().getString(getCurrentLanguage() + "." + key, null);

        if(text == null) return null;

        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    public static String get(String key, Placeholder placeholder, Example... examples) {
        String text = get(key, examples);

        text.replace(placeholder.getReplace(), placeholder.getData());

        return text;
    }

    private static FileConfiguration getConfig() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Language");
        return file.getConfig();
    }

    private static void saveConfig() {
        WarpSystem.getInstance().getFileManager().getFile("Language").saveConfig();
    }
}
