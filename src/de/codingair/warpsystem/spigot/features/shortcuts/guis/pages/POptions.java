package de.codingair.warpsystem.spigot.features.shortcuts.guis.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.*;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.shortcuts.managers.ShortcutManager;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import org.bukkit.entity.Player;

public class POptions extends PageItem {
    private final Shortcut shortcut;

    public POptions(Player p, Shortcut shortcut) {
        super(p, Editor.TITLE_COLOR + Lang.get("Shortcuts"), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.shortcut = shortcut;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1F));

        addButton(new NameButton(1, 2, false, new Value<>(shortcut.getDisplayName())) {
            @Override
            public String acceptName(String name) {
                return null;
            }

            @Override
            public String onChange(String old, String name) {
                name = name.replace(" ", "_").toLowerCase();
                shortcut.setDisplayName(name);
                return name;
            }
        }.setOption(option));

        addButton(new CommandButton(2, 2, shortcut) {
            @Override
            public boolean isOkay(String command) {
                if(ShortcutManager.getInstance().hasCommandLoop(shortcut, command)) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_Editor_Loop"));
                    return false;
                }

                return true;
            }
        }.setOption(option));

        addButton(new PermissionButton(3, 2, shortcut).setOption(option));
        addButton(new CooldownButton(4, 2, shortcut).setOption(option));
        addButton(new CostsButton(5, 2, shortcut).setOption(option));
    }
}
