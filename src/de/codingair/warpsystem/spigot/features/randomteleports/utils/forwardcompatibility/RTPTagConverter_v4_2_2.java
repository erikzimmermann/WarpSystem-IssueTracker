package de.codingair.warpsystem.spigot.features.randomteleports.utils.forwardcompatibility;

import de.codingair.warpsystem.spigot.api.files.TagConverter;
import de.codingair.warpsystem.spigot.base.WarpSystem;

public class RTPTagConverter_v4_2_2 extends TagConverter {
    public RTPTagConverter_v4_2_2() {
        super(WarpSystem.getInstance().getFileManager().loadFile("Config", "/", false), WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/", false));

        for(String key : super.from.getConfig().getKeys(true)) {
            if(key.contains("RandomTeleport.")) {
                String newKey = "RandomTeleport." + key.split("RandomTeleport\\.")[1];
                super.convert.put(key, newKey);
            }
        }

        if(convert()) to.saveConfig();
        WarpSystem.getInstance().getFileManager().unloadFile(super.from);
        WarpSystem.getInstance().getFileManager().unloadFile(super.to);
    }
}
