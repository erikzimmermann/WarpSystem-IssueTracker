package de.codingair.warpsystem.spigot.base.ad.features;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.ad.features.utils.PremiumFeature;
import de.codingair.warpsystem.spigot.base.language.Lang;

public class TeleportCommands implements PremiumFeature {
    @Override
    public boolean disable() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        file.getConfig().set("WarpSystem.TeleportCommands.Tp", false);
        file.getConfig().set("WarpSystem.TeleportCommands.Tpa", false);
        file.getConfig().set("WarpSystem.TeleportCommands.TpaAll", false);
        file.getConfig().set("WarpSystem.TeleportCommands.TpaHere", false);
        file.getConfig().set("WarpSystem.TeleportCommands.TpAll", false);
        file.getConfig().set("WarpSystem.TeleportCommands.TpaToggle", false);
        file.getConfig().set("WarpSystem.TeleportCommands.TpToggle", false);
        file.getConfig().set("WarpSystem.TeleportCommands.Back.Enabled", false);
        file.saveConfig();
        return true;
    }

    @Override
    public String getName() {
        return "All Teleport Commands";
    }

    @Override
    public String[] getSuccessMessage() {
        return new String[]{
                Lang.getPrefix() + "§7Following §6teleport commands §7were §cdisabled§7:",
                "    §7- Back",
                "    §7- Teleport",
                "    §7- TpAll",
                "    §7- TpHere",
                "    §7- TpToggle",
                "    §7- Tpa",
                "    §7- TpaAll",
                "    §7- TpaHere",
                "    §7- TpaToggle",
        };
    }
}