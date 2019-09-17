package de.codingair.warpsystem.spigot.features.simplewarps.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.list.GUIList;
import de.codingair.warpsystem.spigot.base.guis.list.ListItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class GSimpleWarpList extends GUIList<SimpleWarp> {
    public GSimpleWarpList(Player p) {
        super(p, "§c" + Lang.get("SimpleWarps") + "§7- §c" + Lang.get("List") + " §7(%CURRENT%/%MAX%)", true);
    }

    @Override
    public void addListItems(List<ListItem<SimpleWarp>> listItems) {
        for(SimpleWarp value : SimpleWarpManager.getInstance().getWarps().values()) {
            listItems.add(new ListItem<SimpleWarp>(value) {
                @Override
                public ItemStack buildItem() {
                    String world = value.getLocation().getWorldName();
                    double x = round(value.getLocation().getX());
                    double y = round(value.getLocation().getY());
                    double z = round(value.getLocation().getZ());

                    return new ItemBuilder(XMaterial.ENDER_PEARL)
                            .setName("§7\"§r" + ChatColor.highlight(value.getFormattedName(), getSearched(), "§e§n", "§r", true) + "§7\"")
                            .setLore("§7(\"" + ChatColor.highlight(world, getSearched(), "§e§n", "§7", true) + "\", x=" + x + ", y=" + y + ", z=" + z + ")")
                            .getItem();
                }

                @Override
                public void onClick(SimpleWarp value, ClickType clickType) {
                    GSimpleWarpList.this.onClick(value, clickType);
                }

                @Override
                public boolean isSearched(String searching) {
                    return value.getFormattedName().toLowerCase().contains(searching.toLowerCase()) || value.getLocation().getWorldName().toLowerCase().contains(searching.toLowerCase());
                }
            });
        }
    }

    private double round(double d) {
        return ((double) (int) (d * 100)) / 100;
    }
}
