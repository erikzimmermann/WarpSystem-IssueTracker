package de.codingair.warpsystem.spigot.features.warps.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CommandButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.CostsButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.PermissionButton;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.StatusButton;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PFunctions extends PMain {
    public PFunctions(Player player, PMain main) {
        super(player, main);
    }

    public PFunctions(Player p, ItemStack item, String name, int slot, Icon category, boolean isCategory) {
        super(p, item, name, slot, category, isCategory, true);
        super.setFunctions(this);
        super.initialize(p);
        initialize(p);
    }

    public PFunctions(Player p, Icon icon) {
        super(p, icon, true);
        super.setFunctions(this);
        super.initialize(p);
        initialize(p);
    }

    @Override
    public void initialize(Player p) {
        super.initialize(p);

        Button aB = getButton(1);
        Button fB = getButton(2);
        Button dB = getButton(3);

        aB.setItem(new ItemBuilder(aB.getItem()).removeEnchantments().getItem());
        fB.setItem(new ItemBuilder(fB.getItem()).addEnchantment(Enchantment.DAMAGE_ALL, 1).setHideEnchantments(true).getItem());
        dB.setItem(new ItemBuilder(dB.getItem()).removeEnchantments().getItem());

        if(aB.getLink() == null) aB.setLink(getAppearance());
        if(fB.getLink() != null) fB.setLink(null);
        if(dB.getLink() == null) dB.setLink(getDestination());

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));

        addButton(new StatusButton(1, 2, getIcon()).setOption(option));
        addButton(new CommandButton(2, 2, getIcon()).setOption(option));
        addButton(new PermissionButton(3, 2, getIcon()).setOption(option));
        addButton(new CostsButton(4, 2, getIcon()).setOption(option));
    }
}
