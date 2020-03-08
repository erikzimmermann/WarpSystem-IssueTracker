package de.codingair.warpsystem.spigot.base.utils.featureobjects;

import com.google.common.base.CharMatcher;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.io.lib.ParseException;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObjectReadException;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CostsAction;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.exceptions.IconReadException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.codingapi.tools.io.JSON.JSON;

import java.util.*;

public class FeatureObject implements Serializable {
    private List<ActionObject<?>> actions;
    private String permission = null;
    private boolean disabled = false;
    private boolean skip = false;
    protected int performed = 0;

    public FeatureObject() {
        this.actions = new ArrayList<>();
    }

    public FeatureObject(String permission, boolean disabled, List<ActionObject<?>> actions) {
        this.permission = permission;
        this.disabled = disabled;
        this.actions = actions == null ? new ArrayList<>() : actions;
    }

    public FeatureObject(String permission, boolean disabled, ActionObject<?>... actions) {
        this.permission = permission;
        this.disabled = disabled;
        this.actions = new ArrayList<>(Arrays.asList(actions));
    }

    public FeatureObject(FeatureObject featureObject) {
        this.actions = featureObject.getCopyOfActions();
        this.permission = featureObject.permission;
        this.disabled = featureObject.disabled;
        this.skip = featureObject.skip;
        this.performed = featureObject.performed;
    }

    public FeatureObject perform(Player player) {
        return perform(player, hasAction(Action.WARP) ? getAction(WarpAction.class).getValue().getId() : null, hasAction(Action.WARP) ? getAction(WarpAction.class).getValue() : null, new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F), skip, true);
    }

    public FeatureObject perform(Player player, String destName, Destination dest, SoundData sound, boolean skip, boolean afterEffects) {
        TeleportOptions options = new TeleportOptions(dest, destName);
        options.setTeleportSound(sound);
        options.setSkip(skip);
        options.setCanMove(skip);
        options.setAfterEffects(afterEffects);

        return perform(player, options);
    }

    public FeatureObject perform(Player player, TeleportOptions options) {
        if(this.actions == null) return this;

        if(options.getDestination() == null) options.setDestination(hasAction(Action.WARP) ? getAction(WarpAction.class).getValue() : null);
        if(options.getDisplayName() == null) options.setDisplayName(hasAction(Action.WARP) ? getAction(WarpAction.class).getValue().getId() : null);
        if(options.getTeleportSound() == null) options.setTeleportSound(new SoundData(Sound.ENDERMAN_TELEPORT, 1F, 1F));
        options.setSkip(isSkip());

        if(getAction(Action.WARP) != null) {
            Origin origin = Origin.getByClass(this);

            options.setCosts(getAction(CostsAction.class) == null ? 0 : getAction(CostsAction.class).getValue());
            options.setOrigin(origin);
            options.setPermission(this.permission == null ? TeleportManager.NO_PERMISSION : permission);
            if(!origin.sendTeleportMessage()) options.setMessage(null);

            options.setCallback(new Callback<TeleportResult>() {
                @Override
                public void accept(TeleportResult result) {
                    if(result == TeleportResult.TELEPORTED) {
                        for(ActionObject<?> action : actions) {
                            if(action.getType() == Action.WARP || action.getType() == Action.COSTS) continue;
                            action.perform(player);
                        }
                    }
                }
            });

            WarpSystem.getInstance().getTeleportManager().teleport(player, options);
        } else if(getAction(Action.COSTS) == null || getAction(Action.COSTS).perform(player)) {
            for(ActionObject<?> action : this.actions) {
                if(action.getType() == Action.WARP || action.getType() == Action.COSTS) continue;
                action.perform(player);
            }
        }

        performed++;
        return this;
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        destroy();

        this.disabled = d.getBoolean("disabled");
        this.permission = d.get("permission");
        if(this.permission != null) this.permission = ChatColor.stripColor(CharMatcher.WHITESPACE.trimFrom(this.permission));

        this.skip = d.getBoolean("skip");
        this.performed = d.getInteger("performed");

        if(this.actions == null) this.actions = new ArrayList<>();

        if(d.get("actions") != null) {
            JSONArray actionList = d.getList("actions");

            for(Object o : actionList) {
                JSON j;

                if(o instanceof String) {
                    String data = (String) o;
                    try {
                        j = (JSON) new JSONParser().parse(data);
                    } catch(ParseException e) {
                        throw new IconReadException("Could not parse action object.", e);
                    }
                } else j = new JSON((Map<?, ?>) o);

                int id = j.getInteger("id");

                Object validData = j.getRaw("value");

                Action a = Action.getById(id);
                if(a != null) {
                    ActionObject<?> ao;
                    try {
                        ao = a.getClazz().newInstance();
                    } catch(InstantiationException | IllegalAccessException e) {
                        throw new IconReadException("Could not initialize action object instance.", e);
                    }

                    if(validData instanceof String) {
                        try {
                            ao.read((String) validData);
                        } catch(Exception e) {
                            throw new ActionObjectReadException("Could not read ActionObject properly.", e);
                        }
                    } else {
                        j.read(ao, "value");
                    }

                    this.actions.add(ao);
                }
            }
        }

        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("disabled", this.disabled);
        d.put("permission", this.permission);
        d.put("skip", this.skip);
        d.put("performed", this.performed);

        JSONArray actionList = new JSONArray();
        if(this.actions != null) {
            for(ActionObject<?> action : this.actions) {
                JSON jo = new JSON();
                jo.put("id", action.getType().getId());
                jo.put("value", action);
                actionList.add(jo);
            }
        }

        d.put("actions", actionList);
    }

    @Override
    public void destroy() {
        this.disabled = false;
        this.permission = null;

        if(this.actions != null) {
            this.actions.forEach(ActionObject::destroy);
            this.actions.clear();
        }
    }

    public void commitClonedActions() {
        List<ActionObject<?>> a = getActions();
        List<ActionObject<?>> cloned = new ArrayList<>();

        for(ActionObject<?> ao : a) {
            cloned.add(ao.clone());
        }

        a.clear();
        a.addAll(cloned);
        cloned.clear();
    }

    public void apply(FeatureObject object) {
        this.destroy();

        this.skip = object.skip;
        this.disabled = object.disabled;
        this.permission = object.permission;
        this.performed = object.performed;
        this.actions = object.getCopyOfActions();
        checkActionList();
    }

    public void checkActionList() {
        List<ActionObject<?>> l = new ArrayList<>(this.actions);

        for(ActionObject<?> object : l) {
            if(!object.usable()) {
                this.actions.remove(object);
            }
        }

        l.clear();
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

    public <T extends ActionObject<?>> T getAction(Action action) {
        for(ActionObject<?> ao : this.actions) {
            if(ao.getType() == action) return (T) ao;
        }

        return null;
    }

    public <T extends ActionObject<?>> T getAction(Class<T> clazz) {
        for(ActionObject<?> ao : this.actions) {
            if(ao.getClass() == clazz) return (T) ao;
        }

        return null;
    }

    public boolean hasAction(Action action) {
        return getAction(action) != null;
    }

    public boolean removeAction(Action action) {
        ActionObject<?> ao = getAction(action);
        if(ao == null) return false;
        this.actions.remove(ao);
        return true;
    }

    public FeatureObject addAction(ActionObject<?> action) {
        return addAction(action, true);
    }

    public FeatureObject addAction(ActionObject<?> action, boolean overwrite) {
        ActionObject<?> ao = getAction(action.getType());
        if(ao != null) {
            if(overwrite) this.actions.remove(ao);
            else return this;
        }

        this.actions.add(action);
        return this;
    }

    public List<ActionObject<?>> getActions() {
        return actions;
    }

    public List<ActionObject<?>> getCopyOfActions() {
        List<ActionObject<?>> l = new ArrayList<>();
        if(actions == null) return l;

        for(ActionObject<?> a : actions) {
            l.add(a.clone());
        }

        return l;
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

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public int getPerformed() {
        return performed;
    }
}
