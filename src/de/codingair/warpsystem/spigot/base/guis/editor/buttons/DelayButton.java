package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DelayButton extends SyncButton {
    private final FeatureObject object;

    public DelayButton(int x, int y, FeatureObject object) {
        super(x, y);

        this.object = object;
        update(false);
    }

    @Override
    public ItemStack craftItem() {
        if(object == null) return new ItemStack(Material.AIR);

        return new ItemBuilder(XMaterial.CLOCK)
                .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Delay"))
                .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (object.isSkip() ?
                                "§c" + Lang.get("Disabled") :
                                "§a" + Lang.get("Enabled")), "",
                        object.isSkip() ? "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Enable") :
                                "§3" + Lang.get("Leftclick") + ": §c" + Lang.get("Disable"))
                .getItem();
    }

    @Override
    public void onClick(InventoryClickEvent e, Player player) {
        if(e.isLeftClick()) {
            object.setSkip(!object.isSkip());
            update();
        }
    }

    @Override
    public boolean canClick(ClickType click) {
        return click == ClickType.LEFT;
    }
}
