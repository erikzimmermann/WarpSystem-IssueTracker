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
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CommandAction;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandButton extends SyncAnvilGUIButton {
    private FeatureObject object;

    public CommandButton(int x, int y, FeatureObject object) {
        super(x, y, ClickType.LEFT);

        this.object = object;
        update(false);
    }

    @Override
    public ItemStack craftItem() {
        if(object == null) return new ItemStack(Material.AIR);

        CommandAction action = object.getAction(Action.COMMAND);
        List<String> commands = action == null ? null : action.getValue();
        List<String> commandInfo = new ArrayList<>();

        if(commands != null) {
            for(String command : commands) {
                commandInfo.add("§7- '§r" + command + "§7'");
            }
        }

        List<String> lore = new ArrayList<>();
        if(commands != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

        return new ItemBuilder(XMaterial.REDSTONE)
                .setName("§6§n" + Lang.get("Command"))
                .setLore("§3" + Lang.get("Current") + ": " + (commands == null ? "§c" + Lang.get("Not_Set") : ""))
                .addLore(commandInfo)
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"))
                .addLore(lore)
                .getItem();
    }

    @Override
    public void onClick(AnvilClickEvent e) {
        if(!e.getSlot().equals(AnvilSlot.OUTPUT)) return;

        String input = e.getInput();

        if(input == null) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Command"));
            return;
        }

        if(!input.startsWith("/")) input = "/" + input;

        e.setClose(true);

        CommandAction action = object.getAction(Action.COMMAND);
        if(action == null) {
            action = new CommandAction(input);
            object.addAction(action);
        } else {
            action.getValue().add(input);
        }

        update();
    }

    @Override
    public void onClose(AnvilCloseEvent e) {

    }

    @Override
    public ItemStack craftAnvilItem(ClickType trigger) {
        return new ItemBuilder(XMaterial.PAPER).setName(Lang.get("Command") + "...").getItem();
    }

    @Override
    public void onOtherClick(InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT) {
            object.removeAction(Action.COMMAND);
            update();
        }
    }
}
