package de.codingair.warpsystem.spigot.features.tempwarps.guis.keys;

import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.features.tempwarps.managers.TempWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.Key;
import org.bukkit.entity.Player;

public class TemplateGUI extends Editor<Key> {
    private Key clone;

    public TemplateGUI(Player p, Key original, Key clone) {
        super(p, clone, new Backup<Key>(original) {
            @Override
            public void applyTo(Key value) {
                original.setTime(value.getTime());
                original.setItem(value.getItem());

                TempWarpManager.getManager().addTemplate(original);
            }

            @Override
            public void cancel(Key value) {
            }
        }, clone::getItem, new POptions(p, clone));

        this.clone = clone;
    }

    @Override
    public Key getKey() {
        return clone;
    }
}
