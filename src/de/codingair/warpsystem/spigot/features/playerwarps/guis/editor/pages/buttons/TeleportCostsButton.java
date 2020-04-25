package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
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

public class TeleportCostsButton extends EditorAnvilButton {
    public TeleportCostsButton(int x, PlayerWarp warp, PlayerWarp original, boolean isEditing, PageItem page, Player player) {
        super(x, warp, original, isEditing, page, player);
    }

    @Override
    public void onClick(AnvilClickEvent e) {
        Double amount = null;

        try {
            amount = Double.parseDouble(e.getInput().replace(",", "."));
        } catch(NumberFormatException ignored) {
        }

        if(amount == null || amount < 0 || amount > PlayerWarpManager.getManager().getMaxTeleportCosts()) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Amount_between").replace("%X%", 0 + "").replace("%Y%", PlayerWarpManager.getManager().getMaxTeleportCosts() + ""));
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
        if(costs > 0) builder.addLore(PWEditor.getCostsMessage(costs * PlayerWarpManager.getManager().getTeleportCosts(), page));
        else if(costs < 0) builder.addLore(PWEditor.getFreeMessage("≤" + original.getTeleportCosts() + " " + Lang.get("Coins"), page));

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
}
