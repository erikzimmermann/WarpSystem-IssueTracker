package de.codingair.warpsystem.spigot.features.simplewarps.guis;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.GUIListener;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.*;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.NameButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GEditWarp extends SimpleGUI {
    public GEditWarp(Player p, SimpleWarp warp) {
        super(p, new GLayout(), new GPage(p, warp), WarpSystem.getInstance());

        addListener(new GUIListener() {
            @Override
            public void onInvClickEvent(InventoryClickEvent e) {
            }

            @Override
            public void onInvOpenEvent(InventoryOpenEvent e) {

            }

            @Override
            public void onInvCloseEvent(InventoryCloseEvent e) {
                if(!isClosingForGUI() && !isClosingByButton() && !((GPage) GEditWarp.this.getMain()).saved) p.sendMessage(Lang.getPrefix() + Lang.get("SimpleWarp_Cancel_Edit"));
            }

            @Override
            public void onInvDragEvent(InventoryDragEvent e) {

            }

            @Override
            public void onMoveToTopInventory(ItemStack item, int oldRawSlot, List<Integer> newRawSlots) {

            }

            @Override
            public void onCollectToCursor(ItemStack item, List<Integer> oldRawSlots, int newRawSlot) {

            }
        });
    }

    private static class GPage extends Page {
        private SimpleWarp warp;
        private SimpleWarp clone;
        private boolean saved = false;

        public GPage(Player p, SimpleWarp warp) {
            super(p, Lang.get("SimpleWarp_Edit_Title").replace("%WARP%", ChatColor.translateAlternateColorCodes('&', warp.getName())), false);
            this.warp = warp;
            this.clone = warp.clone();
            initialize(p);
        }

        @Override
        public void initialize(Player p) {
            ItemButtonOption option = new StandardButtonOption();

            addButton(new NameButton(2, 0, false, new Value<>(clone.getName())) {
                @Override
                public String acceptName(String name) {
                    if(!name.equalsIgnoreCase(warp.getName()) && !SimpleWarpManager.getInstance().reserveName(name)) {
                        return Lang.getPrefix() + Lang.get("Name_Already_Exists");
                    }
                    return null;
                }

                @Override
                public String onChange(String old, String name) {
                    clone.setName(name);
                    clone.setLastChange(new Date());
                    clone.setLastChanger(p.getName());
                    return name;
                }
            }.setOption(option));

            addButton(new SyncAnvilGUIButton(4, 0, ClickType.LEFT) {
                @Override
                public void onClick(AnvilClickEvent e) {
                    if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                    String input = e.getInput(false);

                    if(input == null) {
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Permission"));
                        return;
                    }

                    e.setClose(true);
                    clone.setPermission(e.getInput());
                    update();
                }

                @Override
                public void onClose(AnvilCloseEvent e) {
                }

                @Override
                public ItemStack craftItem() {
                    String permission = clone.getPermission();

                    boolean perm = SimpleWarpManager.getInstance().isOverwritePermissions();

                    List<String> lore = new ArrayList<>();
                    if(permission != null && !perm) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                    return new ItemBuilder(XMaterial.ENDER_EYE)
                            .setName("§6§n" + Lang.get("Permission"))
                            .setLore(perm ? null : "§8» " + Lang.get("Permission_Notice"))
                            .addLore(perm ? "§7" + Lang.get("Permission_overwritten_by_config") : null)
                            .addLore("§3" + Lang.get("Current") + ": " + (permission == null ? "§c" + Lang.get("Not_Set") : "§7'§r" + permission + "§7'"))
                            .addLore(perm ? null : "", perm ? null : "§3" + Lang.get("Leftclick") + ": §a" + (permission == null ? Lang.get("Set") : Lang.get("Change")))
                            .addLore(lore)
                            .getItem();
                }

                @Override
                public ItemStack craftAnvilItem(ClickType trigger) {
                    return new ItemBuilder(XMaterial.PAPER).setName(clone.getPermission() == null ? Lang.get("Permission") + "..." : clone.getPermission()).getItem();
                }

                @Override
                public void onOtherClick(InventoryClickEvent e) {
                    if(e.getClick() == ClickType.RIGHT) {
                        clone.setPermission(null);
                        update();
                    }
                }

                @Override
                public boolean canClick(ClickType click) {
                    return !SimpleWarpManager.getInstance().isOverwritePermissions() && (click == ClickType.LEFT || (click == ClickType.RIGHT && clone.getPermission() != null));
                }
            }.setOption(option));

            addButton(new SyncAnvilGUIButton(6, ClickType.LEFT) {
                @Override
                public ItemStack craftItem() {
                    double costs = clone.getCosts();
                    String costsPrint = costs + "";
                    if(costsPrint.endsWith(".0")) costsPrint = costsPrint.substring(0, costsPrint.length() - 2);

                    List<String> lore = new ArrayList<>();
                    if(costs != 0) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                    return new ItemBuilder(XMaterial.GOLD_NUGGET)
                            .setName("§6§n" + Lang.get("Costs"))
                            .setLore("§3" + Lang.get("Current") + ": " + (costs == 0 ? "§c" + Lang.get("Not_Set") : "§7" + costsPrint + " " + Lang.get("Coins")))
                            .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + (costs == 0 ? Lang.get("Set") : Lang.get("Change")))
                            .addLore(lore)
                            .getItem();
                }

                @Override
                public void onClick(AnvilClickEvent e) {
                    if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

                    String input = e.getInput(false);

                    if(input == null) {
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                        return;
                    }

                    double costs;
                    try {
                        costs = Double.parseDouble(input);
                    } catch(NumberFormatException ex) {
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                        return;
                    }

                    if(costs < 0) {
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
                        return;
                    }

                    e.setClose(true);
                    clone.setCosts(costs);

                    update();
                }

                @Override
                public void onClose(AnvilCloseEvent e) {

                }

                @Override
                public ItemStack craftAnvilItem(ClickType trigger) {
                    double costs = clone.getCosts();
                    String costsPrint = costs + "";
                    return new ItemBuilder(XMaterial.PAPER).setName(costsPrint).getItem();
                }

                @Override
                public void onOtherClick(InventoryClickEvent e) {
                    if(e.getClick() == ClickType.RIGHT) {
                        clone.setCosts(0);
                        update();
                    }
                }

                @Override
                public boolean canClick(ClickType click) {
                    return click == ClickType.LEFT || (click == ClickType.RIGHT && clone.getCosts() > 0);
                }
            }.setOption(option));

            addButton(new Button(0, new ItemBuilder(XMaterial.RED_TERRACOTTA).setName("§8» §c" + Lang.get("Cancel")).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("SimpleWarp_Cancel_Edit"));
                }
            }.setOption(option).setCloseOnClick(true));

            addButton(new Button(8, new ItemBuilder(XMaterial.LIME_TERRACOTTA).setName("§8» §a" + Lang.get("Save")).getItem()) {
                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    saved = true;
                    SimpleWarpManager.getInstance().commitNewName(warp, clone.getName());
                    warp.apply(clone);
                    p.sendMessage(Lang.getPrefix() + Lang.get("SimpleWarp_Save_Edit"));
                    p.closeInventory();
                }
            }.setOption(option));

        }
    }

    private static class GLayout extends Layout {
        public GLayout() {
            super(9);
        }

        @Override
        public void initialize() {
            ItemBuilder blackPane = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true);

            setItem(1, blackPane.getItem());
            setItem(7, blackPane.getItem());
        }
    }
}
