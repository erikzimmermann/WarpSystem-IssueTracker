package de.codingair.warpsystem.teleport.portals;

import de.codingair.codingapi.player.gui.inventory.guis.ConfirmGUI;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.WarpSystem;
import de.codingair.warpsystem.language.Example;
import de.codingair.warpsystem.language.Lang;
import org.bukkit.entity.Player;

public class PortalDeleteGUI extends ConfirmGUI {
    public PortalDeleteGUI(Player p, Callback<Boolean> callback) {
        super(p, "Portal", Lang.get("Apply_Delete_No", new Example("ENG", "&7No, &akeep it&7."), new Example("GER", "&7Nein, &abehalten&7.")),
                Lang.get("Question_Delete_Portal", new Example("ENG", "&7Do you really want to &cdelete &7this portal?"), new Example("GER", "&7Willst du dieses Portal wirklich &clöschen&7?")),                Lang.get("Apply_Delete_Yes", new Example("ENG", "&7Yes,&4delete&7."), new Example("GER", "&7Ja, &4löschen&7.")), WarpSystem.getInstance(), new Callback<Boolean>() {
                    @Override
                    public void accept(Boolean answer) {
                        callback.accept(!answer);
                    }
                });
    }
}
