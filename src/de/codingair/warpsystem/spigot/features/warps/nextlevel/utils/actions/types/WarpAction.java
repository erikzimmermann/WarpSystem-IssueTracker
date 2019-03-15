package de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.types;

import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.Action;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.actions.ActionObject;
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
    public String write() {
        return getValue().getType() == DestinationType.UNKNOWN ? null : getValue().toJSONString();
    }

    @Override
    public boolean perform(Player player) {
        return true;
    }
}
