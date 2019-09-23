package de.codingair.warpsystem.spigot.features.nativeportals.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.DestinationPage;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.nativeportals.NativePortal;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.pages.PAppearance;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.pages.PMaterial;
import de.codingair.warpsystem.spigot.features.nativeportals.managers.NativePortalManager;
import org.bukkit.entity.Player;

public class NPEditor extends Editor<NativePortal> {
    private NativePortal clone;

    public NPEditor(Player p, NativePortal nativePortal, NativePortal clone) {
        super(p, clone, new Backup<NativePortal>(nativePortal) {
            @Override
            public void applyTo(NativePortal clone) {
                if(clone.getDestination() != null && clone.getDestination().getId() == null) clone.setDestination(null);
                nativePortal.apply(clone);

                clone.setVisible(false);
                clone.destroy();
                nativePortal.setVisible(true);

                if(!NativePortalManager.getInstance().getNativePortals().contains(nativePortal))
                    NativePortalManager.getInstance().addPortal(nativePortal);
            }

            @Override
            public void cancel(NativePortal clone) {
                clone.setVisible(false);
                clone.destroy();
                nativePortal.setVisible(true);
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

    public NativePortal getClone() {
        return clone;
    }

    public static String getMainTitle() {
        return Editor.TITLE_COLOR + Lang.get("Native_Portals");
    }
}
