package de.codingair.warpsystem.spigot.features.tempwarps.guis;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Layout;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import org.bukkit.inventory.ItemStack;

public class StandardLayout extends Layout {
    public StandardLayout() {
        super(27);
    }

    @Override
    public void initialize() {
        ItemStack black = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem();
        ItemStack gray = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem();
        ItemStack leaves = new ItemBuilder(XMaterial.OAK_LEAVES).setHideName(true).getItem();

        setItem(0, 0, leaves);
        setItem(8, 0, leaves);

        setItem(1, 0, black);
        setItem(0, 1, black);
        setItem(0, 2, black);
        setItem(7, 0, black);
        setItem(8, 1, black);
        setItem(8, 2, black);

        setItem(2, 0, gray);
        setItem(1, 1, gray);
        setItem(1, 2, gray);
        setItem(6, 0, gray);
        setItem(7, 1, gray);
        setItem(7, 2, gray);
    }
}
