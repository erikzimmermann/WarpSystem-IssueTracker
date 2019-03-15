package de.codingair.warpsystem.spigot.features.warps.guis;

import de.codingair.codingapi.player.gui.inventory.guis.ConfirmGUI;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.entity.Player;

public class IconDeleteGUI extends ConfirmGUI {
    public IconDeleteGUI(Player p, Callback<Boolean> callback, Runnable close) {
        super(p, Lang.get("Delete"), Lang.get("Apply_Delete_No"), Lang.get("Confirm_Delete"), Lang.get("Apply_Delete_Yes"), WarpSystem.getInstance(), new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean answer) {
                        callback.accept(!answer);
                    }
                }, close);
    }
}
