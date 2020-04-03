package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class NameButton extends SyncAnvilGUIButton {
    private Value<String> name;
    private boolean acceptNull;

    public NameButton(int x, int y, boolean acceptNull, Value<String> name) {
        super(x, y, ClickType.LEFT);

        this.acceptNull = acceptNull;
        this.name = name;
        update(false);
    }

    @Override
    public boolean canClick(ClickType click) {
        return click == ClickType.LEFT || (click == ClickType.RIGHT && acceptNull);
    }

    @Override
    public ItemStack craftItem() {
        if(name == null) return new ItemStack(Material.AIR);

        return new ItemBuilder(XMaterial.NAME_TAG)
                .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Name"))
                .setLore("§3" + Lang.get("Current") + ": " + (name.getValue() == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', name.getValue()) + "§7'"),
                        "", (name.getValue() == null ? "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set_Name") : "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Name")),
                        (name.getValue() == null || !acceptNull ? null : "§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove")))
                .getItem();
    }

    @Override
    public ItemStack craftAnvilItem(ClickType trigger) {
        if(name == null) return new ItemStack(Material.AIR);

        return new ItemBuilder(Material.PAPER).setName(name.getValue() == null ? Lang.get("Name") + "..." : name.getValue().replace("§", "&")).getItem();
    }

    @Override
    public void onOtherClick(InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT && acceptNull) {
            name.setValue(onChange(name.getValue(), null));
            update();
        }
    }

    @Override
    public void onClick(AnvilClickEvent e) {
        if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

        String input = e.getInput();

        if(!acceptNull && input == null) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
            return;
        }

        String msg;
        if((msg = acceptName(input)) != null) {
            e.getPlayer().sendMessage(msg);
            return;
        }

        e.setClose(true);
        String newName = onChange(name.getValue(), e.getInput());
        name.setValue(newName);
        update();
    }

    public abstract String acceptName(String name);

    public abstract String onChange(String old, String name);

    @Override
    public void onClose(AnvilCloseEvent e) {
    }
}
