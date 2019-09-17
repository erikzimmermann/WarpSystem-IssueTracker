package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilSlot;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncAnvilGUIButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CostsAction;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CostsButton extends SyncAnvilGUIButton {
    private FeatureObject object;

    public CostsButton(int x, int y, FeatureObject object) {
        super(x, y, ClickType.LEFT);

        this.object = object;
        update(false);
    }
    
    @Override
    public ItemStack craftItem() {
        if(object == null) return new ItemStack(Material.AIR);

        CostsAction action = object.getAction(Action.COSTS);
        double costs = action == null ? 0 : action.getValue();
        String costsPrint = costs + "";
        if(costsPrint.endsWith(".0")) costsPrint = costsPrint.substring(0, costsPrint.length() - 2);

        List<String> lore = new ArrayList<>();
        if(costs != 0) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

        return new ItemBuilder(XMaterial.GOLD_NUGGET)
                .setName("§6§n" + Lang.get("Costs"))
                .setLore("§3" + Lang.get("Current") + ": " + (costs == 0 ? "§c" + Lang.get("Not_Set") : "§7" + costsPrint + " " + Lang.get("Coins")))
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + (costs == 0 ? Lang.get("Set") : Lang.get("Change")))
                .addLore(lore)
                .getItem();
    }

    @Override
    public void onClick(AnvilClickEvent e) {
        if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

        String input = e.getInput();

        if(input == null) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
            return;
        }

        double costs;
        try {
            costs = Double.parseDouble(input);
        } catch(NumberFormatException ex) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
            return;
        }

        if(costs < 0) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_A_Positive_Number"));
            return;
        }

        e.setClose(true);

        if(costs == 0) {
            object.removeAction(Action.COSTS);
        } else {
            object.addAction(new CostsAction(costs));
        }

        update();
    }

    @Override
    public void onClose(AnvilCloseEvent e) {

    }

    @Override
    public ItemStack craftAnvilItem(ClickType trigger) {
        if(object == null) return new ItemStack(Material.AIR);

        CostsAction action = object.getAction(Action.COSTS);
        double costs = action == null ? 0 : action.getValue();
        String costsPrint = costs + "";
        return new ItemBuilder(XMaterial.PAPER).setName(costsPrint).getItem();
    }

    @Override
    public void onOtherClick(InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT) {
            object.removeAction(Action.COSTS);
            update();
        }
    }
}
