package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.exceptions.ActionObjectReadException;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.exceptions.IconReadException;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.ActionObject;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.CostsAction;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types.WarpAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Icon implements Serializable {
    private String name;
    private ItemStack item;
    private int slot;
    private Icon category;
    private String permission;
    private List<ActionObject> actions;
    private boolean isCategory = false;
    private boolean disabled = false;

    public Icon() {
    }

    public Icon(Icon icon) {
        this.name = icon.getName();
        this.item = icon.getItem().clone();
        this.slot = icon.getSlot();
        this.isCategory = icon.isCategory();
        this.disabled = icon.isDisabled();
        this.slot = icon.getSlot();
        this.category = icon.getCategory();
        this.permission = icon.getPermission();
        this.actions = new ArrayList<>(icon.getActions());
    }

    public Icon(String name, ItemStack item, Icon category, int slot, String permission, List<ActionObject> actions) {
        this.name = name;
        this.item = item;
        this.category = category;
        this.slot = slot;
        this.permission = permission;
        this.actions = actions;
    }

    public Icon(String name, ItemStack item, Icon category, int slot, String permission, ActionObject... actions) {
        this.name = name;
        this.item = item;
        this.category = category;
        this.slot = slot;
        this.permission = permission;
        this.actions = new ArrayList<>();

        for(ActionObject action : actions) {
            this.actions.contains(action);
        }
    }

    public void perform(Player player) {
        if(this.actions == null) return;

        if(getAction(Action.WARP) != null) {
            double costs = getAction(CostsAction.class) == null ? 0 : getAction(CostsAction.class).getValue();
            WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.WarpIcon, getAction(WarpAction.class).getValue(), getAction(WarpAction.class).getValue().getId(), costs, WarpSystem.getInstance()
                    .getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message.WarpGUI", true), new Callback<TeleportResult>() {
                @Override
                public void accept(TeleportResult result) {
                    if(result == TeleportResult.TELEPORTED) {
                        for(ActionObject action : actions) {
                            if(action.getType() == Action.WARP || action.getType() == Action.COSTS) continue;
                            action.perform(player);
                        }
                    }
                }
            });
        } else if(getAction(Action.COSTS) == null || getAction(Action.COSTS).perform(player)) {
            for(ActionObject action : this.actions) {
                if(action.getType() == Action.WARP || action.getType() == Action.COSTS) continue;
                action.perform(player);
            }
        }
    }

    public Icon clone() {
        return new Icon(this);
    }

    public void apply(Icon icon) {
        this.destroy();

        this.name = icon.getName();
        this.item = icon.getItem();
        this.slot = icon.getSlot();
        this.isCategory = icon.isCategory();
        this.disabled = icon.isDisabled();
        this.slot = icon.getSlot();
        this.category = icon.getCategory();
        this.permission = icon.getPermission();
        this.actions = icon.getActions();
    }

    @Override
    public void destroy() {
        this.item = null;
        this.slot = 0;
        this.isCategory = false;
        this.disabled = false;
        this.name = null;
        this.category = null;
        this.permission = null;

        if(this.actions != null) {
            this.actions.forEach(Serializable::destroy);
            this.actions.clear();
            this.actions = null;
        }
    }

    @Override
    public void read(String s) throws IconReadException {
        destroy();

        JSONObject json;
        try {
            json = (JSONObject) new JSONParser().parse(s);
        } catch(ParseException e) {
            throw new IconReadException("Could not parse json data.", e);
        }

        this.name = json.get("name") == null ? null : (String) json.get("name");
        ItemBuilder builder = ItemBuilder.getFromJSON((String) json.get("item"));
        this.item = builder.getItem();

        this.slot = Integer.parseInt(json.get("slot") + "");
        this.isCategory = Boolean.parseBoolean(json.get("isCategory") + "");
        this.disabled = Boolean.parseBoolean(json.get("disabled") + "");

        this.category = json.get("category") == null ? null : IconManager.getInstance().getCategory((String) json.get("category"));
        this.permission = json.get("permission") == null ? null : (String) json.get("permission");

        this.actions = new ArrayList<>();
        JSONArray actionList;
        try {
            actionList = (JSONArray) new JSONParser().parse((String) json.get("actions"));
        } catch(ParseException e) {
            throw new IconReadException("Could not parse action list.", e);
        }

        for(Object o : actionList) {
            String data = (String) o;
            JSONObject j;
            try {
                j = (JSONObject) new JSONParser().parse(data);
            } catch(ParseException e) {
                throw new IconReadException("Could not parse action object.", e);
            }

            int id = Integer.parseInt(j.get("id") + "");
            Object value = j.get("value");
            String validData = value instanceof String ? (String) value : null;

            Action a = Action.getById(id);
            if(a != null) {
                ActionObject<?> ao;
                try {
                    ao = a.getClazz().newInstance();
                } catch(InstantiationException | IllegalAccessException e) {
                    throw new IconReadException("Could not initialize action object instance.", e);
                }

                try {
                    ao.read(validData);
                } catch(Exception e) {
                    throw new ActionObjectReadException("Could not read ActionObject properly.", e);
                }

                this.actions.add(ao);
            }
        }
    }

    @Override
    public String write() {
        JSONObject json = new JSONObject();

        ItemBuilder builder = new ItemBuilder(this.item);
        json.put("name", this.name);
        json.put("item", builder.toJSONString());
        json.put("slot", this.slot);
        json.put("isCategory", this.isCategory);
        json.put("disabled", this.disabled);
        json.put("category", this.category == null ? null : this.category.getName());
        json.put("permission", this.permission);

        JSONArray actionList = new JSONArray();
        if(this.actions != null) {
            for(ActionObject action : this.actions) {
                JSONObject jo = new JSONObject();
                jo.put("id", action.getType().getId());
                jo.put("value", action.write());
                actionList.add(jo.toJSONString());
            }
        }

        json.put("actions", actionList.toJSONString());
        return json.toJSONString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Icon icon = (Icon) o;
        return slot == icon.slot &&
                isCategory == icon.isCategory &&
                disabled == icon.disabled &&
                name.equals(icon.name) &&
                item.equals(icon.item) &&
                Objects.equals(category, icon.category) &&
                Objects.equals(permission, icon.permission) &&
                actions.equals(icon.actions);
    }

    public String getName() {
        return name;
    }

    public String getNameWithoutColor() {
        return name == null ? null : ChatColor.stripColor(name);
    }

    public Icon setName(String name) {
        this.name = name;
        return this;
    }

    public ItemStack getItem() {
        return new ItemBuilder(item).setName(this.name == null ? null : "§r" + ChatColor.translateAlternateColorCodes('&', this.name)).checkFirstLine().getItem();
    }

    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(item).setName(this.name == null ? null : ChatColor.translateAlternateColorCodes('&', this.name));
    }

    public Icon changeItem(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item);
        this.item = new ItemBuilder(this.item).setType(item.getType()).setData(builder.getData()).getItem();
        return this;
    }

    public Icon setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Icon setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public Icon getCategory() {
        return category;
    }

    public Icon setCategory(Icon category) {
        this.category = category;
        return this;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission() {
        return this.permission != null;
    }

    public Icon setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public boolean removeAction(Action action) {
        ActionObject ao = getAction(action);
        if(ao == null) return false;
        this.actions.remove(ao);
        return true;
    }

    public Icon addAction(ActionObject action) {
        ActionObject ao = getAction(action.getType());
        if(ao != null) this.actions.remove(ao);

        this.actions.add(action);
        return this;
    }

    public List<ActionObject> getActions() {
        return actions;
    }

    public <T extends ActionObject> T getAction(Action action) {
        for(ActionObject ao : this.actions) {
            if(ao.getType() == action) return (T) ao;
        }

        return null;
    }

    public <T extends ActionObject> T getAction(Class<T> clazz) {
        for(ActionObject ao : this.actions) {
            if(ao.getClass() == clazz) return (T) ao;
        }

        return null;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public void setCategory(boolean category) {
        isCategory = category;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
