package de.codingair.warpsystem.spigot.base.guis.options.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.options.specific.PortalOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PPortals extends PageItem {
    private PortalOptions options;

    public PPortals(Player p, PortalOptions options) {
        super(p, Editor.TITLE_COLOR + "WarpSystem§r §7- §6" + Lang.get("Config"), new ItemBuilder(XMaterial.BLAZE_POWDER).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Portals")).getItem(), false);

        this.options = options;

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(!options.getEnabled().getValue() ? XMaterial.RED_DYE : XMaterial.LIME_DYE)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Status"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (!options.getEnabled().getValue() ?
                                        "§c" + Lang.get("Disabled") :
                                        "§a" + Lang.get("Enabled")), "",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Toggle"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick() && !e.isShiftClick()) {
                    options.getEnabled().setValue(!options.getEnabled().getValue());
                    update();
                    getLast().updateControllButtons();
                }
            }
        }.setOption(option));

        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.BOOK)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Message"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (!options.getSendTeleportMessage().getValue() ?
                                        "§c" + Lang.get("Disabled") :
                                        "§a" + Lang.get("Enabled")), "",
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Toggle"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick() && !e.isShiftClick()) {
                    options.getSendTeleportMessage().setValue(!options.getSendTeleportMessage().getValue());
                    update();
                }
            }
        }.setOption(option));
    }
}