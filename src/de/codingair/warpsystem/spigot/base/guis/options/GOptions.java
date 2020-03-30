package de.codingair.warpsystem.spigot.base.guis.options;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.ShowIcon;
import de.codingair.warpsystem.spigot.base.guis.options.pages.PGeneral;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.options.OptionBundle;
import de.codingair.warpsystem.spigot.base.utils.options.specific.GeneralOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GOptions extends Editor<OptionBundle> {
    GOptions(Player p, OptionBundle bundle, OptionBundle clone) {
        super(p, clone, new Backup<OptionBundle>(WarpSystem.getInstance().getOptions()) {
            @Override
            public void applyTo(OptionBundle value) {
                bundle.apply(value);
                bundle.write();
            }

            @Override
            public void cancel(OptionBundle value) {

            }
        }, new ShowIcon() {
            @Override
            public ItemStack buildIcon() {
                return new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem();
            }
        }, new PGeneral(p, clone.getOptions(GeneralOptions.class)));
    }

    @Override
    public void updatePageItems() {
        super.updatePageItems();

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new ItemButton(2, new ItemBuilder(XMaterial.ENDER_PEARL).setName(Editor.ITEM_TITLE_COLOR + Lang.get("WarpGUI") + Lang.PREMIUM_LORE).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                Lang.PREMIUM_CHAT(e.getWhoClicked());
            }
        }.setOption(option));

        addButton(new ItemButton(3, new ItemBuilder(XMaterial.END_PORTAL_FRAME).setName(Editor.ITEM_TITLE_COLOR + Lang.get("NativePortals") + Lang.PREMIUM_LORE).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                Lang.PREMIUM_CHAT(e.getWhoClicked());
            }
        }.setOption(option));

        addButton(new ItemButton(4, new ItemBuilder(XMaterial.BLAZE_ROD).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Effect_Portals") + Lang.PREMIUM_LORE).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                Lang.PREMIUM_CHAT(e.getWhoClicked());
            }
        }.setOption(option));

        addButton(new ItemButton(5, new ItemBuilder(XMaterial.OAK_SIGN).setName(Editor.ITEM_TITLE_COLOR + Lang.get("WarpSigns") + Lang.PREMIUM_LORE).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                Lang.PREMIUM_CHAT(e.getWhoClicked());
            }
        }.setOption(option));
    }
}
