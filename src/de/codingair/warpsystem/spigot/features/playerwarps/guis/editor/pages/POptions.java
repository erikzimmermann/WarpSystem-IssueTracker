package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons.ActiveTimeButton;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons.TargetPositionButton;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons.TeleportCostsButton;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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

        if(PlayerWarpManager.getManager().isAllowPublicWarps()) {
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
        }

        if(PlayerWarpManager.getManager().isEconomy() && PlayerWarpManager.getManager().isCustomTeleportCosts() && PlayerWarpManager.getManager().isAllowPublicWarps())
            addButton(new TeleportCostsButton(slot++, warp, original, isEditing, this, p).setOption(option));

        addButton(new TargetPositionButton(slot++, warp, original, isEditing, this, p).setOption(option));

        if(PlayerWarpManager.getManager().isEconomy())
            addButton(new ActiveTimeButton(slot++, warp, original, isEditing, this, p).setOption(option));
    }

    public void updateCosts() {
        getLast().initControllButtons();
    }
}
