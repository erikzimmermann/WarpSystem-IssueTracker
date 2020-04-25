package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons;

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

public class TargetPositionButton extends EditorButton {
    public TargetPositionButton(int x, PlayerWarp warp, PlayerWarp original, boolean isEditing, PageItem page, Player player) {
        super(x, warp, original, isEditing, page, player);
    }

    @Override
    public ItemStack craftItem() {
        ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL);

        builder.setName(Editor.ITEM_TITLE_COLOR + Lang.get("Target_Position"));

        if(PlayerWarpManager.getManager().isEconomy() && !original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue())) {
            String costsMessage = PWEditor.getCostsMessage(PlayerWarpManager.getManager().getPositionChangeCosts(), page);
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

        if(PlayerWarpManager.isProtected(player)) info = "§7" + Lang.get("Change") + " (§c" + Lang.get("Protected_Area") + "§7)";
        else if(equalsLocation(l, player.getLocation())) info = "§7" + Lang.get("Change");
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
        return click == ClickType.LEFT && !PlayerWarpManager.isProtected(page.getLast().getPlayer()) && !equalsLocation(((Destination) warp.getAction(Action.WARP).getValue()).buildLocation(), player.getLocation())
                || click == ClickType.RIGHT && !original.getAction(WarpAction.class).getValue().equals(warp.getAction(WarpAction.class).getValue());
    }

    @Override
    public void onClick(InventoryClickEvent e, Player player) {
        if(e.getClick() == ClickType.LEFT && !equalsLocation(((Destination) warp.getAction(Action.WARP).getValue()).buildLocation(), player.getLocation())) {
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
}
