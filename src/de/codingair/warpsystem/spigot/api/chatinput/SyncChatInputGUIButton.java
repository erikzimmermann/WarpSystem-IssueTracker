package de.codingair.warpsystem.spigot.api.chatinput;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncTriggerButton;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class SyncChatInputGUIButton extends SyncTriggerButton {
    private boolean onlyOutputTrigger = true;
    private ChatInputGUI gui;

    public SyncChatInputGUIButton(int slot) {
        super(slot);
    }

    public SyncChatInputGUIButton(int x, int y) {
        this(x + 9 * y);
    }

    public SyncChatInputGUIButton(int slot, ClickType... trigger) {
        super(slot, trigger);
    }

    public SyncChatInputGUIButton(int x, int y, ClickType... trigger) {
        this(x + 9 * y, trigger);
    }

    @Override
    public void onTrigger(InventoryClickEvent e, ClickType trigger, Player player) {
        if(!canTrigger(e, trigger, player)) return;

        getInterface().setClosingByButton(true);
        getInterface().setClosingForGUI(true);
        getInterface().close();

        player.closeInventory();
        this.gui = new ChatInputGUI(player, WarpSystem.getInstance()) {
            @Override
            public void onEnter(ChatInputEvent e) {
                SyncChatInputGUIButton.this.onEnter(e);

                getInterface().reinitialize();
                e.setPost(() -> getInterface().open());
                getInterface().setClosingForGUI(false);
            }

            @Override
            public void onClose() {
                getInterface().open();
                getInterface().setClosingForGUI(false);
            }
        };

        this.gui.open();
    }

    public boolean canTrigger(InventoryClickEvent e, ClickType trigger, Player player) {
        return true;
    }

    @Override
    public void update(boolean updateGUI) {
        super.update(updateGUI);
    }

    public boolean interrupt() {
        return false;
    }

    public abstract void onEnter(ChatInputEvent e);

    public abstract ItemStack craftItem();

    public boolean isOnlyOutputTrigger() {
        return onlyOutputTrigger;
    }

    public void setOnlyOutputTrigger(boolean onlyOutputTrigger) {
        this.onlyOutputTrigger = onlyOutputTrigger;
    }

    public ChatInputGUI getChatInputGUI() {
        return gui;
    }
}
