package de.codingair.warpsystem.spigot.base.guis.editor;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;

public class StandardButtonOption extends ItemButtonOption {
    public StandardButtonOption() {
        setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1F));
    }
}
