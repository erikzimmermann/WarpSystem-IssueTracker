package de.codingair.warpsystem.remastered.gui.guis;

import de.CodingAir.v1_6.CodingAPI.Player.GUI.Inventory.Interface.GUI;
import de.codingair.warpsystem.remastered.WarpSystem;
import de.codingair.warpsystem.remastered.gui.affiliations.Category;
import org.bukkit.entity.Player;

public class GConfig extends GUI {
    private Category category;

    public GConfig(Player p, Category category) {
        super(p, "§c§l§nWarps§r &7- &6Config", 9, WarpSystem.getInstance(), false);

        this.category = category;
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        
    }
}
