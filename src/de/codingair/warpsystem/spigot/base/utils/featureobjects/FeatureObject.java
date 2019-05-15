package de.codingair.warpsystem.spigot.base.utils.featureobjects;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObjectReadException;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CostsAction;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.exceptions.IconReadException;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FeatureObject implements Serializable {
    private List<ActionObject> actions;
    private String permission = null;
    private boolean disabled = false;

    public FeatureObject() {
    }

    public FeatureObject(String permission, boolean disabled, List<ActionObject> actions) {
        this.permission = permission;
        this.disabled = disabled;
        this.actions = actions;
    }

    public FeatureObject(String permission, boolean disabled, ActionObject... actions) {
        this.permission = permission;
        this.disabled = disabled;
        this.actions = new ArrayList<>(Arrays.asList(actions));
    }

    public FeatureObject(FeatureObject featureObject) {
        this.actions = new ArrayList<>(featureObject.actions);
        this.permission = featureObject.permission;
        this.disabled = featureObject.disabled;
    }

    public FeatureObject perform(Player player) {
        if(this.actions == null) return this;

        if(getAction(Action.WARP) != null) {
            double costs = getAction(CostsAction.class) == null ? 0 : getAction(CostsAction.class).getValue();
            WarpSystem.getInstance().getTeleportManager().teleport(player, Origin.WarpIcon, getAction(WarpAction.class).getValue(), getAction(WarpAction.class).getValue().getId(), TeleportManager.NO_PERMISSION, costs, WarpSystem.getInstance()
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
        return this;
    }

    @Override
    public void read(JSONObject json) throws Exception {
        destroy();

        this.disabled = Boolean.parseBoolean(json.get("disabled") + "");
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
    public void write(JSONObject json) {
        json.put("disabled", this.disabled);
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
    }

    @Override
    public void destroy() {
        this.disabled = false;
        this.permission = null;

        if(this.actions != null) {
            this.actions.forEach(ActionObject::destroy);
            this.actions.clear();
            this.actions = null;
        }
    }

    public void apply(FeatureObject object) {
        this.destroy();

        this.disabled = object.disabled;
        this.permission = object.permission;
        this.actions = object.actions;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        FeatureObject object = (FeatureObject) o;
        return disabled == object.disabled &&
                Objects.equals(permission, object.permission) &&
                actions.equals(object.actions);
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

    public boolean removeAction(Action action) {
        ActionObject ao = getAction(action);
        if(ao == null) return false;
        this.actions.remove(ao);
        return true;
    }

    public FeatureObject addAction(ActionObject action) {
        ActionObject ao = getAction(action.getType());
        if(ao != null) this.actions.remove(ao);

        this.actions.add(action);
        return this;
    }

    public List<ActionObject> getActions() {
        return actions;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission() {
        return this.permission != null;
    }

    public FeatureObject setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public FeatureObject setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }
}
