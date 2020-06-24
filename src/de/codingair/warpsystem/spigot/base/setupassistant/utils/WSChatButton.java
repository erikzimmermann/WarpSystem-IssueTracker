package de.codingair.warpsystem.spigot.base.setupassistant.utils;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;

import java.util.List;

public abstract class WSChatButton extends ChatButton {
    public WSChatButton(String text) {
        super(text);
        setSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1F));
    }

    public WSChatButton(String text, String hover) {
        super(text, hover);
        setSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1F));
    }

    public WSChatButton(String text, List<String> hover) {
        super(text, hover);
        setSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.7F, 1F));
    }
}
