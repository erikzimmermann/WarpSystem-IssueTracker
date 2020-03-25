package de.codingair.warpsystem.spigot.base.ad.features;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.ad.features.utils.PremiumFeature;

public class TempWarpKeys implements PremiumFeature {
    @Override
    public boolean disable() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        file.getConfig().set("WarpSystem.TempWarps.Keys", false);
        file.saveConfig();
        return true;
    }

    @Override
    public String getName() {
        return "TempWarp keys";
    }

    @Override
    public String[] getSuccessMessage() {
        return null;
    }
}