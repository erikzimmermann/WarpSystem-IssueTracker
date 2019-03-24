package de.codingair.warpsystem.spigot.features.warps.guis.editor.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PAppearance extends PMain {
    public PAppearance(Player player, PMain main) {
        super(player, main);
    }

    public PAppearance(Player p, ItemStack item, String name, int slot, Icon category, boolean isCategory) {
        super(p, item, name, slot, category, isCategory, true);
        super.setAppearance(this);
        super.initialize(p);
        initialize(p);
    }

    public PAppearance(Player p, Icon icon) {
        super(p, icon, true);
        super.setAppearance(this);
        super.initialize(p);
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        super.initialize(p);

        Button aB = getButton(1);
        Button fB = getButton(2);
        Button dB = getButton(3);

        aB.setItem(new ItemBuilder(aB.getItem()).addEnchantment(Enchantment.DAMAGE_ALL, 1).setHideEnchantments(true).getItem());
        fB.setItem(new ItemBuilder(fB.getItem()).removeEnchantments().getItem());
        dB.setItem(new ItemBuilder(dB.getItem()).removeEnchantments().getItem());

        if(aB.getLink() != null) aB.setLink(null);
        if(fB.getLink() == null) fB.setLink(getFunctions());
        if(dB.getLink() == null) dB.setLink(getDestination());

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                String info = p.getInventory().getItem(p.getInventory().getHeldItemSlot()) == null || p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() == Material.AIR ?
                        "§c" + Lang.get("No_Item_In_Hand") :
                        getIcon().getItem().getType() == p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() ?
                                "§c" + Lang.get("Cant_Change_Item")
                                : "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Item");


                return new ItemBuilder(XMaterial.ITEM_FRAME)
                        .setName("§6§n" + Lang.get("Item"))
                        .setLore("", info)
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    if(p.getInventory().getItem(p.getInventory().getHeldItemSlot()) == null || p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() == Material.AIR
                            || getIcon().getItem().getType() == p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType()) return;

                    getIcon().changeItem(player.getInventory().getItem(player.getInventory().getHeldItemSlot()));
                    getShowIcon().update();
                    update();
                }
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(2, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.NAME_TAG)
                        .setName("§6§n" + Lang.get("Name"))
                        .setLore("§3" + Lang.get("Current") + ": " + (getIcon().getName() == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', getIcon().getName()) + "§7'"),
                                "", (getIcon().getName() == null ? "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set_Name") : "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Name_Short")),
                                (getIcon().getName() == null ? null : "§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove")))
                        .getItem();
            }

            @Override
            public ItemStack craftAnvilItem() {
                return new ItemBuilder(Material.PAPER).setName(getIcon().getName() == null ? Lang.get("Name") + "..." : getIcon().getName().replace("§", "&")).getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    getIcon().setName(null);
                    getShowIcon().update();
                    update();
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

                if(getBackup() != null && getBackup().getName().equals(input)) {
                    e.setClose(true);
                    getIcon().setName(e.getInput());
                    getShowIcon().update();
                    update();
                    return;
                }

                if(getIcon().isCategory()) {
                    if(IconManager.getInstance().existsCategory(input)) {
                        p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                        return;
                    }
                } else {
                    if(IconManager.getInstance().existsIcon(input)) {
                        p.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                        return;
                    }
                }

                e.setClose(true);
                getIcon().setName(e.getInput());
                getShowIcon().update();
                update();
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
            }
        }.setOption(option).setOnlyLeftClick(false));

        addButton(new SyncAnvilGUIButton(3, 2, ClickType.LEFT) {
            @Override
            public ItemStack craftItem() {
                List<String> loreOfItem = getIcon().getItemBuilder().getLore();
                List<String> lore = new ArrayList<>();
                if(loreOfItem == null) lore = null;
                else {
                    for(String s : loreOfItem) {
                        lore.add("§7- '§r" + s + "§7'");
                    }
                }

                List<String> lore2 = new ArrayList<>();
                if(lore != null && !lore.isEmpty()) lore2.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Reset_Lines"));

                return new ItemBuilder(XMaterial.PAPER)
                        .setName("§6§n" + Lang.get("Description"))
                        .setLore("§3" + Lang.get("Current") + ": " + (lore == null || lore.isEmpty() ? "§c" + Lang.get("Not_Set") : ""))
                        .addLore(lore)
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add_Line"))
                        .addLore(lore2)
                        .getItem();
            }

            @Override
            public ItemStack craftAnvilItem() {
                return new ItemBuilder(Material.PAPER).setName(Lang.get("Line") + "...").getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.getClick() == ClickType.RIGHT) {
                    getIcon().setItem(getIcon().getItemBuilder().removeLore().getItem());
                    getShowIcon().update();
                    update();
                }
            }

            @Override
            public void onClick(AnvilClickEvent e) {
                if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                String input = e.getInput();

                if(input == null) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Lore"));
                    return;
                }

                e.setClose(true);
                playSound(p);

                getIcon().setItem(getIcon().getItemBuilder().addLore(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', input)).getItem());
                getShowIcon().update();
                update();
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
            }
        }.setOption(option).setOnlyLeftClick(false));

        addButton(new SyncButton(4, 2) {
            @Override
            public ItemStack craftItem() {
                ItemStack sparkle;
                if(getIcon().getItemBuilder().getEnchantments() == null || getIcon().getItemBuilder().getEnchantments().size() == 0) {
                    sparkle = new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle"))
                            .setLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Enable"))
                            .getItem();
                } else {
                    sparkle = new ItemBuilder(Material.BLAZE_POWDER).setName("§6§n" + Lang.get("Sparkle"))
                            .setLore("", "§3" + Lang.get("Leftclick") + ": §c" + Lang.get("Disable"))
                            .getItem();
                }

                return sparkle;
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(getIcon().getItemBuilder().getEnchantments() == null || getIcon().getItemBuilder().getEnchantments().size() == 0) {
                    getIcon().setItem(getIcon().getItemBuilder().setHideStandardLore(true).addEnchantment(Enchantment.DAMAGE_ALL, 1).setHideEnchantments(true).getItem());
                } else {
                    getIcon().setItem(getIcon().getItemBuilder().setHideStandardLore(true).removeEnchantments().getItem());
                }

                getShowIcon().update();
                update();
            }
        }.setOption(option));
    }
}
