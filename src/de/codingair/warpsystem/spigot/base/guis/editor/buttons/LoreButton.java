package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class LoreButton extends SyncAnvilGUIButton {
    private ItemBuilder toChange;

    public LoreButton(int x, int y, ItemBuilder toChange) {
        super(x, y, ClickType.LEFT);

        this.toChange = toChange;
        update(false);
    }

    public abstract void updatingLore(ItemBuilder toChange);

    @Override
    public ItemStack craftItem() {
        if(toChange == null) return new ItemStack(Material.AIR);

        List<String> loreOfItem = toChange.getLore();
        List<String> lore = new ArrayList<>();
        if(loreOfItem == null) lore = null;
        else {
            for(String s : loreOfItem) {
                lore.add("§7- '§r" + s + "§7'");
            }
        }

        List<String> lore2 = new ArrayList<>();
        if(lore != null && !lore.isEmpty()) lore2.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

        return new ItemBuilder(XMaterial.PAPER)
                .setName("§6§n" + Lang.get("Description"))
                .setLore("§3" + Lang.get("Current") + ": " + (lore == null || lore.isEmpty() ? "§c" + Lang.get("Not_Set") : ""))
                .addLore(lore)
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add_Line"))
                .addLore(lore2)
                .getItem();
    }

    @Override
    public ItemStack craftAnvilItem(ClickType trigger) {
        return new ItemBuilder(Material.PAPER).setName(Lang.get("Line") + "...").getItem();
    }

    @Override
    public void onOtherClick(InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT) {
            if(!toChange.getLore().isEmpty()) toChange.getLore().remove(toChange.getLore().size() - 1);
            updatingLore(toChange);
            update();
        }
    }

    @Override
    public void onClick(AnvilClickEvent e) {
        if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

        String input = e.getInput();

        if(input == null) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Lore"));
            return;
        }

        e.setClose(true);
        playSound(e.getPlayer());

        toChange.addLore(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', input));
        updatingLore(toChange);
        update();
    }

    @Override
    public void onClose(AnvilCloseEvent e) {
    }
}
