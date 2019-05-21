package de.codingair.warpsystem.spigot.base.guis.options;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.ShowIcon;
import de.codingair.warpsystem.spigot.base.guis.options.pages.PGeneral;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.options.GeneralOptions;
import de.codingair.warpsystem.spigot.base.utils.options.OptionBundle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GOptions extends Editor<OptionBundle> {
    public GOptions(Player p, OptionBundle bundle, OptionBundle clone) {
        super(p, clone, new Backup<OptionBundle>(WarpSystem.getInstance().getOptions()) {
            @Override
            public void applyTo(OptionBundle value) {
                bundle.apply(value);
                bundle.write();
            }

            @Override
            public void cancel(OptionBundle value) {

            }
        }, new ShowIcon() {
            @Override
            public ItemStack buildIcon() {
                return new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem();
            }
        }, new PGeneral(p, clone.getOptions(GeneralOptions.class)));
    }

    @Override
    public void updatePageItems() {
        super.updatePageItems();

        setItem(2, new ItemBuilder(XMaterial.ENDER_PEARL).setName(Editor.ITEM_TITLE_COLOR + Lang.get("WarpGUI")).setLore("§7coming soon...").getItem());
//        setItem(3, new ItemBuilder(XMaterial.END_PORTAL_FRAME).setName(Editor.ITEM_TITLE_COLOR + Lang.get("NativePortals")).setLore("§7coming soon...").getItem());
//        setItem(4, new ItemBuilder(XMaterial.BLAZE_ROD).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Effect_Portals")).setLore("§7coming soon...").getItem());
//        setItem(5, new ItemBuilder(XMaterial.OAK_SIGN).setName(Editor.ITEM_TITLE_COLOR + Lang.get("WarpSigns")).setLore("§7coming soon...").getItem());
        setItem(3, new ItemBuilder(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE).setName("§7...").getItem());
    }
}
