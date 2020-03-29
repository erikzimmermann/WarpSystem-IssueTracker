package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.ImprovedDouble;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarps;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class OwnWarpFilter implements Filter {
    @Override
    public Node<List<Button>, Integer> getListItems(int maxSize, int page, Player player, String search, Object... extra) {
        List<PlayerWarp> warps = PlayerWarpManager.getManager().getOwnWarps(player);

        List<Button> buttons = new ArrayList<>();
        if(createButtonInList() && PlayerWarpManager.hasPermission(player)) maxSize--;

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        int max = (page + 1) * maxSize;
        int i, noMatch = 0;
        for(i = page * maxSize; i < max + noMatch; i++) {
            if(warps.size() <= i) break;
            PlayerWarp w = warps.get(i);

            if(search != null && !w.getName(false).toLowerCase().contains(search)) {
                noMatch++;
                continue;
            }

            SyncButton b = getOwnWarpIcon(w, search);

            b.setOption(option);

            buttons.add(b);
        }

        return new Node<>(buttons, warps.size() - noMatch);
    }

    @Override
    public boolean createButtonInList() {
        return true;
    }

    public static SyncButton getOwnWarpIcon(PlayerWarp w, String highlight) {
        return new SyncButton(0) {
            private BukkitRunnable runnable;

            private Number cut(Number n) {
                if(n.intValue() == n.doubleValue()) return n.intValue();
                return n.doubleValue();
            }

            @Override
            public ItemStack craftItem() {
                return w.getItem(highlight)
                        .addLore(w.getInactiveSales() == 0 ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Earned") + ": §7" + cut(w.getInactiveSales() * w.getTeleportCosts()) + " " + Lang.get("Coins"))
                        .addLore("§8§m                         ", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Teleport"),
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + Lang.get("Edit"),
                                "", w.getInactiveSales() == 0 ? null : Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Leftclick") + ": §a" + Lang.get("Draw_Money"),
                                Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Rightclick") + ": " + (runnable != null ? "§4" : "§7") + ChatColor.stripColor(Lang.get("Delete")) + (runnable != null ? " §7(§c" + ChatColor.stripColor(Lang.get("Confirm")) + "§7)" : ""))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    if(e.isShiftClick()) {
                        //draw money
                        double money = w.collectInactiveSales(player);
                        player.sendMessage(Lang.getPrefix() + Lang.get("Warp_Draw_Money_Info").replace("%NAME%", w.getName()).replace("%AMOUNT%", new ImprovedDouble(money).toString()));

                        update();
                    } else w.perform(player);
                } else if(e.isRightClick() && !e.isShiftClick()) {
                    GUI g = new PWEditor(player, w);
                    g.setOpenSound(null);
                    getInterface().changeGUI(g, true);
                } else {
                    if(runnable != null) {
                        //delete
                        double refund = PlayerWarpManager.getManager().delete(w, true);
                        if(refund == -1) return;

                        if(refund > 0 && PlayerWarpManager.getManager().isEconomy() && w.isOwner(player)) {
                            MoneyAdapterType.getActive().deposit(player, refund);
                            player.sendMessage(Lang.getPrefix() + Lang.get("Warp_Deleted_Info").replace("%NAME%", w.getName(true)).replace("%PRICE%", CPlayerWarps.cut(refund) + ""));
                        } else player.sendMessage(Lang.getPrefix() + Lang.get("Warp_was_deleted").replace("%NAME%", w.getName(true)));

                        if(!runnable.isCancelled()) runnable.cancel();
                        runnable = null;
                        getInterface().reinitialize();
                    } else {
                        runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                runnable = null;
                                update();
                            }
                        };
                        runnable.runTaskLater(WarpSystem.getInstance(), 20);

                        update();
                    }
                }
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT || (click == ClickType.SHIFT_LEFT && w.getInactiveSales() > 0);
            }
        };
    }

    @Override
    public boolean deleteExtraBeforeChangeFilter() {
        return false;
    }

    @Override
    public Object[] getStandardExtra(PWList list) {
        return null;
    }

    @Override
    public PWPage.FilterButton getControllButton(PWPage page, int warps) {
        return null;
    }

    @Override
    public boolean searchable(PWPage page) {
        return true;
    }
}
