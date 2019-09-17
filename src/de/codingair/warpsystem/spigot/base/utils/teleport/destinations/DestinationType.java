package de.codingair.warpsystem.spigot.base.utils.teleport.destinations;

import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.*;
import de.codingair.warpsystem.spigot.features.effectportals.utils.PortalDestinationAdapter;

public enum DestinationType {
    UNKNOWN(-1, null),
    WarpIcon(0, SimpleWarpAdapter.class),
    SimpleWarp(1, SimpleWarpAdapter.class),
    GlobalWarpIcon(2, GlobalWarpAdapter.class),
    GlobalWarp(3, GlobalWarpAdapter.class),
    EffectPortal(4, PortalDestinationAdapter.class),
    Server(5, ServerAdapter.class),
    ;

    private int id;
    private Class<? extends DestinationAdapter> adapter;

    DestinationType(int id, Class<? extends DestinationAdapter> adapter) {
        this.id = id;
        this.adapter = adapter;
    }

    public int getId() {
        return id;
    }

    public Class<? extends DestinationAdapter> getAdapter() {
        return adapter;
    }

    public DestinationAdapter getInstance() {
        if(getAdapter() == null) return null;

        try {
            return getAdapter().newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
