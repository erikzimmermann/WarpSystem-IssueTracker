package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class StatusButton extends SyncButton {
    private final FeatureObject object;

    public StatusButton(int x, int y, FeatureObject object) {
        super(x, y);

        this.object = object;
        update(false);
    }

    @Override
    public ItemStack craftItem() {
        if(object == null) return new ItemStack(Material.AIR);

        return new ItemBuilder(object.isDisabled() ? XMaterial.RED_DYE : XMaterial.LIME_DYE)
                .setName("§6§n" + Lang.get("Status"))
                .setLore("§3" + Lang.get("Current") + ": " + (object.isDisabled() ?
                                "§c" + Lang.get("Disabled") :
                                "§a" + Lang.get("Enabled")), "",
                        object.isDisabled() ? "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Enable_This_Icon") :
                                "§3" + Lang.get("Leftclick") + ": §c" + Lang.get("Disable_This_Icon"))
                .getItem();
    }

    @Override
    public void onClick(InventoryClickEvent e, Player player) {
        if(e.isLeftClick()) {
            object.setDisabled(!object.isDisabled());
            update();
        }
    }
}
