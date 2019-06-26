package de.codingair.warpsystem.spigot.features.tempwarps.guis.keys;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.LoreButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.Key;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class POptions extends PageItem {
    private Key clone;
    private boolean creating;
    
    public POptions(Player p, Key clone) {
        super(p, Editor.TITLE_COLOR + Lang.get("Key_Templates"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);
        
        this.clone = clone;
        this.creating = TempWarpManager.getManager().getTemplate(clone.getName()) == null;
        
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        if(creating) {
            addButton(new SyncAnvilGUIButton(1, 2, ClickType.LEFT) {
                @Override
                public ItemStack craftItem() {
                    return new ItemBuilder(XMaterial.NAME_TAG)
                            .setName("§6§n" + Lang.get("Name"))
                            .setLore("§3" + Lang.get("Current") + ": " + (clone.getName() == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + ChatColor.translateAlternateColorCodes('&', clone.getName()) + "§7'"),
                                    "", (clone.getName() == null ? "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set_Name") : "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Change_Name")))
                            .getItem();
                }

                @Override
                public ItemStack craftAnvilItem(ClickType trigger) {
                    return new ItemBuilder(Material.PAPER).setName(clone.getName() == null ? Lang.get("Name") + "..." : clone.getName().replace("§", "&")).getItem();
                }

                @Override
                public void onOtherClick(InventoryClickEvent e) {
                }

                @Override
                public void onClick(AnvilClickEvent e) {
                    if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                    String input = e.getInput();

                    if(input == null) {
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                        return;
                    }

                    if(TempWarpManager.getManager().getTemplate(ChatColor.translateAlternateColorCodes('&', input)) != null) {
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                        return;
                    }

                    e.setClose(true);
                    clone.setName(e.getInput());
                    getLast().updateShowIcon();
                    update();
                }

                @Override
                public void onClose(AnvilCloseEvent e) {
                }
            }.setOption(option).setOnlyLeftClick(false));
        }
        
        addButton(new SyncButton(creating ? 2 : 1, 2) {
            @Override
            public ItemStack craftItem() {
                String info = p.getInventory().getItem(p.getInventory().getHeldItemSlot()) == null || p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() == Material.AIR ?
                        "§c" + Lang.get("No_Item_In_Hand") :
                        clone.getItem().getType() == p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() ?
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
                            || clone.getItem().getType() == p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType()) return;

                    clone.changeItem(player.getInventory().getItem(player.getInventory().getHeldItemSlot()));
                    getLast().updateShowIcon();
                    update();
                }
            }
        }.setOption(option));
        
        addButton(new LoreButton(creating ? 3 : 2, 2, new ItemBuilder(clone.getItem())) {
            @Override
            public void updatingLore(ItemBuilder toChange) {
                clone.setItem(toChange.getItem());
                getLast().updateShowIcon();
            }
        });

        addButton(new SyncButton(creating ? 4 : 3, 2) {
            private int direction = 0;
            private long last = 0;
            private int increase = 1;

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(last == 0) last = new Date().getTime();

                if(e.isLeftClick()) {
                    if(e.isShiftClick()) {
                        if(clone.getTime() == TempWarpManager.getManager().getMinTime()) {
                            Sound.CLICK.playSound(p, 1, 0.7F);
                        } else {
                            clone.setTime(TempWarpManager.getManager().getMinTime());
                            Sound.CLICK.playSound(p);
                        }

                        increase = 1;
                    } else {
                        if(direction != 1) {
                            increase = 1;
                            direction = 1;
                        } else {
                            if(new Date().getTime() - last < 250L) increase += 2;
                            else increase = 1;

                            last = new Date().getTime();
                        }

                        clone.setTime(clone.getTime() - TempWarpManager.getManager().getConfig().getDurationSteps() * increase);
                        if(clone.getTime() < TempWarpManager.getManager().getMinTime()) {
                            clone.setTime(TempWarpManager.getManager().getMinTime());

                            Sound.CLICK.playSound(p, 1, 0.7F);
                            increase = 1;
                        } else Sound.CLICK.playSound(p);
                    }

                    updatePage();
                } else if(e.isRightClick()) {
                    if(e.isShiftClick()) {
                        if(clone.getTime() == TempWarpManager.getManager().getMaxTime()) {
                            Sound.CLICK.playSound(p, 1, 0.7F);
                        } else {
                            clone.setTime(TempWarpManager.getManager().getMaxTime());
                            Sound.CLICK.playSound(p);
                        }

                        increase = 1;
                    } else {
                        if(direction != 2) {
                            increase = 1;
                            direction = 2;
                        } else {
                            if(new Date().getTime() - last < 250L) increase += 2;
                            else increase = 1;

                            last = new Date().getTime();
                        }

                        clone.setTime(clone.getTime() + TempWarpManager.getManager().getConfig().getDurationSteps() * increase);
                        if(clone.getTime() > TempWarpManager.getManager().getMaxTime()) {
                            clone.setTime(TempWarpManager.getManager().getMaxTime());

                            Sound.CLICK.playSound(p, 1, 0.7F);
                            increase = 1;
                        } else Sound.CLICK.playSound(p);
                    }

                    updatePage();
                }
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK).setName("§7" + Lang.get("Active_Time") + ": " + (clone.getTime() <= 0 ? "§c" + Lang.get("Not_Set") : "§b" + TempWarpManager.getManager().convertInTimeFormat(clone.getTime(), TempWarpManager.getManager().getConfig().getUnit())
                        + (clone.getTime() == TempWarpManager.getManager().getMinTime() ? " §7(§c" + Lang.get("Minimum") + "§7)" : clone.getTime() == TempWarpManager.getManager().getMaxTime() ? " §7(§c" + Lang.get("Maximum") + "§7)" : "")));

                builder.addLore("");
                if(clone.getTime() > TempWarpManager.getManager().getMinTime()) builder.addLore("§3(" + Lang.get("Shift") + ") " + Lang.get("Leftclick") + ": §b" + Lang.get("Reduce"));
                if(clone.getTime() < TempWarpManager.getManager().getMaxTime()) builder.addLore("§3(" + Lang.get("Shift") + ") " + Lang.get("Rightclick") + ": §b" + Lang.get("Enlarge"));

                return builder.getItem();
            }
        }.setOption(option).setOnlyLeftClick(false).setClickSound(null));
    }

    @Override
    public TemplateGUI getLast() {
        return (TemplateGUI) super.getLast();
    }
}
