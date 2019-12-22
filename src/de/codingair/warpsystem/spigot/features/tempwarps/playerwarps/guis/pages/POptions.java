package de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.pages;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.PWEditor;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.utils.PlayerWarp;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class POptions extends PageItem {
    private final PlayerWarp warp;

    public POptions(Player p, PlayerWarp warp) {
        super(p, PWEditor.getMainTitle(), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.warp = warp;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        addButton(new SyncButton(1, 2) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                warp.setPublic(!warp.isPublic());
                updateCosts();
                update();
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(warp.isPublic() ? XMaterial.BIRCH_DOOR : XMaterial.DARK_OAK_DOOR);

                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Status"));

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " +
                        (warp.isPublic() ?
                                "§a" + Lang.get("Public") :
                                "§e" + Lang.get("Private")
                        ));

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Toggle"));

                return builder.getItem();
            }
        }.setOption(option));

        addButton(new SyncAnvilGUIButton(2, 2, ClickType.LEFT) {
            @Override
            public void onClick(AnvilClickEvent e) {
                Double amount = null;

                try {
                    amount = Double.parseDouble(e.getInput().replace(",", "."));
                } catch(NumberFormatException ignored) {
                }

                if(amount == null || amount < 0 || amount > PlayerWarpManager.getInstance().getMaxTeleportCosts()) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Amount_between").replace("%X%", 0 + "").replace("%Y%", PlayerWarpManager.getInstance().getMaxTeleportCosts() + ""));
                    return;
                }

                warp.setTeleportCosts(amount);
                updateCosts();
                update();
                e.setClose(true);
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.GOLD_NUGGET);
                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Costs"));
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §e" + warp.getTeleportCosts() + " " + Lang.get("Coins"));
                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change"));
                if(warp.getTeleportCosts() > 0) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                return builder.getItem();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(XMaterial.PAPER).setName(warp.getTeleportCosts() + "").getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.isRightClick()) {
                    warp.setTeleportCosts(0);
                    update();
                }
            }
        }.setOption(option).setOnlyLeftClick(false));

        addButton(new SyncButton(3, 2) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                Destination d = (Destination) warp.getAction(Action.WARP).getValue();
                d.setId(new de.codingair.codingapi.tools.Location(player.getLocation()).toJSONString(2));
                update();
                updateCosts();
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL);

                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Target_Position"));

                Destination d = (Destination) warp.getAction(Action.WARP).getValue();
                Location l = d.getAdapter().buildLocation(d.getId());

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("World") + ": §7" + l.getWorld().getName());
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Position") + ": §7" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Direction") + ": §7" + cut(l.getYaw()) + ", " + cut(l.getPitch()));

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": " + (equalsLocation(l, p.getLocation()) ? "§7" : "§a") + Lang.get("Change"));

                return builder.getItem();
            }
        }.setOption(option));

        addButton(new SyncButton(4, 2) {
            private int editing = 0;

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK);

                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Active_Time"));

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7" + convertInTimeFormat(warp.getTime(), PlayerWarpManager.getInstance().getConfig().getUnit(), editing, "§e", "§7"));
                builder.addLore("");

                if(editing > 0) {
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §c- §8(§7" + Lang.get("Shift") + ": §b←§8)");
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a+ §8(§7" + Lang.get("Shift") + ": §b→§8)");
                } else {
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Choose") );
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a" + Lang.get("Change"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick() && editing > 0) {
                    if(e.isShiftClick()) {
                        editing--;
                        if(editing < 0) editing = getUnits().length;
                    } else if(editing > 0) {
                        TimeUnit unit = getUnits()[editing - 1];
                        long time = PlayerWarpManager.getInstance().getConfig().getUnit().convert(1, unit);
                        warp.setTime(warp.getTime() - time);
                        if(warp.getTime() < PlayerWarpManager.getInstance().getMinTime()) warp.setTime(PlayerWarpManager.getInstance().getMinTime());
                    }
                    update();
                } else if(e.isRightClick()) {
                    if(e.isShiftClick() || editing == 0) {
                        editing++;
                        if(editing - 1 == getUnits().length) editing = 0;
                    } else {
                        TimeUnit unit = getUnits()[editing - 1];
                        long time = PlayerWarpManager.getInstance().getConfig().getUnit().convert(1, unit);
                        warp.setTime(warp.getTime() + time);
                        if(warp.getTime() > PlayerWarpManager.getInstance().getMaxTime()) warp.setTime(PlayerWarpManager.getInstance().getMaxTime());
                    }
                    update();
                } else {
                    getInterface().setClosingByButton(true);
                    getInterface().setClosingForGUI(true);

                    AnvilGUI.openAnvil(getInterface().getPlugin(), player, new AnvilListener() {
                        @Override
                        public void onClick(AnvilClickEvent e) {
                            e.setCancelled(true);
                            e.setClose(false);

                            if(e.getSlot() != AnvilSlot.OUTPUT) return;

                            int d = 0, h = 0, m = 0;

                            String input = e.getInput().trim().toLowerCase();
                            
                            try {
                                if(input.contains("d")) {
                                    String[] a = input.split("d")[0].split(" ");
                                    d = Integer.parseInt(a[a.length - 1]);
                                }

                                if(input.contains("h")) {
                                    String[] a = input.split("h")[0].split(" ");
                                    h = Integer.parseInt(a[a.length - 1]);
                                }

                                if(input.contains("m")) {
                                    String[] a = input.split("m")[0].split(" ");
                                    m = Integer.parseInt(a[a.length - 1]);
                                }
                            } catch(Exception ex) {
                                ex.printStackTrace();
                                return;
                            }

                            TimeUnit unit = PlayerWarpManager.getInstance().getConfig().getUnit();
                            long time = unit.convert(d, TimeUnit.DAYS) + unit.convert(h, TimeUnit.HOURS) + unit.convert(m, TimeUnit.MINUTES);
                            warp.setTime(time);

                            if(warp.getTime() < PlayerWarpManager.getInstance().getMinTime()) warp.setTime(PlayerWarpManager.getInstance().getMinTime());
                            else if(warp.getTime() > PlayerWarpManager.getInstance().getMaxTime()) warp.setTime(PlayerWarpManager.getInstance().getMaxTime());

                            playSound(player);
                            update();
                            e.setClose(true);
                        }

                        @Override
                        public void onClose(AnvilCloseEvent e) {
                            if(e.getPost() == null) {
                                getInterface().reinitialize();
                                e.setPost(() -> getInterface().open());
                                getInterface().setClosingForGUI(false);
                            }
                        }
                    }, new ItemBuilder(XMaterial.PAPER).setName(convertInTimeFormat(warp.getTime(), PlayerWarpManager.getInstance().getConfig().getUnit(), 10, "§e", "§7")).getItem());
                }

            }

            private String convertInTimeFormat(long time, TimeUnit unit, int highlight, String highlighter, String reset) {
                long days = TimeUnit.DAYS.convert(time, unit);
                time -= unit.convert(days, TimeUnit.DAYS);
                long hours = TimeUnit.HOURS.convert(time, unit);
                time -= unit.convert(hours, TimeUnit.HOURS);
                long min = TimeUnit.MINUTES.convert(time, unit);

                StringBuilder builder = new StringBuilder();

                if(days > 0 || highlight > 0) {
                    if(!builder.toString().isEmpty()) builder.append(", ");
                    if(highlight == 1) builder.append(highlighter).append("»");
                    builder.append(days).append("d");
                    if(highlight == 1) builder.append(highlighter).append("«").append(reset);
                }

                if(hours > 0 || highlight > 0) {
                    if(!builder.toString().isEmpty()) builder.append(", ");
                    if(highlight == 2) builder.append(highlighter).append("»");
                    builder.append(hours).append("h");
                    if(highlight == 2) builder.append(highlighter).append("«").append(reset);
                }

                if(min > 0 || highlight > 0) {
                    if(!builder.toString().isEmpty()) builder.append(", ");
                    if(highlight == 3) builder.append(highlighter).append("»");
                    builder.append(min).append("m");
                    if(highlight == 3) builder.append(highlighter).append("«").append(reset);
                }

                return builder.toString();
            }

            private TimeUnit[] getUnits() {
                return new TimeUnit[] {TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES};
            }
        }.setOption(option).setOnlyLeftClick(false));
    }

    private boolean equalsLocation(Location loc0, Location loc1) {
        de.codingair.codingapi.tools.Location l = new de.codingair.codingapi.tools.Location(loc0);
        de.codingair.codingapi.tools.Location l1 = new de.codingair.codingapi.tools.Location(loc1);

        l.trim(1);
        l1.trim(1);

        return l.equals(l1);
    }

    private float cut(float f) {
        return ((float) (int) (f * 10F)) / 10F;
    }

    public void updateCosts() {
        getLast().initControllButtons();
    }
}
