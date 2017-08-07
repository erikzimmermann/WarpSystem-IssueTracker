package de.codingair.warpsystem.remastered.managers;

import de.CodingAir.v1_6.CodingAPI.Files.ConfigFile;
import de.codingair.warpsystem.remastered.WarpSystem;
import de.codingair.warpsystem.remastered.gui.affiliations.ActionIcon;
import de.codingair.warpsystem.remastered.gui.affiliations.ActionIconHelper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class IconManager {
    private List<ActionIcon> actionIcons = new ArrayList<>();

    public void load(boolean sync) {
        if(!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    load(true);
                }
            });
        } else {
            //Load
            this.actionIcons = new ArrayList<>();

            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
            FileConfiguration config = file.getConfig();

            List<String> data = config.getStringList("Data");
            for (String s : data) {
                this.actionIcons.add(ActionIconHelper.fromString(s));
            }
        }
    }

    public void save(boolean sync) {
        if(!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), new Runnable() {
                @Override
                public void run() {
                    save(true);
                }
            });
        } else {
            //Save
            ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
            FileConfiguration config = file.getConfig();

            List<String> data = new ArrayList<>();
            for (ActionIcon icon : this.actionIcons) {
                data.add(ActionIconHelper.toString(icon));
            }

            config.set("Data", data);
            file.saveConfig();
        }
    }

    public void addActionIcon(ActionIcon icon) {
        this.actionIcons.add(icon);
    }

    public void removeActionIcon(ActionIcon icon) {
        this.actionIcons.remove(icon);
    }

    public List<ActionIcon> getActionIcons() {
        return actionIcons;
    }
}
