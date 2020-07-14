package de.codingair.warpsystem.spigot.features.warps.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.*;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PFunctions extends PageItem {
    private final Icon icon;

    public PFunctions(Player p, Icon icon) {
        super(p, Editor.TITLE_COLOR + Lang.get("Item_Editing"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.icon = icon;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1F));

        int slot = 1;
        addButton(new StatusButton(slot++, 2, icon).setOption(option));
        addButton(new CommandButton(slot++, 2, icon).setOption(option));
        if(!icon.isPage()) addButton(new CooldownButton(slot++, 2, icon).setOption(option));
        addButton(new PermissionButton(slot++, 2, icon).setOption(option));
        if(!icon.isPage()) addButton(new CostsButton(slot++, 2, icon).setOption(option));
        addButton(new SyncButton(slot++, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.BOOK)
                        .setName("§6§n" + Lang.get("Message") + Lang.PREMIUM_LORE)
                        .addLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                Lang.PREMIUM_CHAT(player);
            }
        }.setOption(option));
    }
}
