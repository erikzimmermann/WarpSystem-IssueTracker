package de.codingair.warpsystem.spigot.base.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public abstract class GUIList<E> extends SimpleGUI {

    public GUIList(Player p, String title, boolean searchable) {
        super(p, null, new PList(p, searchable, title), WarpSystem.getInstance(), false);

        List<ListItem<E>> itemList = new ArrayList<>();
        addListItems(itemList);
        ((PList<E>) getMain()).initList(itemList);

        initialize(p);
    }

    public abstract void addListItems(List<ListItem<E>> items);

    public abstract void onClick(E value, ClickType clickType);

    public abstract void buildItemDescription(List<String> lore);
}
