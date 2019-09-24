package de.codingair.warpsystem.spigot.base.guis.options;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.ShowIcon;
import de.codingair.warpsystem.spigot.base.guis.options.pages.*;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.options.OptionBundle;
import de.codingair.warpsystem.spigot.base.utils.options.specific.*;
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
        }, new PGeneral(p, clone.getOptions(GeneralOptions.class)), new PWarpGUI(p, clone.getOptions(WarpGUIOptions.class)), new PNativePortals(p, clone.getOptions(NativePortalsOptions.class)), new PEffectPortals(p, clone.getOptions(EffectPortalsOptions.class)), new PWarpSigns(p, clone.getOptions(WarpSignOptions.class)));

        old = bundle;
        update = clone;
    }

    @Override
    public String finishButtonNameAddition() {
        if(old == null || update == null) return "";
        return reloadRequired(old, update) ? " §8(§7Reload§8)" : "";
    }

    private static boolean reloadRequired(OptionBundle old, OptionBundle update) {
        if(old.getOptions(WarpGUIOptions.class).getEnabled().getValue() != update.getOptions(WarpGUIOptions.class).getEnabled().getValue()) return true;
        if(old.getOptions(WarpSignOptions.class).getEnabled().getValue() != update.getOptions(WarpSignOptions.class).getEnabled().getValue()) return true;
        if(old.getOptions(EffectPortalsOptions.class).getEnabled().getValue() != update.getOptions(EffectPortalsOptions.class).getEnabled().getValue()) return true;
        if(old.getOptions(NativePortalsOptions.class).getEnabled().getValue() != update.getOptions(NativePortalsOptions.class).getEnabled().getValue()) return true;

        return false;
    }
}
