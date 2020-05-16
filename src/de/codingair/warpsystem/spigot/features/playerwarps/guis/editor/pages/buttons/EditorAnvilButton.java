package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages.buttons;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;

public abstract class EditorAnvilButton extends SyncAnvilGUIButton {
    protected final PlayerWarp warp, original;
    protected final boolean isEditing;
    protected final PageItem page;
    protected final Player player;

    public EditorAnvilButton(int x, PlayerWarp warp, PlayerWarp original, boolean isEditing, PageItem page, Player player) {
        super(x, 2);
        this.warp = warp;
        this.original = original;
        this.isEditing = isEditing;
        this.page = page;
        this.player = player;

        update(false);
    }

    @Override
    public void update(boolean updateGUI) {
        if(this.warp == null) return;
        super.update(updateGUI);
    }

    public void updateCosts() {
        page.getLast().initControllButtons();
    }
}
