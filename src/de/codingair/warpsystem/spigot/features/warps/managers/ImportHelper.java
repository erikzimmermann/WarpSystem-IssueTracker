package de.codingair.warpsystem.spigot.features.warps.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class ImportHelper {
    public static Location stringToLoc(String s){
        if(s == null) return null;

        s = s.replaceAll(",", ".");

        String[] a = s.split(";");

        World w = Bukkit.getWorld(a[0]);

        float x = Float.parseFloat(a[1]);
        float y = Float.parseFloat(a[2]);
        float z = Float.parseFloat(a[3]);
        float yaw = Float.parseFloat(a[4]);
        float pitch = Float.parseFloat(a[5]);

        return new Location(w, x, y, z, yaw, pitch);
    }

    public static ItemStack getItem(String itemCode) {
        ItemStack item = new ItemStack(Material.getMaterial(itemCode.split(";")[0]), Integer.parseInt(itemCode.split(";")[1]));

        return item;
    }
}
