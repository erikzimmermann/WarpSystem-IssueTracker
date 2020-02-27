package de.codingair.warpsystem.spigot.features.playerwarps.guis.pages;

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
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class POptions extends PageItem {
    private final PlayerWarp warp, original;
    private final boolean isEditing;

    public POptions(Player p, PlayerWarp warp, PlayerWarp original, boolean editing) {
        super(p, PWEditor.getMainTitle(), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.warp = warp;
        this.original = original;
        this.isEditing = editing;
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
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT;
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(warp.isPublic() ? XMaterial.BIRCH_DOOR : XMaterial.DARK_OAK_DOOR);

                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Status"));

                if(original.isPublic() && !warp.isPublic()) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Free") + ": §7" + Lang.get("Public"));
                else if(!original.isPublic() && warp.isPublic()) builder.addLore(PWEditor.getCostsMessage(PlayerWarpManager.getManager().getPublicCosts()));

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

                if(amount == null || amount < 0 || amount > PlayerWarpManager.getManager().getMaxTeleportCosts()) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Enter_Amount_between").replace("%X%", 0 + "").replace("%Y%", PlayerWarpManager.getManager().getMaxTeleportCosts() + ""));
                    return;
                }

                amount = ((double) (int) (amount * 100D)) / 100;

                warp.setTeleportCosts(amount);
                updateCosts();
                update();
                e.setClose(true);
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || (click == ClickType.RIGHT && (warp.getTeleportCosts() > 0 || warp.getTeleportCosts() != original.getTeleportCosts()));
            }

            @Override
            public void onClose(AnvilCloseEvent e) {
            }

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.GOLD_NUGGET);
                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Costs"));

                double costs = warp.getTeleportCosts() - original.getTeleportCosts();
                if(costs > 0) builder.addLore(PWEditor.getCostsMessage(costs * PlayerWarpManager.getManager().getTeleportCosts()));
                else if(costs < 0) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Free") + ": §7≤" + original.getTeleportCosts() + " " + Lang.get("Coins"));

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §e" + PWEditor.cut(warp.getTeleportCosts()) + " " + Lang.get("Coins"));
                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Change"));
                if(warp.getTeleportCosts() > 0) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));
                else if(warp.getTeleportCosts() != original.getTeleportCosts()) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Reset"));

                return builder.getItem();
            }

            @Override
            public ItemStack craftAnvilItem(ClickType trigger) {
                return new ItemBuilder(XMaterial.PAPER).setName(warp.getTeleportCosts() + "").getItem();
            }

            @Override
            public void onOtherClick(InventoryClickEvent e) {
                if(e.isRightClick()) {
                    if(warp.getTeleportCosts() > 0) warp.setTeleportCosts(0);
                    else if(warp.getTeleportCosts() != original.getTeleportCosts()) warp.setTeleportCosts(original.getTeleportCosts());

                    update();
                    updateCosts();
                }
            }
        }.setOption(option).setOnlyLeftClick(false));

        addButton(new SyncButton(3, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL);

                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Target_Position"));

                if(!original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue())) {
                    builder.addLore(PWEditor.getCostsMessage(PlayerWarpManager.getManager().getPositionChangeCosts()), "");
                }

                Destination d = (Destination) warp.getAction(Action.WARP).getValue();
                Location l = d.buildLocation();
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("World") + ": §7" + l.getWorld().getName());
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Position") + ": §7" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Direction") + ": §7" + cut(l.getYaw()) + ", " + cut(l.getPitch()));

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": "+(equalsLocation(l, p.getLocation()) ? "§7" : "§a") + Lang.get("Change"));

                if(!original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue())) {
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Reset"));
                }

                if(builder.getLore().get(builder.getLore().size() - 1).isEmpty()) builder.getLore().remove(builder.getLore().size() - 1);

                return builder.getItem();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && !equalsLocation(((Destination) warp.getAction(Action.WARP).getValue()).buildLocation(), p.getLocation())
                        || click == ClickType.RIGHT && !original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue());
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.getClick() == ClickType.LEFT && !equalsLocation(((Destination) warp.getAction(Action.WARP).getValue()).buildLocation(), p.getLocation())) {
                    Destination d = (Destination) warp.getAction(Action.WARP).getValue();
                    d.setId(new de.codingair.codingapi.tools.Location(player.getLocation()).toJSONString(2));

                    update();
                    updateCosts();
                } else if(e.getClick() == ClickType.RIGHT && !original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue())) {
                    Destination d = (Destination) warp.getAction(Action.WARP).getValue();
                    Destination old = (Destination) original.getAction(Action.WARP).getValue();
                    d.setId(old.getId());

                    update();
                    updateCosts();
                }
            }
        }.setOption(option).setOnlyLeftClick(false));

        addButton(new SyncButton(4, 2) {
            private int editing = 0;

            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK);

                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Active_Time"));

                if(isEditing && original.getLeftTime() > 500) {
                    long diff = warp.getTime() + original.getPassedTime() - original.getTime();
                    if(warp.getLeftTime() <= 0) diff = -original.getLeftTime();

                    builder.addLore(PWEditor.getCostsMessage(diff / 60000D * PlayerWarpManager.getManager().getActiveTimeCosts()));

                    if(editing == 0) {
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7" + PlayerWarpManager.convertInTimeFormat(original.getLeftTime(), 0, "", ""));
                    } else {
                        builder.addLore("§7   " + PlayerWarpManager.convertInTimeFormat(original.getLeftTime(), 0, "", ""));
                    }

                    builder.addLore((diff >= 0 ? "§2+ " : "§4- ") + PlayerWarpManager.convertInTimeFormat(Math.abs(diff), 0, "", ""));
                    builder.addLore("§7= " + (editing == 0 ? "§e" : "§7") + PlayerWarpManager.convertInTimeFormat(warp.getTime(), editing, "§e", "§7"));
                } else {
                    builder.addLore(PWEditor.getCostsMessage(warp.getTime() / 60000D * PlayerWarpManager.getManager().getActiveTimeCosts()));
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (editing == 0 ? "§e" : "§7") + PlayerWarpManager.convertInTimeFormat(isEditing ? warp.getTime() : warp.getLeftTime(), editing, "§e", "§7"));
                }

                builder.addLore("");

                if(editing > 0) {
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §c- §8(§7" + Lang.get("Shift") + ": §b←§8)");
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a+ §8(§7" + Lang.get("Shift") + ": §b→§8)");
                } else {
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Choose"));
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a" + Lang.get("Change"));
                }

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick() && (editing > 0 || e.isShiftClick())) {
                    if(e.isShiftClick()) {
                        editing--;
                        if(editing < 0) editing = getUnits().length;
                    } else if(editing > 0) {
                        TimeUnit unit = getUnits()[editing - 1];
                        long time = TimeUnit.MILLISECONDS.convert(1, unit);

                        if(warp.getLeftTime() > 0) {
                            long min = PlayerWarpManager.getManager().getMinTime();

                            if(warp.getTime() > min && warp.getTime() - time < min) warp.setTime(min);
                            else warp.setTime(warp.getTime() - time);

                            if(warp.getTime() < min) warp.setTime(PlayerWarpManager.getManager().getMaxTime());
                        }
                    }
                    update();
                } else if(e.isRightClick()) {
                    if(e.isShiftClick() || editing == 0) {
                        editing++;
                        if(editing - 1 == getUnits().length) editing = 0;
                    } else {
                        TimeUnit unit = getUnits()[editing - 1];
                        long time = TimeUnit.MILLISECONDS.convert(1, unit);

                        long max = PlayerWarpManager.getManager().getMaxTime();

                        if(warp.getTime() < max && warp.getTime() + time > max) warp.setTime(max);
                        else warp.setTime(warp.getTime() + time);

                        if(warp.getTime() > max) warp.setTime(PlayerWarpManager.getManager().getMinTime());
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

                            long time;
                            try {
                                time = PlayerWarpManager.convertFromTimeFormat(e.getInput());
                            } catch(Exception ex) {
                                time = -1;
                            }

                            long min = PlayerWarpManager.getManager().getMinTime();
                            long max = PlayerWarpManager.getManager().getMaxTime();

                            playSound(e.getClickType(), player);

                            if(time < min || time > max) {
                                player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Something_between")
                                        .replace("%X%", PlayerWarpManager.convertInTimeFormat(PlayerWarpManager.getManager().getMinTime(), 10, "", ""))
                                        .replace("%Y%", PlayerWarpManager.convertInTimeFormat(PlayerWarpManager.getManager().getMaxTime(), 10, "", ""))
                                );
                                return;
                            }

                            warp.setTime(time);

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
                    }, new ItemBuilder(XMaterial.PAPER).setName(PlayerWarpManager.convertInTimeFormat(warp.getTime(), 10, "", "")).getItem());
                }
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
