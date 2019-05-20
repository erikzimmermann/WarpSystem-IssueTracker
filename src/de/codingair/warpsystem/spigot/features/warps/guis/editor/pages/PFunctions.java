package de.codingair.warpsystem.spigot.features.warps.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CommandButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CostsButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.PermissionButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.StatusButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.entity.Player;

public class PFunctions extends PageItem {
    private Icon icon;

    public PFunctions(Player p, Icon icon) {
        super(p, "§c§n" + Lang.get("Item_Editing"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName("§6§n" + Lang.get("Options")).getItem(), false);

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
    }
}
