package de.codingair.warpsystem.spigot.features.globalwarps.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.list.GUIList;
import de.codingair.warpsystem.spigot.base.guis.list.ListItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class GGlobalWarpList extends GUIList<String> {
    public GGlobalWarpList(Player p) {
        super(p, "§c" + Lang.get("GlobalWarps") + " §7- §c" + Lang.get("List") + " §7(%CURRENT%/%MAX%)", true);
    }

    @Override
    public void addListItems(List<ListItem<String>> listItems) {
        for(String s : GlobalWarpManager.getInstance().getGlobalWarps().keySet()) {
            String server = GlobalWarpManager.getInstance().getGlobalWarps().get(s);
            listItems.add(new ListItem<String>(s) {
                @Override
                public ItemStack buildItem() {
                    return new ItemBuilder(XMaterial.ENDER_CHEST)
                            .setName("§7\"§r" + ChatColor.highlight(s, getSearched(), "§e§n", "§r", true) + "§7\"")
                            .setLore("§7" + Lang.get("Target_Server") + ": \"§e" + ChatColor.highlight(server, getSearched(), "§e§n", "§e", true) + "§7\")")
                            .getItem();
                }

                @Override
                public void onClick(String value, ClickType clickType) {
                    GGlobalWarpList.this.onClick(value, clickType);
                }

                @Override
                public boolean isSearched(String searching) {
                    return s.toLowerCase().contains(searching.toLowerCase()) || server.toLowerCase().contains(searching.toLowerCase());
                }
            });
        }
    }
}
