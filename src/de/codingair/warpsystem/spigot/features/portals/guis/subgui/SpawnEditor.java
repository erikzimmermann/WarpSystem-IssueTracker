package de.codingair.warpsystem.spigot.features.portals.guis.subgui;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.components.SyncItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnEditor extends HotbarGUI {
    private PortalEditor fallBack;
    private Portal clone;

    public SpawnEditor(Player player, PortalEditor fallBack, Portal clone) {
        super(player, WarpSystem.getInstance(), 2);

        this.fallBack = fallBack;
        this.clone = clone;

        initialize();
    }

    @Override
    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                close(false);

                fallBack.updatePage();
                fallBack.open();
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        }));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        setItem(2, new SyncItemComponent(new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK || clickType == ClickType.SHIFT_LEFT_CLICK) {
                    clone.setSpawn(new Location(player.getLocation()));
                    updateSingle(2);
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Set"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }) {
            @Override
            public ItemStack craftItem() {
                String pos = clone.getSpawn() == null ? "§c-" : "x=" + cut(clone.getSpawn().getX()) + ", y=" + cut(clone.getSpawn().getY()) + ", z=" + cut(clone.getSpawn().getZ());
                return new ItemBuilder(XMaterial.ENDER_EYE).setName("§7" + Lang.get("Position") + ": §e" + pos).getItem();
            }
        });
    }

    public static Number cut(double n) {
        double d = ((double) (int) (n * 100)) / 100;

        if(PlayerWarpManager.getManager().isNaturalNumbers()) d = Math.ceil(d);

        if(d == (int) d) return (int) d;
        else return d;
    }
}
