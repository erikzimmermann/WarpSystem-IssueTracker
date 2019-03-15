package de.codingair.warpsystem.spigot.features.warps.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButtonOption;
import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.editor.PEditor;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PMain extends PEditor {
    private PAppearance appearance;
    private PFunctions functions;
    private PDestination destination;
    private PMain main;

    public PMain(Player player, PMain main) {
        super(player, main);
        this.main = main;
    }

    public PMain(Player p, ItemStack item, String name, int slot, Icon category, boolean isCategory) {
        super(p, item, name, slot, category, isCategory);
        initialize(p);
    }

    public PMain(Player p, Icon icon) {
        super(p, icon);
        initialize(p);
    }

    public PMain(Player p, ItemStack item, String name, int slot, Icon category, boolean isCategory, boolean wait) {
        super(p, item, name, slot, category, isCategory);
        if(!wait) initialize(p);
    }

    public PMain(Player p, Icon icon, boolean wait) {
        super(p, icon);
        if(!wait) initialize(p);
    }

    @Override
    public void initialize(Player p) {
        super.initialize(p);

        if(main == null) {
            if(this.appearance == null) this.appearance = new PAppearance(p, this);
            if(this.functions == null) this.functions = new PFunctions(p, this);
            if(this.destination == null) this.destination = new PDestination(p, this);

            if(!(this instanceof PAppearance)) this.appearance.initialize(p);
            if(!(this instanceof PFunctions)) this.functions.initialize(p);
            if(!(this instanceof PDestination)) this.destination.initialize(p);
        }

        ItemButtonOption option = new ItemButtonOption();
        option.setClickSound(new SoundData(Sound.CLICK, 0.7F, 1F));
        option.setOnlyLeftClick(true);

        Button appearance = new Button(1, new ItemBuilder(XMaterial.ITEM_FRAME).setName("§6§n" + Lang.get("Appearance")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
            }
        }.setOption(option);
        appearance.setLink(getAppearance());

        Button functions = new Button(2, new ItemBuilder(XMaterial.COMMAND_BLOCK).setName("§6§n" + Lang.get("Options")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
            }
        }.setOption(option);
        functions.setLink(getFunctions());

        Button destination = new Button(3, new ItemBuilder(XMaterial.ENDER_PEARL).setName("§6§n" + Lang.get("Destination")).getItem()) {
            @Override
            public void onClick(InventoryClickEvent e, Player player) {
            }
        }.setOption(option);
        destination.setLink(getDestination());

        addButton(appearance);
        addButton(functions);
        addButton(destination);
    }

    public PAppearance getAppearance() {
        if(this.main != null) return this.main.getAppearance();
        return appearance;
    }

    public PFunctions getFunctions() {
        if(this.main != null) return this.main.getFunctions();
        return functions;
    }

    public PDestination getDestination() {
        if(this.main != null) return this.main.getDestination();
        return destination;
    }

    public void setAppearance(PAppearance appearance) {
        this.appearance = appearance;
    }

    public void setFunctions(PFunctions functions) {
        this.functions = functions;
    }

    public void setDestination(PDestination destination) {
        this.destination = destination;
    }
}
