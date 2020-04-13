package de.codingair.warpsystem.spigot.features.playerwarps.imports;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarpData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EssentialsImportFilter implements ImportFilter {
    @Override
    public List<PlayerWarp> importAll() {
        try {
            List<PlayerWarp> data = new ArrayList<>();

            File target = new File(WarpSystem.getInstance().getDataFolder().getParent() + "/Essentials/userdata/");
            if(!target.exists()) return null;

            for(File w : target.listFiles()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(w);

                PlayerWarpData pwd = null;
                for(String key : config.getKeys(true)) {
                    if(pwd == null) {
                        pwd = new PlayerWarpData();
                        pwd.getOwner().setName(config.getString("lastAccountName"));
                        pwd.getOwner().setId(UUID.fromString(w.getName().split("\\.")[0]));
                        pwd.setServer(WarpSystem.getInstance().getCurrentServer());
                    }

                    if(key.startsWith("homes.")) {
                        String s = key.replaceFirst("homes\\.", "");

                        if(s.contains(".")) {
                            String[] fileData = s.split("\\.");
                            if(pwd.getName() == null) pwd.setName(fileData[0]);

                            if(fileData[1].equals("world")) pwd.setWorld(config.getString(key));
                            else if(fileData[1].equals("x")) pwd.setX(config.getDouble(key));
                            else if(fileData[1].equals("y")) pwd.setY(config.getDouble(key));
                            else if(fileData[1].equals("z")) pwd.setZ(config.getDouble(key));
                            else if(fileData[1].equals("yaw")) pwd.setYaw((float) config.getDouble(key));
                            else if(fileData[1].equals("pitch")) {
                                pwd.setPitch((float) config.getDouble(key));
                                pwd.setBorn(System.currentTimeMillis());

                                PlayerWarp pw = new PlayerWarp();
                                pw.setData(pwd);
                                pw.resetItem();
                                data.add(pw);
                                pwd = null;
                            }
                        }
                    }
                }
            }

            return data;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
