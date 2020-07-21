package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
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
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons.StatusButton;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons.TargetPositionButton;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons.TeleportCostsButton;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
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
        option.setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1F));
        int slot = 1;

        if(PlayerWarpManager.getManager().isAllowPublicWarps()) addButton(new StatusButton(slot++, warp, original, isEditing, this, p).setOption(option));

        if(PlayerWarpManager.getManager().isEconomy() && PlayerWarpManager.getManager().isCustomTeleportCosts() && PlayerWarpManager.getManager().isAllowPublicWarps())
            addButton(new TeleportCostsButton(slot++, warp, original, isEditing, this, p).setOption(option));

        addButton(new TargetPositionButton(slot++, warp, original, isEditing, this, p).setOption(option));

        if(PlayerWarpManager.getManager().isEconomy() && warp.isTimeDependent())
            addButton(new ActiveTimeButton(slot++, warp, original, isEditing, this, p).setOption(option));
    }

    public static int count(PlayerWarp warp) {
        return (PlayerWarpManager.getManager().isAllowPublicWarps() ? 1 : 0)
                + (PlayerWarpManager.getManager().isEconomy() && PlayerWarpManager.getManager().isCustomTeleportCosts() && PlayerWarpManager.getManager().isAllowPublicWarps() ? 1 : 0)
                + 1
                + (PlayerWarpManager.getManager().isEconomy() && warp.isTimeDependent() ? 1 : 0);
    }
}
