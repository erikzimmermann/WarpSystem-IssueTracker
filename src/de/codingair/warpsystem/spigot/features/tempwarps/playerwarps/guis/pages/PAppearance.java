package de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Color;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.LoreButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.NameButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.PWEditor;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.utils.PlayerWarp;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PAppearance extends PageItem {
    private final PlayerWarp warp;
    private final String startName;

    public PAppearance(Player p, PlayerWarp warp) {
        super(p, PWEditor.getMainTitle(), new ItemBuilder(XMaterial.PAINTING).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Appearance")).getItem(), false);

        this.warp = warp;
        this.startName = this.warp.getName();
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ITEM_FRAME).setName("§6§n" + Lang.get("Item"));

                if(p.getInventory().getItem(p.getInventory().getHeldItemSlot()) == null || p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() == Material.AIR)
                    builder.addLore("§c" + Lang.get("No_Item_In_Hand"));
                else if(warp.getItem().getType() == p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType())
                    builder.addLore("§c" + Lang.get("Cant_Change_Item"));
                else
                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Item"));

                if(!warp.isStandardItem()) {
                    if(builder.getLore() == null || builder.getLore().size() <= 1) builder.addLore("");
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    if(p.getInventory().getItem(p.getInventory().getHeldItemSlot()) == null || p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() == Material.AIR
                            || warp.getItem().getType() == p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType()) return;

                    warp.changeItem(player.getInventory().getItem(player.getInventory().getHeldItemSlot()));
                    getLast().updateShowIcon();
                    update();
                    updateCosts();
                } else if(e.isRightClick()) {
                    warp.resetItem();
                    getLast().updateShowIcon();
                    update();
                    updateCosts();
                }
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(2, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                if(warp.getName() == null) return new ItemStack(Material.AIR);

                return new ItemBuilder(XMaterial.NAME_TAG)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Name"))
                        .setLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + "§7'§r" + org.bukkit.ChatColor.translateAlternateColorCodes('&', warp.getName()) + "§7'",
                                "", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Name"),
                                (warp.getName().equals(startName) ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Reset")))
                        .getItem();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                if(warp.getName() == null) return new ItemStack(Material.AIR);
                return new ItemBuilder(Material.PAPER).setName(warp.getName() == null ? Lang.get("Name") + "..." : warp.getName().replace("§", "&")).getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT && !warp.getName().equals(startName)) {
                    warp.setName(startName);
                    update();
                    updateCosts();
                }
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                    return;
                }

                if(!startName.equalsIgnoreCase(warp.getName()) && !warp.getName(false).equalsIgnoreCase(warp.getName()) && PlayerWarpManager.getInstance().exists(warp.getName())) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                    return;
                }

                warp.setName(e.getInput());
                getLast().updateShowIcon();
                updateCosts();
                update();
                e.setClose(true);
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
            }
        }.setOption(option));

        addButton(new LoreButton(3, 2, warp.getItem()) {
            @Override
            public void updatingLore(ItemBuilder toChange) {
                warp.setItem(toChange);
                getLast().updateShowIcon();
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(4, 2, ClickType.LEFT) {
            @Override
            public void onClick(AnvilClickEvent e) {
                String input = e.getInput();

                if(input == null || input.toCharArray().length < TempWarpManager.getManager().getMinMessageCharLength() || input.toCharArray().length > TempWarpManager.getManager().getMaxMessageCharLength()) {
                    p.sendMessage(Lang.getPrefix() +
                            Lang.get("Message_Too_Long_Too_Short")
                                    .replace("%MIN%", TempWarpManager.getManager().getMinMessageCharLength() + "")
                                    .replace("%MAX%", TempWarpManager.getManager().getMaxMessageCharLength() + "")
                    );
                    return;
                }

                warp.setTeleportMessage(input);
                updateCosts();
                e.setClose(true);
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
                updatePage();
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_EYE).setName("§6§n" + Lang.get("Teleport_Message"));

                List<String> msg = TextAlignment.lineBreak((warp.getTeleportMessage() == null ? "§c" + Lang.get("Not_Set") : "§7\"§f" + ChatColor.translateAlternateColorCodes('&', warp.getTeleportMessage()) + "§7\""), 100);

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + msg.remove(0));
                if(!msg.isEmpty()) builder.addLore(msg);

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change"));
                if(warp.getTeleportMessage() != null) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick")+": §c" + Lang.get("Remove"));

                return builder.getItem();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(XMaterial.PAPER)
                        .setName(warp.getTeleportMessage() == null ? (Lang.get("Message") + "...") : warp.getTeleportMessage())
                        .getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT && warp.getTeleportMessage() != null) {
                    warp.setTeleportMessage(null);
                    update();
                    updateCosts();
                }
            }
        }.setOption(option));
    }

    public void updateCosts() {
        getLast().initControllButtons();
    }
}
