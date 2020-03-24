package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.GlobalLocationAdapter;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
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
        int slot = 1;

        addButton(new SyncButton(slot++, 2) {
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

                if(original.isPublic() && !warp.isPublic()) builder.addLore(PWEditor.getFreeMessage(Lang.get("Public"), POptions.this));
                else if(!original.isPublic() && warp.isPublic()) builder.addLore(PWEditor.getCostsMessage(PlayerWarpManager.getManager().getPublicCosts(), POptions.this));

                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " +
                        (warp.isPublic() ?
                                "§a" + Lang.get("Public") :
                                "§e" + Lang.get("Private")
                        ));

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": " + (warp.isPublic() == original.isPublic() ? "§a" + Lang.get("Toggle") : "§c" + Lang.get("Reset")));

                return builder.getItem();
            }
        }.setOption(option));

        if(PlayerWarpManager.getManager().isEconomy() && PlayerWarpManager.getManager().isCustomTeleportCosts())
            addButton(new SyncAnvilGUIButton(slot++, 2, ClickType.LEFT) {
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
                    if(costs > 0) builder.addLore(PWEditor.getCostsMessage(costs * PlayerWarpManager.getManager().getTeleportCosts(), POptions.this));
                    else if(costs < 0) builder.addLore(PWEditor.getFreeMessage("≤" + original.getTeleportCosts() + " " + Lang.get("Coins"), POptions.this));

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
            }.setOption(option));

        addButton(new SyncButton(slot++, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL);

                builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Target_Position"));

                if(PlayerWarpManager.getManager().isEconomy() && !original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue())) {
                    String costsMessage = PWEditor.getCostsMessage(PlayerWarpManager.getManager().getPositionChangeCosts(), POptions.this);
                    builder.addLore(costsMessage, costsMessage != null ? "" : null);
                }

                Destination d = (Destination) warp.getAction(Action.WARP).getValue();
                GlobalLocationAdapter a = (GlobalLocationAdapter) d.getAdapter();
                de.codingair.codingapi.tools.Location l = a.getLocation();
                if(a.getServer() != null) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Server") + ": §7" + a.getServer());
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("World") + ": §7" + l.getWorldName());
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Position") + ": §7" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ());
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Direction") + ": §7" + cut(l.getYaw()) + ", " + cut(l.getPitch()));

                String info;

                if(PlayerWarpManager.isProtected(p)) info = "§7" + Lang.get("Change") + " (§c" + Lang.get("Protected_Area") + "§7)";
                else if(equalsLocation(l, p.getLocation())) info = "§7" + Lang.get("Change");
                else info = "§a" + Lang.get("Change");

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": " + info);

                if(!original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue())) {
                    builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Reset"));
                }

                if(builder.getLore().get(builder.getLore().size() - 1).isEmpty()) builder.getLore().remove(builder.getLore().size() - 1);

                return builder.getItem();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && !PlayerWarpManager.isProtected(p) && !equalsLocation(((Destination) warp.getAction(Action.WARP).getValue()).buildLocation(), p.getLocation())
                        || click == ClickType.RIGHT && !original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue());
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.getClick() == ClickType.LEFT && !equalsLocation(((Destination) warp.getAction(Action.WARP).getValue()).buildLocation(), p.getLocation())) {
                    Destination d = (Destination) warp.getAction(Action.WARP).getValue();
                    GlobalLocationAdapter a = (GlobalLocationAdapter) d.getAdapter();
                    de.codingair.codingapi.tools.Location l = a.getLocation();
                    l.apply(player.getLocation());
                    a.setServer(WarpSystem.getInstance().getCurrentServer());

                    update();
                    updateCosts();
                } else if(e.getClick() == ClickType.RIGHT && !original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue())) {
                    Destination d = (Destination) warp.getAction(Action.WARP).getValue();
                    GlobalLocationAdapter a = (GlobalLocationAdapter) d.getAdapter();
                    de.codingair.codingapi.tools.Location l = (de.codingair.codingapi.tools.Location) d.buildLocation();
                    Destination old = (Destination) original.getAction(Action.WARP).getValue();
                    GlobalLocationAdapter aOld = (GlobalLocationAdapter) d.getAdapter();
                    de.codingair.codingapi.tools.Location lOld = (de.codingair.codingapi.tools.Location) old.buildLocation();
                    l.apply(lOld);
                    a.setServer(aOld.getServer());

                    update();
                    updateCosts();
                }
            }
        }.setOption(option));

        if(PlayerWarpManager.getManager().isEconomy())
            addButton(new SyncButton(slot++, 2) {
                private int editing = 0;

                @Override
                public boolean canClick(ClickType click) {
                    if(editing > 0) return click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT;
                    else return click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT;
                }

                @Override
                public ItemStack craftItem() {
                    ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK);

                    builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Active_Time"));

                    if(isEditing && original.getLeftTime() > 500) {
                        long diff;
                        if(warp.getLeftTime() <= 0) diff = -original.getLeftTime();
                        else diff = warp.getTime() - original.getLeftTime();

                        builder.addLore(PWEditor.getCostsMessage(diff / 60000D * PlayerWarpManager.getManager().getActiveTimeCosts(), POptions.this));

                        if(editing == 0) {
                            builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7" + PlayerWarpManager.convertInTimeFormat(original.getLeftTime(), 0, "", ""));
                        } else {
                            builder.addLore("§7   " + PlayerWarpManager.convertInTimeFormat(original.getLeftTime(), 0, "", ""));
                        }

                        builder.addLore((diff >= 0 ? "§2+ " : "§4- ") + PlayerWarpManager.convertInTimeFormat(Math.abs(diff), 0, "", ""));
                        builder.addLore("§7= " + (editing == 0 ? "§e" : "§7") + PlayerWarpManager.convertInTimeFormat(warp.getTime(), editing, "§e", "§7"));
                    } else {
                        builder.addLore(PWEditor.getCostsMessage(warp.getTime() / 60000D * PlayerWarpManager.getManager().getActiveTimeCosts(), POptions.this));
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (editing == 0 ? "§e" : "§7") + PlayerWarpManager.convertInTimeFormat(isEditing ? warp.getTime() : warp.getLeftTime(), editing, "§e", "§7"));
                    }

                    builder.addLore("");

                    if(editing > 0) {
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §c- §8(§7" + Lang.get("Shift") + ": §b←§8)");
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a+ §8(§7" + Lang.get("Shift") + ": §b→§8)");
                    } else {
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Choose"));
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a" + Lang.get("Change"));
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Rightclick") + ": §c" + Lang.get("Reset"));
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
                            long min = PlayerWarpManager.getManager().getMinTime();

                            long ms = warp.getTime() - (((long) (warp.getTime() / 60000D)) * 60000);

                            if(warp.getTime() == min) warp.setTime(PlayerWarpManager.getManager().getMaxTime());
                            else if(warp.getTime() > min && warp.getTime() - time < min) warp.setTime(min);
                            else if(ms > 500 && warp.getTime() - ms >= min) warp.setTime(warp.getTime() - ms - (unit != TimeUnit.MINUTES ? time : 0));
                            else warp.setTime(warp.getTime() - time);
                        }
                        update();
                    } else if(e.isRightClick()) {
                        if(editing > 0 || !e.isShiftClick()) {
                            if(e.isShiftClick() || editing == 0) {
                                editing++;
                                if(editing - 1 == getUnits().length) editing = 0;
                            } else {
                                TimeUnit unit = getUnits()[editing - 1];
                                long time = TimeUnit.MILLISECONDS.convert(1, unit);
                                long max = PlayerWarpManager.getManager().getMaxTime();

                                long ms = warp.getTime() - (((long) (warp.getTime() / 60000D)) * 60000);

                                if(warp.getTime() == max) warp.setTime(PlayerWarpManager.getManager().getMinTime());
                                else if(warp.getTime() < max && warp.getTime() + time > max) warp.setTime(max);
                                else if(ms > 500 && warp.getTime() + (60000L - ms) <= max) warp.setTime(warp.getTime() + ((unit == TimeUnit.MINUTES ? 60000 : 0) - ms) + (unit != TimeUnit.MINUTES ? time : 0));
                                else warp.setTime(warp.getTime() + time);
                            }
                        } else warp.setTime(Math.max(original.getLeftTime(), PlayerWarpManager.getManager().getMinTime()));

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
            }.setOption(option));
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
