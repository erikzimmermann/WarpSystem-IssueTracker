package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types;

import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import org.bukkit.entity.Player;

public class TeleportSoundAction extends ActionObject<SoundData> {
    public TeleportSoundAction(SoundData value) {
        super(Action.TELEPORT_SOUND, value);
    }

    public TeleportSoundAction() {
        this(null);
    }

    @Override
    public boolean perform(Player player) {
        getValue().play(player);
        return true;
    }

    @Override
    public ActionObject<SoundData> clone() {
        return new TeleportSoundAction(new SoundData(getValue().getSound(), getValue().getVolume(), getValue().getPitch()));
    }

    @Override
    public boolean usable() {
        return getValue() != null && getValue().getSound() != null;
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        setValue(new SoundData(Sound.valueOf(d.getString("sound", "ENDERMAN_TELEPORT")), d.getFloat("volume"), d.getFloat("pitch")));
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("sound", getValue() == null ? null : getValue().getSound() == null ? null : getValue().getSound().name());
        d.put("volume", getValue() == null ? null : getValue().getVolume());
        d.put("pitch", getValue() == null ? null : getValue().getPitch());
    }
}
