package de.codingair.warpsystem.spigot.features.warps.guis.editor;

import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.DestinationPage;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.warps.guis.editor.pages.PAppearance;
import de.codingair.warpsystem.spigot.features.warps.guis.editor.pages.PFunctions;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.entity.Player;

public class GEditor extends de.codingair.warpsystem.spigot.base.guis.editor.Editor<Icon> {
    public GEditor(Player p, Icon icon, Icon clone) {
        super(p, clone, new Backup<Icon>(icon) {
            @Override
            public void applyTo(Icon clone) {
                icon.apply(clone);

                if(icon.isPage() && !IconManager.getInstance().existsPage(icon.getName())) {
                    IconManager.getInstance().getIcons().add(icon);
                } else if(!icon.isPage() && !IconManager.getInstance().existsIcon(icon.getName())) {
                    IconManager.getInstance().getIcons().add(icon);
                }
            }

            @Override
            public void cancel(Icon value) {
            }
        }, clone::getItem, new PAppearance(p, clone), new PFunctions(p, clone), icon.isPage() ? null : new DestinationPage(p, "§c§n" + Lang.get("Item_Editing"), clone.getAction(WarpAction.class).getValue(), Origin.WarpIcon));
    }
}
