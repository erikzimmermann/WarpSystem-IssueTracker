package de.codingair.warpsystem.spigot.base.language;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Lang {
    private static final Cache<String, Boolean> EXIST = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private static ConfigFile config = null;

    public static void initPreDefinedLanguages(JavaPlugin plugin) throws IOException {
        List<String> languages = new ArrayList<>();
        languages.add("ENG.yml");
        languages.add("GER.yml");
        languages.add("ES.yml");
        languages.add("FRA.yml");

        File folder = new File(plugin.getDataFolder(), "/Languages/");
        if(!folder.exists()) mkDir(folder);

        for(String language : languages) {
            InputStream is = plugin.getResource("languages/" + language);

            File file = new File(plugin.getDataFolder() + "/Languages/", language);
            if(!file.exists()) {
                file.createNewFile();
                copy(is, new FileOutputStream(file));
            }
        }
    }

    private static void mkDir(File file) {
        if(!file.getParentFile().exists()) mkDir(file.getParentFile());
        if(!file.exists()) {
            try {
                file.mkdir();
            } catch(SecurityException ex) {
                throw new IllegalArgumentException("Plugin is not permitted to create a folder!");
            }
        }
    }

    private static long copy(InputStream from, OutputStream to) throws IOException {
        if(from == null) return -1;
        if(to == null) throw new NullPointerException();

        byte[] buf = new byte[4096];
        long total = 0L;

        while(true) {
            int r = from.read(buf);
            if(r == -1) {
                return total;
            }

            to.write(buf, 0, r);
            total += r;
        }
    }

    private static boolean exist(String tag) {
        Boolean b = EXIST.getIfPresent(tag);

        if(b == null) {
            b = new File(WarpSystem.getInstance().getDataFolder(), "/Languages/" + tag + ".yml").exists();
            EXIST.put(tag, b);
        }

        return b;
    }

    public static String getCurrentLanguage() {
        String s = getConfig().getString("WarpSystem.Language", "ENG");
        if(exist(s)) return s;
        return "ENG";
    }

    public static String getPrefix() {
        return get("Prefix");
    }

    public static List<String> getStringList(String key) {
        List<String> l = getLanguageFile(getCurrentLanguage()).getStringList(key);
        List<String> prepared = new ArrayList<>();

        for(String s : l) {
            if(s == null) prepared.add(null);
            else prepared.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        return prepared;
    }

    public static String get(String key) {
        String text = getLanguageFile(getCurrentLanguage()).getString(key);

        if(text == null) {
            if(key.equalsIgnoreCase("Yes") && get("true") != null) {
                String s = get("true");
                return s.equalsIgnoreCase("true") ? "Yes" : s;
            } else if(key.equalsIgnoreCase("No") && get("false") != null) {
                String s = get("false");
                return s.equalsIgnoreCase("false") ? "No" : s;
            }

            throw new IllegalStateException("Unknown translation key: '" + key + "' >> Check " + getCurrentLanguage() + ".yml at '" + key + "'");
        }

        return prepare(text);
    }

    private static String prepare(String s) {
        s = s.replace("\\n", "\n");
        s = ChatColor.translateAlternateColorCodes('&', s);
        s = s.replace("%CURRENCY%", Bank.name());
        return s;
    }

    private static FileConfiguration getConfig() {
        if(config == null) {
            try {
                config = WarpSystem.getInstance().getFileManager().getFile("Config");
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return config.getConfig();
    }

    private static FileConfiguration getLanguageFile(String langTag) {
        try {
            ConfigFile file = WarpSystem.getInstance().getFileManager().loadFile(langTag, "/Languages/", "languages/");
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
