package de.codingair.warpsystem.spigot.features.signs.guis.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncSignGUIButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.signs.guis.WarpSignGUI;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OptionPage extends PageItem {
    private WarpSign sign;
    private Sign s;

    public OptionPage(Player p, WarpSign sign) {
        super(p, WarpSignGUI.getMainTitle(), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.sign = sign;
        s = (Sign) sign.getLocation().getBlock().getState();

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1));

        addButton(new SyncSignGUIButton(19, sign.getLocation(), true) {
            private String[] lines = s.getLines();

            @Override
            public void onSignChangeEvent(String[] lines) {
                this.lines = lines;
                ((WarpSignGUI.ShowIcon) getLast().getShowIcon()).applyLines(lines);
                update();
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.WRITABLE_BOOK).setName("§6§n" + Lang.get("Description"));

                builder.setLore("§3" + Lang.get("Current") + ":");

                for(String line : lines == null ? s.getLines() : lines) {
                    builder.addLore("§7- '§r" + (line == null ? "" : ChatColor.translateAlternateColorCodes('&', line)) + "§7'");
                }

                builder.addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Edit"));

                return builder.getItem();
            }
        }.setOption(option).setOnlyLeftClick(true));

        addButton(new SyncAnvilGUIButton(2, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                String permission = sign.getPermission();

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
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Permission"));
                    return;
                }

                e.setClose(true);
                sign.setPermission(e.getInput());
                update();
            }

            @Override
            public void onClose(AnvilCloseEvent e) {

            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(XMaterial.PAPER).setName(sign.getPermission() == null ? Lang.get("Permission") + "..." : sign.getPermission()).getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    sign.setPermission(null);
                    update();
                }
            }
        }.setOption(option));
    }
}
