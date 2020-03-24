package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.GlobalLocationAdapter;
import de.codingair.warpsystem.spigot.features.tempwarps.utils.TempWarp;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TempWarpAdapter {

    public static List<PlayerWarp> convertTempWarps(boolean clear) {
        List<PlayerWarp> warps = new ArrayList<>();

        File f = new File(WarpSystem.getInstance().getDataFolder(), "Memory/TempWarps.yml");

        if(f.exists()) {
            WarpSystem.getInstance().getFileManager().loadFile("TempWarps", "Memory/");
            ConfigFile configFile = WarpSystem.getInstance().getFileManager().getFile("TempWarps");
            FileConfiguration config = configFile.getConfig();

            for(String s : config.getStringList("Warps")) {
                PlayerWarp warp = convert(TempWarp.getByJSONString(s));
                if(warp != null) warps.add(warp);
            }

            WarpSystem.getInstance().getFileManager().unloadFile(configFile);

            if(clear) {
                f.delete();
            }
        }

        return warps;
    }

    private static PlayerWarp convert(TempWarp warp) {
        PlayerWarp pw = new PlayerWarp();

        pw.getOwner().setName(warp.getLastKnownName());
        pw.getOwner().setId(warp.getOwner());

        pw.resetItem();

        pw.addAction(new WarpAction(new Destination(new GlobalLocationAdapter(WarpSystem.getInstance().getCurrentServer(), warp.getLocation()))));
        pw.setName(warp.getName());

        pw.setBorn(warp.getBornDate().getTime());
        pw.setStarted(warp.getStartDate().getTime());
        pw.setTime(warp.getEndDate().getTime() - pw.getStarted());

        pw.setPublic(warp.isPublic());
        pw.setTeleportMessage(warp.getMessage());
        pw.setTeleportCosts(warp.getTeleportCosts());
        pw.setCreatorKey(warp.getCreatorKey());

        pw.setInactiveSales(warp.getInactiveSales());

        return pw;
    }

}
