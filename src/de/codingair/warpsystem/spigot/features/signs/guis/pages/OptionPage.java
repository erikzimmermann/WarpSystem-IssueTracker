package de.codingair.warpsystem.spigot.features.signs.guis.pages;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SignGUIButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.signs.guis.WarpSignGUI;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class OptionPage extends PageItem {
    private WarpSign sign;

    public OptionPage(Player p, WarpSign sign) {
        super(p, WarpSignGUI.getMainTitle(), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName("§6§n" + Lang.get("Options")).getItem(), false);

        this.sign = sign;

        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        addButton(new SignGUIButton(19, new ItemBuilder(XMaterial.SIGN).setName("Change text").getItem(), sign.getLocation()) {
            @Override
            public void onClose(String[] lines) {
                System.out.println(Arrays.toString(lines));
            }
        });
    }
}
