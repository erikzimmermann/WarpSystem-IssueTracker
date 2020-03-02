package de.codingair.warpsystem.spigot.features.playerwarps.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Layout;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.entity.Player;

public class PWList extends SimpleGUI {
    public PWList(Player p) {
        super(p,
                new PWLayout(27)
                , new PWPage(p, 27)
                , WarpSystem.getInstance(), false);

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        getMain().initialize(p);
        super.initialize(p);
    }

    private static class PWLayout extends Layout {
        public PWLayout(int size) {
            super(size);
        }

        @Override
        public void initialize() {
            addLine(7, 0, 7, getSize() / 9 - 1, new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem(), true);
        }
    }

    @Override
    public PWPage getMain() {
        return (PWPage) super.getMain();
    }
}
