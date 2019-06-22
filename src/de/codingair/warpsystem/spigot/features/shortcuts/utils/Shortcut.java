package de.codingair.warpsystem.spigot.features.shortcuts.utils;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.utils.JSONObject;

public class Shortcut extends FeatureObject {
    private String displayName;

    public Shortcut() {
    }

    public Shortcut(Shortcut shortcut) {
        super(shortcut);
        this.displayName = shortcut.getDisplayName();
    }

    public Shortcut(Destination destination, String displayName) {
        super(null, false, new WarpAction(destination));
        this.displayName = displayName;
    }

    @Override
    public boolean read(JSONObject json) throws Exception {
        this.displayName = (String) json.get("Name");
        return super.read(json);
    }

    @Override
    public void write(JSONObject json) {
        json.put("Name", this.displayName);
        super.write(json);
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        if(object instanceof Shortcut) {
            this.displayName = ((Shortcut) object).displayName;
        }
    }

    public boolean isActive() {
        if(getDestination() == null || getDestination().getAdapter() == null) return false;

        if(getDestination().getType() == DestinationType.GlobalWarp) {
            return WarpSystem.getInstance().isOnBungeeCord();
        }

        return true;
    }

    public Destination getDestination() {
        return hasAction(Action.WARP) ? ((WarpAction) getAction(Action.WARP)).getValue() : null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Shortcut clone() {
        return new Shortcut(this);
    }
}
