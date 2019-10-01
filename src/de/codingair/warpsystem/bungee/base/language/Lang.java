package de.codingair.warpsystem.bungee.base.language;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lang {
    public static void PREMIUM_CHAT_ONLY_OPED(CommandSender sender) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;

            p.sendTitle(new BungeeTitle()
                    .title(new TextComponent("§7This is a §6Premium §7feature!"))
                    .subTitle(new TextComponent("§7Only §eoped §7players can use this."))
                    .fadeIn(5)
                    .stay(50)
                    .fadeOut(5)
            );
        } else {
            sender.sendMessage("\n"+getPrefix() + "§7This is a §6§lPremium§7 feature! Only §eoped §7players can use this.\n");
        }
    }

    public static void PREMIUM_CHAT(CommandSender sender) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;

            p.sendTitle(new BungeeTitle()
                    .title(new TextComponent("§7This is a §6Premium §7feature!"))
                    .subTitle(new TextComponent("§7Get full access with \"§6/ws upgrade§7\""))
                    .fadeIn(5)
                    .stay(50)
                    .fadeOut(5)
            );
        } else {
            TextComponent tc0 = new TextComponent("\n" + Lang.getPrefix() + "§7This is a ");
            TextComponent premium = new TextComponent("§6§lPremium");
            TextComponent tc1 = new TextComponent(" feature. Buy it now to get full access! §8[");
            TextComponent upgrade = new TextComponent("§6§nUpgrade");
            TextComponent tc2 = new TextComponent("§8]\n");

            tc0.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            tc1.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            tc2.setColor(net.md_5.bungee.api.ChatColor.GRAY);

            premium.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-14.66035/"));
            upgrade.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-14.66035/"));
            upgrade.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("§8» §6§lClick §8«")}));

            tc0.addExtra(premium);
            tc0.addExtra(tc1);
            tc0.addExtra(upgrade);
            tc0.addExtra(tc2);

            sender.sendMessage(tc0);
        }
    }

    public static void initPreDefinedLanguages(Plugin plugin) throws IOException {
        List<String> languages = new ArrayList<>();
        languages.add("ENG.yml");
        languages.add("GER.yml");

        File folder = new File(plugin.getDataFolder(), "/Languages/");
        if(!folder.exists()) mkDir(folder);

        for(String language : languages) {
            InputStream is = plugin.getResourceAsStream("languages/" + language);

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
            total += (long) r;
        }
    }

    public static String getCurrentLanguage() {
        return getConfig().getString("WarpSystem.Language", "ENG");
    }

    public static void setCurrentLanguage(String lang) {
        save(() -> getConfig().set("WarpSystem.Language", lang.toUpperCase()));
    }

    public static String getPrefix() {
        return get("Prefix");
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

        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }

    private static Configuration getConfig() {
        try {
            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
            return file.getConfig();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Configuration getLanguageFile(String langTag) {
        try {
            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile(langTag);
            if(file == null) {
                WarpSystem.getInstance().getFileManager().loadFile(langTag, "/Languages/", "languages/");
                return getLanguageFile(langTag);
            }
            return file.getConfig();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void save(Runnable task) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Language");
        try {
            file.load();
            task.run();
            file.save();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
