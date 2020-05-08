package de.codingair.warpsystem.spigot.features.signs.guis.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncSignGUIButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CommandButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CostsButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.PermissionButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.signs.guis.WarpSignGUI;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        option.setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1));

        addButton(new SyncSignGUIButton(1, 2, sign.getLocation(), true) {
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

        addButton(new CommandButton(2,2, sign).setOption(option));
        addButton(new PermissionButton(3,2, sign).setOption(option));
        addButton(new CostsButton(4,2, sign).setOption(option));
    }
}
