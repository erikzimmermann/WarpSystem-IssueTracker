package de.codingair.warpsystem.spigot.base.guis.editor.buttons;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.api.chatinput.ChatInputEvent;
import de.codingair.warpsystem.spigot.api.chatinput.SyncChatInputGUIButton;
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

public class CommandButton extends SyncChatInputGUIButton {
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
                List<String> list = TextAlignment.lineBreak("§7- '§r" + command + "§7'", 200);
                commandInfo.addAll(list);
                list.clear();
            }
        }

        List<String> lore = new ArrayList<>();
        if(commands != null) lore.add("§3" + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

        return new ItemBuilder(XMaterial.REDSTONE)
                .setName("§6§n" + Lang.get("Command"))
                .setLore(Lang.get("Command_button_hint") + Lang.PREMIUM_LORE)
                .addLore("", "§3" + Lang.get("Current") + ": " + (commands == null ? "§c" + Lang.get("Not_Set") : ""))
                .addText(commandInfo)
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"))
                .addLore(lore)
                .getItem();
    }

    @Override
    public void onEnter(ChatInputEvent e) {
        String input = e.getText();

        if(input == null) {
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Enter_Command"));
            return;
        }

        if(!input.startsWith("/")) input = "/" + input;

        if(!isOkay(input)) {
            return;
        }

        if(e.getText().contains("%player%")) {
            e.setNotifier("§7Only §6premium §7can use §e%player%\n§7Get full access with \"§6/ws upgrade§7\"");
            e.setClose(false);
            return;
        }

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

    public boolean isOkay(String command) {
        return true;
    }

    @Override
    public void onOtherClick(InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT) {
            if(object.hasAction(Action.COMMAND)) {
                if(object.getAction(CommandAction.class).getValue().size() == 1) object.removeAction(Action.COMMAND);
                else object.getAction(CommandAction.class).getValue().remove(object.getAction(CommandAction.class).getValue().size() - 1);
            }
            update();
        }
    }
}
