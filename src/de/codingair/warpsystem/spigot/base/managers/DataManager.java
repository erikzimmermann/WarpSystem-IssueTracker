package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.utils.Manager;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private List<Manager> managers = new ArrayList<>();

    public DataManager() {
        for(FeatureType.Priority value : FeatureType.Priority.values()) {
            if(value == FeatureType.Priority.DISABLED) continue;

            for(FeatureType ft : FeatureType.values(value)) {
                if(!ft.isActive()) continue;

                try {
                    this.managers.add(ft.getManagerClass().newInstance());
                } catch(InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean load() {
        boolean success = true;
        for(Manager manager : this.managers) {
            if(!manager.load()) success = false;
        }

        WarpSystem.getInstance().getFileManager().getFile("Config").saveConfig();

        return success;
    }

    public void save(boolean saver) {
        for(Manager manager : this.managers) {
            manager.save(saver);
        }
    }

    public <T extends Manager> T getManager(FeatureType type) {
        for(Manager manager : this.managers) {
            if(manager.getClass().equals(type.getManagerClass())) return (T) manager;
        }

        return null;
    }

    public List<Manager> getManagers() {
        return managers;
    }
}
