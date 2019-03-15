package de.codingair.warpsystem.spigot.features.effectportals.guis;

import de.codingair.codingapi.player.gui.inventory.guis.ConfirmGUI;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.entity.Player;

public class PortalDeleteGUI extends ConfirmGUI {
    public PortalDeleteGUI(Player p, Callback<Boolean> callback) {
        super(p, "Portal", Lang.get("Apply_Delete_No"), Lang.get("Question_Delete_Portal"), Lang.get("Apply_Delete_Yes"), WarpSystem.getInstance(), new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean answer) {
                        callback.accept(!answer);
                    }
                });
    }
}
