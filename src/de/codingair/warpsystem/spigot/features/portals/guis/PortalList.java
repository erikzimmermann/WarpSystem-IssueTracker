package de.codingair.warpsystem.spigot.features.portals.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.list.GUIList;
import de.codingair.warpsystem.spigot.base.guis.list.ListItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class PortalList extends GUIList<Portal> {
    public PortalList(Player p) {
        super(p, Editor.TITLE_COLOR + Lang.get("Portals"), true);
    }

    public static Number cut(double n) {
        double d = ((double) (int) (n * 100)) / 100;
        if(d == (int) d) return (int) d;
        else return d;
    }

    @Override
    public void addListItems(List<ListItem<Portal>> listItems) {
        for(Portal portal : PortalManager.getInstance().getPortals()) {
            if(portal.getSpawn() == null) continue;

            listItems.add(new ListItem<Portal>(portal) {
                @Override
                public ItemStack buildItem() {
                    String pos = "§7x=§f" + cut(portal.getSpawn().getX()) + "§7, y=§f" + cut(portal.getSpawn().getY()) + "§7, z=§f" + cut(portal.getSpawn().getZ());

                    ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL)
                            .setName(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Name") + ": §7'§f" + ChatColor.translateAlternateColorCodes('&', ChatColor.highlight(portal.getDisplayName(), getSearched(), "§e§n", "§7", true)) + "§7'")
                            .addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("World") + ": §7'§f" + ChatColor.highlight(portal.getSpawn().getWorldName(), getSearched(), "§e§n", "§7", true) + "§7'",
                                    Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Position") + ": " + pos);

                    buildItemDescription(builder.getLore());
                    return builder.getItem();
                }

                @Override
                public void onClick(Portal value, ClickType clickType) {
                    PortalList.this.onClick(value, clickType);
                }

                @Override
                public boolean isSearched(String searching) {
                    searching = searching.toLowerCase();
                    return ChatColor.stripColor(portal.getDisplayName()).toLowerCase().contains(searching) || (portal.getSpawn() != null && portal.getSpawn().getWorldName().toLowerCase().contains(searching));
                }
            });
        }
    }
}
