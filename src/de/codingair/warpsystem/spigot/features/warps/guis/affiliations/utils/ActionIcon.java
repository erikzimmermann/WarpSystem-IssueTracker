package de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.transfer.packets.spigot.PerformCommandPacket;
import de.codingair.warpsystem.transfer.serializeable.icons.SActionIcon;
import de.codingair.warpsystem.transfer.serializeable.icons.SActionObject;
import de.codingair.warpsystem.transfer.serializeable.icons.SIcon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class ActionIcon extends Icon implements Serializable {
    static final long serialVersionUID = 1L;

    String permission;
    List<ActionObject> actions;

    public ActionIcon() {
    }

    public ActionIcon(String name, ItemStack item, int slot) {
        super(name, item, slot);
        actions = new ArrayList<>();
        permission = null;
    }

    public ActionIcon(String name, ItemStack item, int slot, String permission) {
        super(name, item, slot);
        actions = new ArrayList<>();
        this.permission = permission;
    }

    public ActionIcon(String name, ItemStack item, int slot, List<ActionObject> actions) {
        this(name, item, slot);
        this.actions = actions;
    }

    public ActionIcon(String name, ItemStack item, int slot, String permission, List<ActionObject> actions) {
        this(name, item, slot, permission);
        this.actions = actions;
    }

    public ActionIcon(String name, ItemStack item, int slot, ActionObject... actions) {
        this(name, item, slot, Arrays.asList(actions));
    }

    public ActionIcon(String name, ItemStack item, int slot, String permission, ActionObject... actions) {
        this(name, item, slot, permission, Arrays.asList(actions));
    }

    public ActionIcon(SActionIcon icon) {
        super(icon);
        this.permission = icon.getPermission();
        this.actions = new ArrayList<>();
        for(SActionObject s : icon.getActions()) {
            this.actions.add(ActionIconHelper.translate(s));
        }
    }

    @Override
    public SIcon getSerializable() {
        SActionIcon s = new SActionIcon(super.getSerializable());

        s.setPermission(this.permission);
        List<SActionObject> actions = new ArrayList<>();
        for(ActionObject action : this.actions) {
            actions.add(ActionIconHelper.translate(action));
        }
        s.setActions(actions);

        return s;
    }

    public void perform(Player p, boolean editor, Action... without) {
        List<Action> withouts = Arrays.asList(without);

        for(Action.Priority value : Action.Priority.values()) {
            for(ActionObject ao : getActions(value)) {
                if(!withouts.contains(ao.getAction()) && run(ao, p, editor)) return;
            }
        }
    }

    /**
     * @return 'true' if the loop should be canceled.
     */
    private boolean run(ActionObject action, Player p, boolean editor) {
        switch(action.getAction()) {
            case OPEN_CATEGORY:
                Category category = action.getValue();
//                new GWarps(p, category, editor).open();
                break;

            case RUN_COMMAND:
                String command = action.getValue();
                if(command.startsWith("/")) command = command.substring(1);

                String tag = command.contains(" ") ? command.split(" ")[0] : command;

                if(WarpSystem.getInstance().isOnBungeeCord() && Bukkit.getPluginCommand(tag) == null) {
                    WarpSystem.getInstance().getDataHandler().send(new PerformCommandPacket(p.getName(), command, new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean exists) {
                            if(!exists) p.sendMessage(org.spigotmc.SpigotConfig.unknownCommandMessage);
                        }
                    }));
                } else p.performCommand(command);
                break;

            case SWITCH_SERVER: {
                if(WarpSystem.getInstance().getTeleportManager().isTeleporting(p)) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting"));
                    break;
                }

                String server = action.getValue();
                double costs = getAction(Action.PAY_MONEY) == null ? 0 : getAction(Action.PAY_MONEY).getValue();
                if(p.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs)) costs = 0;

                WarpSystem.getInstance().getTeleportManager().teleport(p, Origin.GlobalWarpIcon, new Destination(server, DestinationType.GlobalWarp), getName(), costs);
                break;
            }

            case TELEPORT_TO_WARP: {
                if(!(this instanceof Warp)) break;

                double costs = getAction(Action.PAY_MONEY) == null ? 0 : getAction(Action.PAY_MONEY).getValue();
                if(p.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs)) costs = 0;

//                WarpSystem.getInstance().getTeleportManager().teleport(p, Origin.WarpIcon, new Destination(((Warp) this).getIdentifier(), DestinationType.WarpIcon), getName(), costs);
                break;
            }

            case PAY_MONEY:
                if(p.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs)) break;
                if(AdapterType.getActive() == null) break;
                if(WarpSystem.getInstance().getTeleportManager().isTeleporting(p)) break;

                double prize = action.getValue();
                if(prize <= 0) break;

                double bank = AdapterType.getActive().getMoney(p);

                if(bank < prize) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Not_Enough_Money").replace("%AMOUNT%", (prize % ((int) prize) == 0 ? (int) prize : prize) + ""));
                    return true;
                }

                AdapterType.getActive().setMoney(p, bank - prize);
                break;
        }

        return false;
    }

    public void perform(Player p) {
        perform(p, false);
    }

    public List<ActionObject> getActions() {
        return actions;
    }

    public List<ActionObject> getActions(Action.Priority priority) {
        List<ActionObject> actions = new ArrayList<>(this.actions);

        for(ActionObject action : this.actions) {
            if(!action.action.getPriority().equals(priority)) actions.remove(action);
        }

        return actions;
    }

    public ActionObject getAction(Action action) {
        for(ActionObject actionObject : this.actions) {
            if(actionObject.getAction().equals(action)) return actionObject;
        }

        return null;
    }

    private void checkList() {
        if(!(this.actions instanceof ArrayList)) {
            List<ActionObject> actionObjects = new ArrayList<>();
            actionObjects.addAll(this.actions);
            this.actions = actionObjects;
        }
    }

    public void addAction(ActionObject action) {
        checkList();
        this.actions.add(action);
    }

    public void removeAction(Action action) {
        checkList();
        ActionObject object = null;

        for(ActionObject actionObject : actions) {
            if(actionObject.getAction().equals(action)) {
                object = actionObject;
                break;
            }
        }

        if(object != null) actions.remove(object);
    }

    public void addAllActions(Collection<ActionObject> actions) {
        checkList();
        this.actions.addAll(actions);
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean hasPermission() {
        return permission != null;
    }
}
