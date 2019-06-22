package de.codingair.warpsystem.spigot.features.effectportals.guis.editor.pages;

import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import org.bukkit.entity.Player;

public class DestinationPage extends de.codingair.warpsystem.spigot.base.guis.editor.pages.DestinationPage {
    public DestinationPage(Player player, String title, Destination destination) {
        super(player, title, destination);
    }

    @Override
    public void initialize(Player p) {
        super.initialize(p);

        for(int i = 1; i < 7; i++) {
            if(getButton(i, 2) == null) {

                break;
            }
        }
    }
}
