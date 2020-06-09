package de.codingair.warpsystem.spigot.features.randomteleports.utils.forwardcompatibility;

import de.codingair.warpsystem.spigot.api.files.TagConverter;
import de.codingair.warpsystem.spigot.base.WarpSystem;

public class RTPTagConverter_v4_2_6 extends TagConverter {
    public RTPTagConverter_v4_2_6() {
        super(WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/", false), WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/", false));

        if(super.from.getConfig().getInt("RandomTeleport.Range.Min", -1) >= 0) {
            convert.put("RandomTeleport.Range.Min", "RandomTeleport.Worlds.Default.min_range");
            convert.put("RandomTeleport.Range.Max", "RandomTeleport.Worlds.Default.max_range");
        }

        if(convert()) to.saveConfig();
        WarpSystem.getInstance().getFileManager().unloadFile(super.from);
        WarpSystem.getInstance().getFileManager().unloadFile(super.to);
    }
}
