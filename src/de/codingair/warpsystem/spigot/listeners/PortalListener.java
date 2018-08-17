package de.codingair.warpsystem.spigot.listeners;

import de.codingair.codingapi.API;
import de.codingair.codingapi.server.events.PlayerWalkEvent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.commands.CPortal;
import de.codingair.warpsystem.spigot.features.portals.Portal;
import de.codingair.warpsystem.spigot.features.portals.PortalDeleteGUI;
import de.codingair.warpsystem.spigot.features.portals.PortalEditor;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PortalListener implements Listener {

    @EventHandler
    public void onWalk(PlayerWalkEvent e) {
        Player player = e.getPlayer();
        boolean aboutToEdit = CPortal.aboutToEdit.contains(player.getName());
        boolean aboutToDelete = CPortal.aboutToDelete.contains(player.getName());

        for(Portal portal : API.getRemovables(Portal.class)) {
            if(!portal.isRegistered()) continue;

            try {
                if(player.getWorld() == portal.getStart().getWorld() && e.getFrom().distance(portal.getStart()) > portal.getTeleportRadius() && e.getTo().distance(portal.getStart()) <= portal.getTeleportRadius()) {

                    if(aboutToEdit) {
                        CPortal.aboutToEdit.remove(player.getName());
                        new PortalEditor(player, portal).start();
                        return;
                    } else if(aboutToDelete) {
                        CPortal.aboutToDelete.remove(player.getName());
                        new PortalDeleteGUI(player, new Callback<Boolean>() {
                            @Override
                            public void accept(Boolean delete) {
                                if(delete) {
                                    WarpSystem.getInstance().getTeleportManager().getPortals().remove(portal);
                                    portal.setRunning(false);

                                    player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Successfully", new Example("ENG", "&7The portal has been &cdeleted &7successfully."), new Example("GER", "&7Das Portal wurde erfolgreich &cgelöscht&7.")));
                                } else {
                                    player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Cancel", new Example("ENG", "&7The portal has &4not &7been &cdeleted&7."), new Example("GER", "&7Das Portal wurde &4nicht &cgelöscht&7.")));
                                }
                            }
                        }).open();
                        return;
                    }

                    portal.teleportToDestination(player);
                } else if(player.getWorld() == portal.getDestination().getWorld() && e.getFrom().distance(portal.getDestination()) > portal.getTeleportRadius() && e.getTo().distance(portal.getDestination()) <= portal.getTeleportRadius()) {

                    if(aboutToEdit) {
                        CPortal.aboutToEdit.remove(player.getName());
                        new PortalEditor(player, portal).start();
                        return;
                    } else if(aboutToDelete) {
                        CPortal.aboutToDelete.remove(player.getName());
                        new PortalDeleteGUI(player, new Callback<Boolean>() {
                            @Override
                            public void accept(Boolean delete) {
                                if(delete) {
                                    WarpSystem.getInstance().getTeleportManager().getPortals().remove(portal);
                                    portal.setRunning(false);

                                    player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Successfully", new Example("ENG", "&7The portal has been &cdeleted &7successfully."), new Example("GER", "&7Das Portal wurde erfolgreich &cgelöscht&7.")));
                                } else {
                                    player.sendMessage(Lang.getPrefix() + Lang.get("Delete_Portal_Cancel", new Example("ENG", "&7The portal has &4not &7been &cdeleted&7."), new Example("GER", "&7Das Portal wurde &4nicht &cgelöscht&7.")));
                                }
                            }
                        }).open();
                        return;
                    }

                    portal.teleportToStart(player);
                }
            } catch(IllegalArgumentException ignored) {

            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        for(Portal portal : API.getRemovables(Portal.class)) {
            portal.update(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        for(PortalEditor portalEditor : API.getRemovables(e.getPlayer(), PortalEditor.class)) {
            portalEditor.exit();
        }
    }

}
