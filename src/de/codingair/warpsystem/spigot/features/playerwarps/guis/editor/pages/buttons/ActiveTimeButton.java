package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class ActiveTimeButton extends EditorButton {
    private int editing = 0;

    public ActiveTimeButton(int x, PlayerWarp warp, PlayerWarp original, boolean isEditing, PageItem page, Player player) {
        super(x, warp, original, isEditing, page, player);
    }

    @Override
    public boolean canClick(ClickType click) {
        if(editing > 0) return click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT;
        else
            return click == ClickType.LEFT || click == ClickType.RIGHT || (click == ClickType.SHIFT_RIGHT && isEditing && original.getLeftTime() > 500 && (original.getLeftTime() > PlayerWarpManager.getManager().getMinTime() || warp.getTime() > PlayerWarpManager.getManager().getMinTime()) && Math.abs(original.getLeftTime() - warp.getTime()) >= 1000);
    }

    @Override
    public ItemStack craftItem() {
        ItemBuilder builder = new ItemBuilder(XMaterial.CLOCK);

        builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Active_Time"));

        if(isEditing && original.getLeftTime() > 500) {
            long diff;
            if(warp.getLeftTime() <= 0) diff = -original.getLeftTime();
            else diff = warp.getTime() - original.getLeftTime();

            builder.addLore(PWEditor.getCostsMessage(diff / 60000D * PlayerWarpManager.getManager().getActiveTimeCosts(), page));

            if(editing == 0) {
                builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": §7" + PlayerWarpManager.convertInTimeFormat(original.getLeftTime(), 0, "", ""));
            } else {
                builder.addLore("§7   " + PlayerWarpManager.convertInTimeFormat(original.getLeftTime(), 0, "", ""));
            }

            builder.addLore((diff >= 0 ? "§2+ " : "§4- ") + PlayerWarpManager.convertInTimeFormat(Math.abs(diff), 0, "", ""));
            builder.addLore("§7= " + (editing == 0 ? "§e" : "§7") + PlayerWarpManager.convertInTimeFormat(warp.getTime(), editing, "§e", "§7"));
        } else {
            builder.addLore(PWEditor.getCostsMessage(warp.getTime() / 60000D * PlayerWarpManager.getManager().getActiveTimeCosts(), page));
            builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (editing == 0 ? "§e" : "§7") + PlayerWarpManager.convertInTimeFormat(isEditing ? warp.getTime() : warp.getLeftTime(), editing, "§e", "§7"));
        }

        builder.addLore("");

        if(editing > 0) {
            builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §c- §8(§7" + Lang.get("Shift") + ": §b←§8)");
            builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a+ §8(§7" + Lang.get("Shift") + ": §b→§8)");
        } else {
            builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Choose"));
            builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §a" + Lang.get("Change"));
            if(isEditing && original.getLeftTime() > 500 && (original.getLeftTime() > PlayerWarpManager.getManager().getMinTime() || warp.getTime() > PlayerWarpManager.getManager().getMinTime()) && Math.abs(original.getLeftTime() - warp.getTime()) >= 1000)
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
                    else if(ms > 500 && warp.getTime() + (60000L - ms) <= max)
                        warp.setTime(warp.getTime() + ((unit == TimeUnit.MINUTES ? 60000 : 0) - ms) + (unit != TimeUnit.MINUTES ? time : 0));
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
}
