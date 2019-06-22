package de.codingair.warpsystem.spigot.features.effectportals.guis.editor;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.DestinationPage;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.effectportals.guis.editor.pages.OptionPage;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.entity.Player;

public class EffectPortalEditor extends Editor<EffectPortal> {
    public EffectPortalEditor(Player p, EffectPortal portal) {
        this(p, (EffectPortal) new EffectPortal(portal).addAction(new WarpAction(new Destination()), false), portal);
        portal.setRunning(false);
    }

    private EffectPortalEditor(Player p, EffectPortal clone, EffectPortal portal) {
        super(p, clone, new Backup<EffectPortal>(portal) {
            @Override
            public void applyTo(EffectPortal value) {
                if(!PortalManager.getInstance().getEffectPortals().contains(portal)) {
                    PortalManager.getInstance().getEffectPortals().add(portal);
                }

                portal.apply(value);
            }

            @Override
            public void cancel(EffectPortal value) {
                value.destroy();
                portal.checkActionList();
            }
        }, () -> new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem(),
                new DestinationPage(p, getMainTitle(), clone.getDestination()),
                new OptionPage(p, clone));
    }

    public static String getMainTitle() {
        return Editor.TITLE_COLOR + Lang.get("Effect_Portals");
    }
}
