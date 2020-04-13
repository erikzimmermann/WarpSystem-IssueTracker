package de.codingair.warpsystem.spigot.features.warps.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
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

public class PFunctions extends PageItem {
    private Icon icon;

    public PFunctions(Player p, Icon icon) {
        super(p, Editor.TITLE_COLOR + Lang.get("Item_Editing"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.icon = icon;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new StatusButton(1, 2, icon).setOption(option));
        addButton(new CommandButton(2, 2, icon).setOption(option));
        addButton(new PermissionButton(3, 2, icon).setOption(option));
        addButton(new CostsButton(4, 2, icon).setOption(option));
        addButton(new MessageButton(5, 2, icon).setOption(option));
    }
}
