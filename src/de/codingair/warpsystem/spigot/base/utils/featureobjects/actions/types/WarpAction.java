package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types;

import de.codingair.codingapi.tools.io.DataWriter;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import org.bukkit.entity.Player;

public class WarpAction extends ActionObject<Destination> {
    public WarpAction(Destination destination) {
        super(Action.WARP, destination);
    }

    public WarpAction() {
        this(null);
    }

    @Override
    public void read(String s) {
        if(s != null) {
            setValue(new Destination(s));
        }
    }

    @Override
    public boolean read(DataWriter d) {
        setValue(d.getSerializable("destination", new Destination()));
        return true;
    }

    @Override
    public void write(DataWriter d) {
        d.put("destination", getValue());
    }

    @Override
    public String write() {
        return getValue() == null || getValue().getType() == DestinationType.UNKNOWN ? null : getValue().toJSONString();
    }

    @Override
    public boolean perform(Player player) {
        return true;
    }

    @Override
    public boolean usable() {
        return getValue() != null && getValue().getId() != null;
    }

    @Override
    public WarpAction clone() {
        return new WarpAction(getValue() == null ? null : getValue().clone());
    }
}
