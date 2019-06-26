package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PermissionButton extends SyncAnvilGUIButton {
    private FeatureObject object;

    public PermissionButton(int x, int y, FeatureObject object) {
        super(x, y, ClickType.LEFT);

        this.object = object;
        update(false);
    }

    @Override
    public void onClick(AnvilClickEvent e) {
        if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

        String input = e.getInput();

        if(input == null) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Permission"));
            return;
        }

        e.setClose(true);
        object.setPermission(e.getInput());
        update();
    }

    @Override
    public void onClose(AnvilCloseEvent e) {
    }

    @Override
    public ItemStack craftItem() {
        if(object == null) return new ItemStack(Material.AIR);

        String permission = object.getPermission();

        List<String> lore = new ArrayList<>();
        if(permission != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

        return new ItemBuilder(XMaterial.ENDER_EYE)
                .setName("§6§n" + Lang.get("Permission"))
                .setLore("§3" + Lang.get("Current") + ": " + (permission == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + permission + "§7'"))
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + (permission == null ? Lang.get("Set") : Lang.get("Change")))
                .addLore(lore)
                .getItem();
    }

    @Override
    public ItemStack craftAnvilItem(ClickType trigger) {
        if(object == null) return new ItemStack(Material.AIR);

        return new ItemBuilder(XMaterial.PAPER).setName(object.getPermission() == null ? Lang.get("Permission") + "..." : object.getPermission()).getItem();
    }

    @Override
    public void onOtherClick(InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT) {
            object.setPermission(null);
            update();
        }
    }
}
