package de.codingair.warpsystem.spigot.features.warps.guis.editor;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Page;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PEditor extends Page {
    private Icon icon;
    private Icon backup;
    private PEditor editor;

    public PEditor(Player p, PEditor editor) {
        super(p, "§c§l§n" + Lang.get("Item_Editing"), false);
        this.editor = editor;
    }

    public PEditor(Player p, ItemStack item, String name, int slot, Icon category, boolean isCategory) {
        super(p, "§c§l§n" + Lang.get("Item_Editing"), false);

        this.backup = null;
        this.icon = new Icon(name, item, category, slot, null);
        this.icon.setCategory(isCategory);
    }

    public PEditor(Player p, Icon icon) {
        super(p, "§c§l§n" + Lang.get("Item_Editing"), false);

        this.backup = icon;
        this.icon = icon.clone();
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        addButton(new Button(8, new ItemBuilder(XMaterial.RED_TERRACOTTA).setName("§c" + Lang.get("Cancel")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                new GWarps(p, getIcon().getCategory(), true).open();
            }
        }.setOption(option).setClickSound2(new SoundData(Sound.ITEM_BREAK, 0.7F, 1F)).setCloseOnClick(true));

        addButton(new SyncButton(8, 1) {
            @Override
            public ItemStack craftItem() {
                return getIcon().getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
            }
        }.setBuffering(false));

        addButton(new Button(8, 2, new ItemBuilder(XMaterial.LIME_TERRACOTTA).setName("§a" + Lang.get("Ready")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(getBackup() != null) {
                    getBackup().apply(getIcon());
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Configured"));
                } else {
                    IconManager.getInstance().getIcons().add(getIcon());
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Create_Icon"));
                }

                new GWarps(p, getIcon().getCategory(), true).open();
            }
        });
    }

    public Icon getIcon() {
        if(this.editor != null) return this.editor.getIcon();
        return icon;
    }

    public Icon getBackup() {
        if(this.editor != null) return this.editor.getBackup();
        return backup;
    }

    public SyncButton getShowIcon() {
        return (SyncButton) getButton(8, 1);
    }
}
