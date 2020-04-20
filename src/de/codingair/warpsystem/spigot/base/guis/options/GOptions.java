package de.codingair.warpsystem.spigot.base.guis.options;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.ShowIcon;
import de.codingair.warpsystem.spigot.base.guis.options.pages.PGeneral;
import de.codingair.warpsystem.spigot.base.guis.options.pages.PPortals;
import de.codingair.warpsystem.spigot.base.guis.options.pages.PWarpGUI;
import de.codingair.warpsystem.spigot.base.guis.options.pages.PWarpSigns;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.options.OptionBundle;
import de.codingair.warpsystem.spigot.base.utils.options.specific.GeneralOptions;
import de.codingair.warpsystem.spigot.base.utils.options.specific.PortalOptions;
import de.codingair.warpsystem.spigot.base.utils.options.specific.WarpGUIOptions;
import de.codingair.warpsystem.spigot.base.utils.options.specific.WarpSignOptions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GOptions extends Editor<OptionBundle> {
    private OptionBundle old, update;

    GOptions(Player p, OptionBundle bundle, OptionBundle clone) {
        super(p, clone, new Backup<OptionBundle>(WarpSystem.getInstance().getOptions()) {
            @Override
            public void applyTo(OptionBundle value) {
                boolean reload = reloadRequired(bundle, value);

                bundle.apply(value);
                bundle.write();

                if(reload) {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Plugin_Reloading"));
                    WarpSystem.getInstance().reload(true);
                    p.sendMessage(Lang.getPrefix() + Lang.get("Success_Plugin_Reloaded"));
                }
            }

            @Override
            public void cancel(OptionBundle value) {

            }
        }, new ShowIcon() {
            @Override
            public ItemStack buildIcon() {
                return new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem();
            }
        }, new PGeneral(p, clone.getOptions(GeneralOptions.class)), new PWarpGUI(p, clone.getOptions(WarpGUIOptions.class)), new PWarpSigns(p, clone.getOptions(WarpSignOptions.class)), new PPortals(p, clone.getOptions(PortalOptions.class)));

        old = bundle;
        update = clone;
    }

    private static boolean reloadRequired(OptionBundle old, OptionBundle update) {
        if(old.getOptions(WarpGUIOptions.class).getEnabled().getValue() != update.getOptions(WarpGUIOptions.class).getEnabled().getValue()) return true;
        if(old.getOptions(WarpSignOptions.class).getEnabled().getValue() != update.getOptions(WarpSignOptions.class).getEnabled().getValue()) return true;
        if(old.getOptions(PortalOptions.class).getEnabled().getValue() != update.getOptions(PortalOptions.class).getEnabled().getValue()) return true;

        return false;
    }

    @Override
    public String finishButtonNameAddition() {
        if(old == null || update == null) return "";
        return reloadRequired(old, update) ? " §8(§7Reload§8)" : "";
    }
}
