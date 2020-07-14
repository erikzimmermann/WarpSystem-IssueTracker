package de.codingair.warpsystem.spigot.features.shortcuts.utils;

import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;

import java.util.Objects;

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
    public boolean read(DataWriter d) throws Exception {
        this.displayName = d.getString("Name");
        return super.read(d);
    }

    @Override
    public void write(DataWriter d) {
        d.put("Name", this.displayName);
        super.write(d);
        setPermission(null);
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        if(object instanceof Shortcut) {
            this.displayName = ((Shortcut) object).displayName;
        }
    }

    public boolean isActive() {
        if(getActions().isEmpty()) return false;

        if(hasAction(Action.WARP)) {
            if(getDestination().getType() == DestinationType.GlobalWarp || getDestination().getType() == DestinationType.Server) {
                return WarpSystem.getInstance().isOnBungeeCord();
            }
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

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        Shortcut shortcut = (Shortcut) o;
        return displayName.equals(shortcut.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName);
    }
}
