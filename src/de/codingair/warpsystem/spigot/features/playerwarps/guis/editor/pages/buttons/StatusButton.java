package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.POptions;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class StatusButton extends EditorButton {
    public StatusButton(int x, PlayerWarp warp, PlayerWarp original, boolean isEditing, PageItem page, Player player) {
        super(x, warp, original, isEditing, page, player);
    }

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

        if(original.isPublic() && !warp.isPublic()) builder.addLore(PWEditor.getFreeMessage(Lang.get("Public"), page));
        else if(!original.isPublic() && warp.isPublic()) builder.addLore(PWEditor.getCostsMessage(PlayerWarpManager.getManager().getPublicCosts(), page));

        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " +
                (warp.isPublic() ?
                        "§a" + Lang.get("Public") :
                        "§e" + Lang.get("Private")
                ));

        builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": " + (warp.isPublic() == original.isPublic() ? "§a" + Lang.get("Toggle") : "§c" + Lang.get("Reset")));

        return builder.getItem();
    }
}
