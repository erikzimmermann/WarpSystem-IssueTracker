package de.codingair.warpsystem.spigot.api;

import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.ChatColor;

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
}
