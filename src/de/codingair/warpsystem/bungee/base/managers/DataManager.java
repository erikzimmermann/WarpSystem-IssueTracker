package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.teleport.listeners.TabCompleterListener;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private List<Manager> managers = new ArrayList<>();
    private List<String> oped = new ArrayList<>();

    public DataManager() {
        for(FeatureType.Priority value : FeatureType.Priority.values()) {
            for(FeatureType ft : FeatureType.values(value)) {
                try {
                    this.managers.add(ft.getManagerClass().newInstance());
                } catch(InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean load(boolean hidePrints) {
        boolean success = true;
        for(Manager manager : this.managers) {
            if(!manager.load(hidePrints)) success = false;
        }

        BungeeCord.getInstance().getPluginManager().registerListener(WarpSystem.getInstance(), new TabCompleterListener());

        return success;
    }

    public void save(boolean saver) {
        for(Manager manager : this.managers) {
            manager.save(saver);
        }
    }

    public boolean reload() {
        save(true);
        return load(true);
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

    public List<String> getOped() {
        return oped;
    }

    public boolean isOp(CommandSender player) {
        return this.oped.contains(player.getName());
    }
}
