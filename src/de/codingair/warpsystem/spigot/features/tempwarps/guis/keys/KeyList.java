package de.codingair.warpsystem.spigot.features.tempwarps.guis.keys;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.guis.list.GUIList;
import de.codingair.warpsystem.spigot.base.guis.list.ListItem;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyList extends GUIList<Key> {
    public KeyList(Player p) {
        super(p, "§c" + Lang.get("Key_Templates") + " §7(§e%CURRENT%§7/§e%MAX%§7)", false);
    }

    @Override
    public void addListItems(List<ListItem<Key>> listItems) {
        for(Key key : TempWarpManager.getManager().getTemplates()) {
            listItems.add(new ListItem<Key>(key) {
                @Override
                public ItemStack buildItem() {
                    List<String> lore = new ArrayList<>();
                    buildItemDescription(lore);

                    ItemStack item = new ItemBuilder(key.getItem()).addLore(lore).getItem();

                    lore.clear();
                    return item;
                }

                @Override
                public void onClick(Key value, ClickType clickType) {
                    KeyList.this.onClick(value, clickType);
                }

                @Override
                public boolean isSearched(String searching) {
                    return false;
                }
            });
        }
    }
}
