package de.codingair.warpsystem.spigot.features.playerwarps.utils.forwardcompatibility;

import de.codingair.warpsystem.spigot.api.files.TagConverter;
import de.codingair.warpsystem.spigot.base.WarpSystem;

public class PlayerWarpTagConverter_v4_2_2 extends TagConverter {
    public PlayerWarpTagConverter_v4_2_2() {
        super(WarpSystem.getInstance().getFileManager().loadFile("Config", "/", false), WarpSystem.getInstance().getFileManager().loadFile("PlayerWarpConfig", "/", false));

        for(String key : super.from.getConfig().getKeys(true)) {
            if(key.contains("PlayerWarps.")) {
                String newKey = "PlayerWarps." + key.split("PlayerWarps\\.")[1];
                super.convert.put(key, newKey);
            }
        }

        convert();
        to.saveConfig();
        WarpSystem.getInstance().getFileManager().unloadFile(super.from);
        WarpSystem.getInstance().getFileManager().unloadFile(super.to);
    }
}
