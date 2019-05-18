package de.codingair.warpsystem.spigot.features.nativeportals.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.DestinationPage;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.nativeportals.Portal;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.pages.PAppearance;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.pages.PMaterial;
import de.codingair.warpsystem.spigot.features.nativeportals.managers.NativePortalManager;
import org.bukkit.entity.Player;

public class NPEditor extends Editor<Portal> {
    private Portal clone;

    public NPEditor(Player p, Portal portal, Portal clone) {
        super(p, clone, new Backup<Portal>(portal) {
            @Override
            public void applyTo(Portal clone) {
                if(clone.getDestination() != null && clone.getDestination().getId() == null) clone.setDestination(null);
                portal.apply(clone);

                clone.setVisible(false);
                clone.destroy();
                portal.setVisible(true);

                if(!NativePortalManager.getInstance().getPortals().contains(portal))
                    NativePortalManager.getInstance().addPortal(portal);
            }

            @Override
            public void cancel(Portal clone) {
                clone.setVisible(false);
                clone.destroy();
                portal.setVisible(true);
            }
        }, () -> new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem(), new PAppearance(p, clone), new PMaterial(p, clone), new DestinationPage(p, getMainTitle(), clone.getDestination()));

        this.clone = clone;
        initControllButtons();
    }

    @Override
    public boolean canFinish() {
        if(this.clone == null) return false;
        return !this.clone.getBlocks().isEmpty();
    }

    public Portal getClone() {
        return clone;
    }

    public static String getMainTitle() {
        return Editor.TITLE_COLOR + Lang.get("Native_Portals");
    }
}
