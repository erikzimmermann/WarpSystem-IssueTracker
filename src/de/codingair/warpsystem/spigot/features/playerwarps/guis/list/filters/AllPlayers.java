package de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.managers.HeadManager;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWPage;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AllPlayers implements Filter {
    @Override
    public Node<List<Button>, Boolean> getListItems(int maxSize, int page, Object... extra) {
        UUID special = null;
        if(extra != null && extra.length == 1 && extra[0] instanceof UUID) special = (UUID) extra[0];

        List<UUID> uuids = special == null ? new ArrayList<>(PlayerWarpManager.getManager().getUUIDs()) : null;
        List<PlayerWarp> publicWarps = special != null ? PlayerWarpManager.getManager().getWarps(special) : null;

        List<Button> buttons = new ArrayList<>();
        boolean hasNextPage = true;

        int max = (page + 1) * maxSize;
        int i;
        for(i = page * maxSize; i < max; i++) {
            if(uuids != null) {
                if(uuids.size() <= i) {
                    hasNextPage = false;
                    break;
                }

                UUID id = uuids.get(i);
                PlayerWarp.User user = PlayerWarpManager.getManager().getWarps(id).get(0).getOwner();
                SyncButton b = new SyncButton(0) {
                    @Override
                    public ItemStack craftItem() {
                        return new ItemBuilder(WarpSystem.getInstance().getHeadManager().getHead(id).buildProfile()).setName("§b" + user.getName()).getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        //list PlayerWarps of selected player
                        ((PWList) getInterface()).getMain().setExtra(true, id);
                    }
                };

                buttons.add(b);
            } else if(publicWarps != null) {
                if(publicWarps.size() <= i) {
                    hasNextPage = false;
                    break;
                }

                PlayerWarp w = publicWarps.get(i);
                SyncButton b = new SyncButton(0) {
                    @Override
                    public ItemStack craftItem() {
                        return w.getItem().getItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent e, Player player) {
                        w.perform(player);
                    }
                };

                buttons.add(b);
            }
        }

        if(uuids != null) uuids.clear();
        return new Node<>(buttons, hasNextPage);
    }

    @Override
    public boolean createButtonInList() {
        return false;
    }

    @Override
    public boolean deleteExtraBeforeChangeFilter() {
        return true;
    }

    @Override
    public Object[] getStandardExtra(PWList list) {
        return null;
    }
}
