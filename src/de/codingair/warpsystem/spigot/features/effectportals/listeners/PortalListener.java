package de.codingair.warpsystem.spigot.features.effectportals.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.effectportals.PortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.commands.CPortal;
import de.codingair.warpsystem.spigot.features.effectportals.guis.PortalDeleteGUI;
import de.codingair.warpsystem.spigot.features.effectportals.guis.editor.EffectPortalEditor;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import de.codingair.warpsystem.spigot.features.effectportals.utils.PortalDestinationAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PortalListener implements Listener {

    @EventHandler
    public void onWalk(PlayerWalkEvent e) {
        PortalManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);

        Player player = e.getPlayer();

        boolean aboutToEdit = CPortal.aboutToEdit.contains(player.getName());
        boolean aboutToDelete = CPortal.aboutToDelete.contains(player.getName());

        for(EffectPortal effectPortal : API.getRemovables(EffectPortal.class)) {
            if(!effectPortal.isRegistered()) continue;

            if(e.getFrom().getWorld() == effectPortal.getStart().getWorld() && e.getTo().getWorld() == effectPortal.getStart().getWorld() && effectPortal.entered(e.getPlayer())) {
                if(aboutToEdit) {
                    CPortal.aboutToEdit.remove(player.getName());
                    new EffectPortalEditor(player, effectPortal).open();
//                    new PortalEditor(player, effectPortal).start();
                    return;
                } else if(aboutToDelete) {
                    CPortal.aboutToDelete.remove(player.getName());
                    new PortalDeleteGUI(player, new Callback<Boolean>() {
                        @Override
                        public void accept(Boolean delete) {
                            if(delete) {
                                manager.getEffectPortals().remove(effectPortal);
                                effectPortal.setRunning(false);

                                player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Successfully"));
                            } else {
                                player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Cancel"));
                            }
                        }
                    }).open();
                    return;
                }

                if(!player.hasPermission(WarpSystem.PERMISSION_USE_PORTALS)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                } else effectPortal.teleportToDestination(player);
            } else if(effectPortal.getDestination() != null && effectPortal.getDestination().getAdapter() instanceof PortalDestinationAdapter) {
                Location destination = effectPortal.getDestination().buildLocation();

                if(e.getFrom().getWorld() == destination.getWorld() && e.getTo().getWorld() == destination.getWorld() && effectPortal.entered(e.getPlayer())) {
                    if(aboutToEdit) {
                        CPortal.aboutToEdit.remove(player.getName());
                        new EffectPortalEditor(player, effectPortal).open();
//                        new PortalEditor(player, effectPortal).start();
                        return;
                    } else if(aboutToDelete) {
                        CPortal.aboutToDelete.remove(player.getName());
                        new PortalDeleteGUI(player, new Callback<Boolean>() {
                            @Override
                            public void accept(Boolean delete) {
                                if(delete) {
                                    manager.getEffectPortals().remove(effectPortal);
                                    effectPortal.setRunning(false);

                                    player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Successfully"));
                                } else {
                                    player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Cancel"));
                                }
                            }
                        }).open();
                        return;
                    }

                    if(!player.hasPermission(WarpSystem.PERMISSION_USE_PORTALS)) {
                        player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                        return;
                    } else effectPortal.teleportToStart(player);
                }
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
        for(PortalEditor portalEditor : API.getRemovables(e.getPlayer(), PortalEditor.class)) {
            portalEditor.exit();
        }

        for(EffectPortal effectPortal : API.getRemovables(EffectPortal.class)) {
            effectPortal.remove(e.getPlayer());
        }
    }

}
