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
    Location(6, LocationAdapter.class),
    GlobalLocation(7, GlobalLocationAdapter.class),
    ;

    private int id;
    private Class<? extends DestinationAdapter> adapter;

    DestinationType(int id, Class<? extends DestinationAdapter> adapter) {
        this.id = id;
        this.adapter = adapter;
    }

    public static DestinationType getByAdapter(DestinationAdapter adapter) {
        if(adapter == null) return UNKNOWN;

        for(DestinationType value : values()) {
            if(adapter.getClass().equals(value.adapter)) return value;
        }

        return UNKNOWN;
    }

    public static DestinationType getById(int id) {
        for(DestinationType value : values()) {
            if(value.getId() == id) return value;
        }

        return UNKNOWN;
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
