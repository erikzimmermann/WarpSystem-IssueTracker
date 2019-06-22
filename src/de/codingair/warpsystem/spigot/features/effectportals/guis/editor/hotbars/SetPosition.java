package de.codingair.warpsystem.spigot.features.effectportals.guis.editor.hotbars;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetPosition extends HotbarGUI {
    private Callback<Location> callback;
    private Location location;

    public SetPosition(Player player, Location location, Callback<Location> callback) {
        super(player, WarpSystem.getInstance(), 2);
        this.callback = callback;
        this.location = location;

        init(player);
    }

    private void init(Player p) {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setCloseOnClick(true), false);
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));
        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.ENDER_EYE).setName("§7" + Lang.get("Position") + " §8- §e" + locToString(this.location)).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(callback != null) callback.accept(player.getLocation());
                location = player.getLocation();
                updateDisplayName(ic, "§7" + Lang.get("Position") + " §8- §e" + locToString(location));
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(player);
            }
        }));
    }

    private String locToString(Location l) {
        if(l == null) return "§c" + Lang.get("Not_Set");
        return l.getWorld().getName() + " (" + round(l.getX()) + ", " + round(l.getY()) + ", " + round(l.getZ()) + ")";
    }

    private double round(double d) {
        return ((float) Math.round(d * 10)) / 10;
    }
}
