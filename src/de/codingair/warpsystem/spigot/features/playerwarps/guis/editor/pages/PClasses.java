package de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.editor.PWEditor;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.Category;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PClasses extends PageItem {
    private final PlayerWarp warp, original;

    public PClasses(Player p, PlayerWarp warp, PlayerWarp original) {
        super(p, PWEditor.getMainTitle(), null, false);

        this.warp = warp;
        this.original = original;
        initialize(p);
    }

    @Override
    public ItemStack getPageItem() {
        List<String> l = Lang.getStringList("PlayerWarp_Classes");
        List<String> modified = new ArrayList<>();
        for(String s : l) {
            modified.add(s.replace("%MIN%", PlayerWarpManager.getManager().getClassesMin() + "").replace("%MAX%", PlayerWarpManager.getManager().getClassesMax() + ""));
        }
        l.clear();

        ItemBuilder builder = new ItemBuilder(XMaterial.WRITABLE_BOOK).setHideStandardLore(true)
                .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Classes"))
                .setLore(getFreeMessage(warp, original, this))
                .addLore(PWEditor.getCostsMessage(Math.max(warp.getTrusted().size() - original.getTrusted().size(), 0) * PlayerWarpManager.getManager().getTrustedMemberCosts(), PClasses.this))
                .addLore(modified);

        if(!warp.getClasses().equals(original.getClasses())) {
            builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Shift_Rightclick") + ": §c" + Lang.get("Reset"));
        }

        return builder.getItem();
    }

    @Override
    public Button getPageButton() {
        return new Button(0, getPageItem()) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.getClick() == ClickType.SHIFT_RIGHT) {
                    warp.getClasses().clear();
                    warp.getClasses().addAll(original.getClasses());

                    getLast().updatePage();
                    updateIcon();
                }
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || (click == ClickType.SHIFT_RIGHT && !warp.getClasses().equals(original.getClasses()));
            }
        };
    }

    private void updateIcon() {
        getLast().updatePageItems();
    }

    private static String getFreeMessage(PlayerWarp warp, PlayerWarp original, PageItem page) {
        if(warp.getTrusted().size() - original.getTrusted().size() >= 0) return null;
        return PWEditor.getFreeMessage(-(warp.getTrusted().size() - original.getTrusted().size()) + " " + Lang.get("Trusted_members"), page);
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
        for(i = 0; i < PlayerWarpManager.getManager().getWarpClasses().size(); i++) {
            Category c = PlayerWarpManager.getManager().getWarpClasses().get(i);

            addButton(new SyncButton(slots[i]) {
                @Override
                public ItemStack craftItem() {
                    ItemBuilder builder = c.getBuilder().clone();

                    builder.addLore("");

                    if(warp.hasClass(c)) {
                        builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                        builder.setHideEnchantments(true);
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §c" + Lang.get("Remove") + "§8 (§7" + warp.getClasses().size() + "/" + PlayerWarpManager.getManager().getClassesMax() + "§8)");
                    } else {
                        builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Add") + "§8 (§7" + warp.getClasses().size() + "/" + PlayerWarpManager.getManager().getClassesMax() + "§8)");
                    }

                    return builder.getItem();
                }

                @Override
                public void onClick(InventoryClickEvent e, Player player) {
                    if(warp.hasClass(c)) warp.getClasses().remove(c);
                    else warp.getClasses().add(c);

                    getLast().updatePage();
                    updateIcon();
                    getLast().updateControllButtons();
                }

                @Override
                public boolean canClick(ClickType click) {
                    return click == ClickType.LEFT && (warp.hasClass(c) || warp.getClasses().size() + 1 <= PlayerWarpManager.getManager().getClassesMax());
                }
            }.setOption(option));
        }
    }
}
