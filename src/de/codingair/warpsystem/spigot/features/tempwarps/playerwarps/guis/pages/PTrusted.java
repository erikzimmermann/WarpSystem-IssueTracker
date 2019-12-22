package de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.pages;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.PWEditor;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.utils.PlayerWarp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PTrusted extends PageItem {
    private final PlayerWarp warp;

    public PTrusted(Player p, PlayerWarp warp) {
        super(p, PWEditor.getMainTitle(), new ItemBuilder(XMaterial.IRON_CHESTPLATE).setHideStandardLore(true).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Trusted_members")).getItem(), false);

        this.warp = warp;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        int[] slots = new int[] {19, 20, 21, 22, 23, 24, 15, 14, 13, 12, 11, 10};

        for(int slot : slots) {
            removeButton(slot);
        }

        int i;
        for(i = 0; i < warp.getTrusted().size(); i++) {
            PlayerWarp.User user = warp.getTrusted().get(i);

            addButton(new SyncButton(slots[i]) {
                @Override
                public ItemStack craftItem() {
                    ItemBuilder builder = new ItemBuilder(WarpSystem.getInstance().getHeadManager().getHead(user.getId()).buildProfile());
                    builder.setName("§b" + user.getName());

                    builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                    return builder.getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(e.isRightClick()) {
                        warp.getTrusted().remove(user);
                        getLast().updatePage();
                    }
                }
            }.setOption(option));
        }

        if(i < slots.length) {
            addButton(new SyncAnvilGUIButton(slots[i]) {
                @Override
                public void onClick(AnvilClickEvent e) {
                    String input = e.getInput();

                    if(input == null) {
                        e.getPlayer().sendMessage(Lang.get("Prefix") + Lang.get("Enter_Name"));
                        return;
                    }

                    Player other;
                    if((other = Bukkit.getPlayer(input)) == null) {
                        e.getPlayer().sendMessage(Lang.get("Prefix") + Lang.get("Player_is_not_online"));
                        return;
                    }

                    warp.getTrusted().add(new PlayerWarp.User(other));
                    getLast().updatePage();

                    e.setClose(true);
                }

                @Override
                public void onClose(AnvilCloseEvent e) {
                }

                @Override
                public ItemStack craftItem() {
                    return new ItemBuilder(Skull.PLUS).setName(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Add")).getItem();
                }

                @Override
                public ItemStack craftAnvilItem(ClickType trigger) {
                    return new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Name") + "...").getItem();
                }
            }.setOption(option).setOnlyLeftClick(true));
        }
    }

    public void updateCosts() {
        getLast().initControllButtons();
    }
}
