package de.codingair.warpsystem.spigot.api;

import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.ChatColor;

import java.util.concurrent.TimeUnit;

public class StringFormatter {
    public static String MINUS_PLUS(String s) {
        return LEFT_RIGHT(s, "-", "+");
    }

    public static String MINUS_PLUS_SHIFT(String s) {
        return LEFT_RIGHT(s, "§7(§e" + Lang.get("Shift") + "§7) §e-", "+ §7(§e" + Lang.get("Shift") + "§7)");
    }

    public static String PREVIOUS_NEXT(String s) {
        return LEFT_RIGHT(s, "«", "»");
    }

    public static String PREVIOUS_NEXT_SHIFT(String s) {
        return LEFT_RIGHT(s, "§7(§e" + Lang.get("Shift") + "§7) §e«", "» §7(§e" + Lang.get("Shift") + "§7)");
    }

    public static String LEFT_RIGHT(String s, String left, String right) {
        return ChatColor.YELLOW.toString() + left + ChatColor.GRAY + " " + Lang.get("Leftclick") + " | " + ChatColor.RED + s + ChatColor.GRAY + " | " + ChatColor.GRAY + Lang.get("Rightclick") + " " + ChatColor.YELLOW + right;
    }

    public static String convertInTimeFormat(long time) {
        return convertInTimeFormat(time, 0, "", "");
    }

    public static String convertInTimeFormat(long time, int highlight, String highlighter, String reset) {
        long days = 0, hours = 0, min = 0, sec = 0;

        if(time > 0) {
            days = Math.max(TimeUnit.DAYS.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
            hours = Math.max(TimeUnit.HOURS.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);
            min = Math.max(TimeUnit.MINUTES.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(min, TimeUnit.MINUTES);
            sec = Math.max(TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS), 0);
            time -= TimeUnit.MILLISECONDS.convert(sec, TimeUnit.SECONDS);
        }

        StringBuilder builder = new StringBuilder();

        if(days > 0 || highlight > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            if(highlight == 1) builder.append(highlighter).append("»");
            builder.append(days).append("d");
            if(highlight == 1) builder.append(highlighter).append("«").append(reset);
        }

        if(hours > 0 || highlight > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            if(highlight == 2) builder.append(highlighter).append("»");
            builder.append(hours).append("h");
            if(highlight == 2) builder.append(highlighter).append("«").append(reset);
        }

        if(min > 0 || highlight > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            if(highlight == 3) builder.append(highlighter).append("»");
            builder.append(min).append("m");
            if(highlight == 3) builder.append(highlighter).append("«").append(reset);
        }

        if((days + hours + min + sec == 0) || highlight == 5 || sec > 0) {
            if(!builder.toString().isEmpty()) builder.append(", ");
            builder.append(sec).append("s");
        }
        return builder.toString();
    }

    public static long convertFromTimeFormat(String text) throws NumberFormatException {
        long d = 0, h = 0, m = 0, s = 0;

        text = text.trim().toLowerCase();

        if(text.contains("d")) {
            String[] a = text.split("d")[0].split(" ");
            d = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("h")) {
            String[] a = text.split("h")[0].split(" ");
            h = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("m")) {
            String[] a = text.split("m")[0].split(" ");
            m = Long.parseLong(a[a.length - 1]);
        }

        if(text.contains("s")) {
            String[] a = text.split("s")[0].split(" ");
            s = Long.parseLong(a[a.length - 1]);
        }

        return TimeUnit.MILLISECONDS.convert(d, TimeUnit.DAYS) + TimeUnit.MILLISECONDS.convert(h, TimeUnit.HOURS) + TimeUnit.MILLISECONDS.convert(m, TimeUnit.MINUTES) + TimeUnit.MILLISECONDS.convert(s, TimeUnit.SECONDS);
    }

    public static long convertFromTimeFormat(String s, long def) {
        if(s == null || (!s.contains("d") && !s.contains("h") && !s.contains("m") && !s.contains("s"))) return def;

        try {
            return convertFromTimeFormat(s);
        } catch(NumberFormatException ex) {
            return def;
        }
    }
}
