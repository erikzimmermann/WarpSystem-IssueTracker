package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OwnWarpFilter implements Filter {
    @Override
    public Node<List<Button>, Boolean> getListItems(int maxSize, int page, Object... extra) {
        assert extra instanceof Player[] && extra.length == 1;
        Player p = (Player) extra[0];
        List<PlayerWarp> warps = PlayerWarpManager.getManager().getWarps(p);

        List<Button> buttons = new ArrayList<>();
        boolean hasNextPage = true;
        if(createButtonInList() && PlayerWarpManager.hasPermission(p)) maxSize--;

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        int max = (page + 1) * maxSize;
        int i;
        for(i = page * maxSize; i < max; i++) {
            if(warps.size() <= i) {
                hasNextPage = false;
                break;
            }

            PlayerWarp w = warps.get(i);
            SyncButton b = new SyncButton(0) {
                @Override
                public ItemStack craftItem() {
                    return w.getItem().clone().addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Teleport"),
                            Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §7" + Lang.get("Edit")).getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(e.isLeftClick()) w.perform(player);
                    else {
                        getInterface().changeGUI(new PWEditor(player, w).setUseFallbackGUI(true));
                    }
                }

                @Override
                public boolean canClick(ClickType click) {
                    return click == ClickType.LEFT || click == ClickType.RIGHT;
                }
            };

            b.setOption(option);

            buttons.add(b);
        }

        if(createButtonInList() && PlayerWarpManager.hasPermission(p)) buttons.add(PWPage.getCreateButton());

        return new Node<>(buttons, hasNextPage);
    }

    @Override
    public boolean createButtonInList() {
        return true;
    }

    @Override
    public boolean deleteExtraBeforeChangeFilter() {
        return false;
    }

    @Override
    public Object[] getStandardExtra(PWList list) {
        return new Object[] {list.getPlayer()};
    }
}
