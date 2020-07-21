package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CooldownButton extends SyncButton {
    private final FeatureObject object;

    public CooldownButton(int x, int y, FeatureObject object) {
        super(x, y);

        this.object = object;
        update(false);
    }

    @Override
    public ItemStack craftItem() {
        if(object == null) return new ItemStack(Material.AIR);

        return new ItemBuilder(XMaterial.CLOCK)
                .setName("§6§n" + Lang.get("Cooldown") + Lang.PREMIUM_LORE)
                .setLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set"))
                .getItem();
    }

    @Override
    public void onClick(InventoryClickEvent e, Player player) {
        Lang.PREMIUM_CHAT(player);
    }

    @Override
    public boolean canClick(ClickType click) {
        return click == ClickType.LEFT;
    }
}
