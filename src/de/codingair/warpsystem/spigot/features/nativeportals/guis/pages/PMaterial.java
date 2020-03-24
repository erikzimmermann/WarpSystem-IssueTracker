package de.codingair.warpsystem.spigot.features.nativeportals.guis.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.nativeportals.NativePortal;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.NPEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PMaterial extends PageItem {
    private NativePortal clone;

    public PMaterial(Player p, NativePortal clone) {
        super(p, NPEditor.getMainTitle(), new ItemBuilder(XMaterial.END_PORTAL_FRAME).setName(Editor.ITEM_TITLE_COLOR + Lang.get("NativePortal_Material")).getItem(), false);

        this.clone = clone;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1));

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.WATER_BUCKET).setName("§b§n" + Lang.get("Water_Portal"));

                if(clone.getType() == PortalType.WATER) {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§a" + Lang.get("Enabled"));
                } else {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§c" + Lang.get("Disabled"));
                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Use"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                PortalType next = PortalType.WATER;
                clone.setType(next);
                for(int i = 1; i < 6; i++) ((SyncButton) getButton(i, 2)).update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && clone.getType() != PortalType.WATER;
            }
        }.setOption(option));

        addButton(new SyncButton(2, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.LAVA_BUCKET).setName("§c§n" + Lang.get("Lava_Portal"));

                if(clone.getType() == PortalType.LAVA) {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§a" + Lang.get("Enabled"));
                } else {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§c" + Lang.get("Disabled"));
                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Use"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                PortalType next = PortalType.LAVA;
                clone.setType(next);
                for(int i = 1; i < 6; i++) ((SyncButton) getButton(i, 2)).update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && clone.getType() != PortalType.LAVA;
            }
        }.setOption(option));

        addButton(new SyncButton(3, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.WHITE_STAINED_GLASS).setName("§e§n" + Lang.get("Air_Portal"));

                if(clone.getType() == PortalType.AIR) {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§a" + Lang.get("Enabled"));
                } else {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§c" + Lang.get("Disabled"));
                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Use"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                PortalType next = PortalType.AIR;
                clone.setType(next);

                for(int i = 1; i < 6; i++) ((SyncButton) getButton(i, 2)).update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && clone.getType() != PortalType.AIR;
            }
        }.setOption(option));

        addButton(new SyncButton(4, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.FLINT_AND_STEEL).setName("§4§n" + Lang.get("Nether_Portal"));

                if(clone.getType() == PortalType.NETHER) {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§a" + Lang.get("Enabled"));
                } else {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§c" + Lang.get("Disabled"));
                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Use"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                PortalType next = PortalType.NETHER;
                clone.setType(next);
                for(int i = 1; i < 6; i++) ((SyncButton) getButton(i, 2)).update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && clone.getType() != PortalType.NETHER;
            }
        }.setOption(option));

        addButton(new SyncButton(5, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.END_PORTAL_FRAME).setName("§9§n" + Lang.get("End_Portal"));

                if(clone.getType() == PortalType.END) {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§a" + Lang.get("Enabled"));
                } else {
                    builder.setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§c" + Lang.get("Disabled"));
                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Use"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                PortalType next = PortalType.END;
                clone.setType(next);
                for(int i = 1; i < 6; i++) ((SyncButton) getButton(i, 2)).update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && clone.getType() != PortalType.END;
            }
        }.setOption(option));
    }
}
