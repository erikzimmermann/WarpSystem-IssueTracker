package de.codingair.warpsystem.spigot.base.guis.options;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.options.OptionBundle;
import org.bukkit.entity.Player;

public class OptionsGUI {
    private OptionBundle bundle;
    private OptionBundle clone;
    private GOptions options;

    public OptionsGUI(Player player) {
        this.bundle = WarpSystem.getInstance().getOptions();
        this.clone = this.bundle.clone();
        this.options = new GOptions(player, bundle, clone);
    }

    public void open() {
        this.options.open();
    }
}
