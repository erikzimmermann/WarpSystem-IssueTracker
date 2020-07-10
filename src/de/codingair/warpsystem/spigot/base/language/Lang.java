package de.codingair.warpsystem.spigot.base.language;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.codingapi.API;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.tools.time.TimeList;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Lang {
    private static final Cache<String, Boolean> EXIST = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private static ConfigFile config = null;
    public static final String PREMIUM_HOTBAR = "§8» §6§lPremium feature §8«";
    public static final String PREMIUM_LORE = "§r §8(§6Premium§8)";
    private static final TimeList<CommandSender> premiumMessage = new TimeList<>();

    public static void PREMIUM_CHAT_ONLY_OPED(CommandSender sender) {
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if(API.getRemovable(p, GUI.class) != null) return;

            MessageAPI.sendTitle(p, "§7This is a §6Premium §7feature!", "§7Only §eoped §7players can use this.", 5, 50, 5);
        } else {
            sender.sendMessage("\n" + getPrefix() + "§7This is a §6§lPremium§7 feature! Only §eoped §7players can use this.\n");
        }
    }

    public static void PREMIUM_CHAT(CommandSender sender) {
        TextComponent tc0 = new TextComponent("\n" + Lang.getPrefix() + "§7This is a ");
        TextComponent premium = new TextComponent("§6§lPremium");
        TextComponent tc1 = new TextComponent(" feature. Buy it now to get full access!");

        tc0.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        tc1.setColor(net.md_5.bungee.api.ChatColor.GRAY);

        premium.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-14.66035/"));

        tc0.addExtra(premium);
        tc0.addExtra(tc1);
        PREMIUM_CHAT(tc0, sender);
    }

    public static void PREMIUM_TITLE(Player player, String title) {
        if(API.getRemovable(player, GUI.class) != null) return;

        MessageAPI.sendTitle(player, title, "§7Get full access with \"§6/ws upgrade§7\"", 5, 50, 5);
    }

    public static void PREMIUM_CHAT(TextComponent base, CommandSender sender) {
        PREMIUM_CHAT(base, sender, true);
    }

    public static void PREMIUM_CHAT(TextComponent base, CommandSender sender, boolean chat) {
        if(!chat && sender instanceof Player) {
            PREMIUM_TITLE((Player) sender, "§7This is a §6Premium §7feature!");
        } else {
            if(premiumMessage.contains(sender)) return;

            TextComponent tc1 = new TextComponent(" §8[");
            TextComponent upgrade = new TextComponent("§6§nUpgrade");
            TextComponent tc2 = new TextComponent("§8]");

            tc1.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            tc2.setColor(net.md_5.bungee.api.ChatColor.GRAY);

            upgrade.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-14.66035/"));
            upgrade.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("§8» §6§lClick §8«")}));

            base.addExtra(tc1);
            base.addExtra(upgrade);
            base.addExtra(tc2);

            if(sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(base);
            } else sender.sendMessage(base.getText());

            premiumMessage.add(sender, 5);
        }
    }

    public static void PREMIUM_CHAT_UPGRADE(CommandSender sender) {
        if(premiumMessage.contains(sender)) return;

        TextComponent tc0 = new TextComponent("\n" + Lang.getPrefix() + "§7Thank you for thinking about an ");
        TextComponent upgrade = new TextComponent("§6§nupgrade");
        TextComponent tc2 = new TextComponent("§7!\n");

        tc0.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        tc2.setColor(net.md_5.bungee.api.ChatColor.GRAY);

        upgrade.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-14.66035/"));
        upgrade.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("§8» §6§lClick §8«")}));

        tc0.addExtra(upgrade);
        tc0.addExtra(tc2);

        if(sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(tc0);
        } else sender.sendMessage(tc0.getText());

        premiumMessage.add(sender, 10);
    }

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
