package de.codingair.warpsystem.spigot.features.effectportals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.effectportals.EffectPortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.commands.CPortal;
import de.codingair.warpsystem.spigot.features.effectportals.guis.PortalDeleteGUI;
import de.codingair.warpsystem.spigot.features.effectportals.managers.EffectPortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PortalListener implements Listener {

    @EventHandler
    public void onWalk(PlayerWalkEvent e) {
        Player player = e.getPlayer();

        boolean aboutToEdit = CPortal.aboutToEdit.contains(player.getName());
        boolean aboutToDelete = CPortal.aboutToDelete.contains(player.getName());

        for(EffectPortal effectPortal : EffectPortalManager.getInstance().getEffectPortals()) {
            if(effectPortal.entered(e.getPlayer(), e.getFrom(), e.getTo())) {
                if(aboutToEdit) {
                    CPortal.aboutToEdit.remove(player.getName());
                    if(effectPortal.useLink()) new EffectPortalEditor(player, effectPortal.getLink()).start();
                    else new EffectPortalEditor(player, effectPortal).start();
                    return;
                } else if(aboutToDelete) {
                    CPortal.aboutToDelete.remove(player.getName());
                    new PortalDeleteGUI(player, new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean delete) {
                            if(delete) {

                                if(effectPortal.useLink()) effectPortal.getLink().unregister();
                                else effectPortal.unregister();

                                player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Successfully"));
                            } else {
                                player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Cancel"));
                            }
                        }
                    }).open();
                    return;
                }

                if(!WarpSystem.hasPermission(player, WarpSystem.PERMISSION_USE_PORTALS)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                } else effectPortal.perform(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        for(EffectPortal effectPortal : API.getRemovables(EffectPortal.class)) {
            effectPortal.add(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        for(EffectPortalEditor effectPortalEditor : API.getRemovables(e.getPlayer(), EffectPortalEditor.class)) {
            effectPortalEditor.exit();
        }

        for(EffectPortal effectPortal : API.getRemovables(EffectPortal.class)) {
            effectPortal.remove(e.getPlayer());
        }
    }

}
