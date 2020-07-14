package de.codingair.warpsystem.spigot.features.portals.guis.pages;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.guis.editor.StandardButtonOption;
import de.codingair.warpsystem.spigot.base.guis.editor.buttons.*;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class POptions extends PageItem {
    private final Portal clone;

    public POptions(Player p, Portal clone) {
        super(p, PortalEditor.getMainTitle(), new ItemBuilder(XMaterial.COMMAND_BLOCK).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Options")).getItem(), false);

        this.clone = clone;
        initialize(p);
    }

    @Override
    public boolean initialize(SimpleGUI gui) {
        boolean result = super.initialize(gui);
        updatePage();
        return result;
    }

    @Override
    public void initialize(Player p) {
        StandardButtonOption option = new StandardButtonOption();

        addButton(new SyncButton(1, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.ENDER_EYE)
                        .setName("§6§n" + Lang.get("Permission") + Lang.PREMIUM_LORE)
                        .addLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                Lang.PREMIUM_CHAT(player);
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT;
            }
        }.setOption(option));

        addButton(new CommandButton(2, 2, clone).setOption(option));
        addButton(new CooldownButton(3, 2, clone).setOption(option));

        addButton(new SyncButton(4, 2) {
            @Override
            public ItemStack craftItem() {
                return new ItemBuilder(XMaterial.GOLD_NUGGET)
                        .setName("§6§n" + Lang.get("Costs") + Lang.PREMIUM_LORE)
                        .setLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                        .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"))
                        .getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                Lang.PREMIUM_CHAT(player);
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT;
            }
        }.setOption(option));

        addButton(new SyncButton(5, 2) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder b = new ItemBuilder().setName(Editor.ITEM_TITLE_COLOR + Lang.get("Teleport_Trigger"));
                String loreStart = Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": ";

                if(!clone.getBlocks().isEmpty() && !clone.getAnimations().isEmpty() && clone.getTrigger() == 0)
                    b.setType(XMaterial.LIME_TERRACOTTA).setLore(loreStart + "§a" + Lang.get("Portal_Blocks") + " §7+ §a" + Lang.get("Particle_Effects"));
                else if(!clone.getBlocks().isEmpty() && (clone.getAnimations().isEmpty() || clone.getTrigger() == 1))
                    b.setType(XMaterial.YELLOW_TERRACOTTA).setLore(loreStart + "§e" + Lang.get("Portal_Blocks"));
                else if(!clone.getAnimations().isEmpty() && (clone.getBlocks().isEmpty() || clone.getTrigger() == 2))
                    b.setType(XMaterial.LIGHT_BLUE_TERRACOTTA).setLore(loreStart + "§b" + Lang.get("Particle_Effects"));
                else b.setType(XMaterial.RED_TERRACOTTA).setLore(loreStart + "§c-");

                if(!clone.getBlocks().isEmpty() && !clone.getAnimations().isEmpty()) {
                    b.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §7" + Lang.get("Toggle"));
                }

                return b.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                clone.setTrigger(clone.getTrigger() + 1);
                update();
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT && !clone.getBlocks().isEmpty() && !clone.getAnimations().isEmpty();
            }
        }.setOption(option));
    }
}
