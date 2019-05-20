package de.codingair.warpsystem.spigot.features.shortcuts.guis.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CommandButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.NameButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.PermissionButton;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import org.bukkit.entity.Player;

public class POptions extends PageItem {
    private Shortcut shortcut;

    public POptions(Player p, Shortcut shortcut) {
        super(p, Editor.TITLE_COLOR + Lang.get("Shortcuts"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.shortcut = shortcut;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new NameButton(1, 2, false, new Value<>(shortcut.getDisplayName())) {
            @Override
            public String acceptName(String name) {
                return null;
            }

            @Override
            public void onChange(String old, String name) {
                shortcut.setDisplayName(name);
            }
        }.setOption(option));

        addButton(new CommandButton(2, 2, shortcut).setOption(option));
        addButton(new PermissionButton(3, 2, shortcut).setOption(option));
    }
}
