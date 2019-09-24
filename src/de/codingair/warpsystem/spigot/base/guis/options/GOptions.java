package de.codingair.warpsystem.spigot.base.guis.options;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.ShowIcon;
import de.codingair.warpsystem.spigot.base.guis.options.pages.PGeneral;
import de.codingair.warpsystem.spigot.base.guis.options.pages.PWarpGUI;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.options.OptionBundle;
import de.codingair.warpsystem.spigot.base.utils.options.specific.GeneralOptions;
import de.codingair.warpsystem.spigot.base.utils.options.specific.WarpGUIOptions;
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
        }, new PGeneral(p, clone.getOptions(GeneralOptions.class)), new PWarpGUI(p, clone.getOptions(WarpGUIOptions.class)));

        old = bundle;
        update = clone;
    }

    @Override
    public String finishButtonNameAddition() {
        if(old == null || update == null) return "";
        return reloadRequired(old, update) ? " §8(§7Reload§8)" : "";
    }

    @Override
    public void updatePageItems() {
        super.updatePageItems();

//        setItem(3, new ItemBuilder(XMaterial.END_PORTAL_FRAME).setName(Editor.ITEM_TITLE_COLOR + Lang.get("NativePortals")).setLore("§7coming soon...").getItem());
//        setItem(4, new ItemBuilder(XMaterial.BLAZE_ROD).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Effect_Portals")).setLore("§7coming soon...").getItem());
//        setItem(5, new ItemBuilder(XMaterial.OAK_SIGN).setName(Editor.ITEM_TITLE_COLOR + Lang.get("WarpSigns")).setLore("§7coming soon...").getItem());
        setItem(3, new ItemBuilder(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE).setName("§7...").getItem());
    }

    private static boolean reloadRequired(OptionBundle old, OptionBundle update) {
        if(old.getOptions(WarpGUIOptions.class).getEnabled().getValue() != update.getOptions(WarpGUIOptions.class).getEnabled().getValue()) return true;

        return false;
    }
}
