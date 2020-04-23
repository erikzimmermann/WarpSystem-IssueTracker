package de.codingair.warpsystem.spigot.features.portals.managers;

import de.codingair.codingapi.API;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.player.gui.inventory.gui.GUI;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.codingapi.tools.time.TimeList;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Teleport;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.portals.commands.CPortals;
import de.codingair.warpsystem.spigot.features.portals.guis.DeleteGUI;
import de.codingair.warpsystem.spigot.features.portals.guis.PortalEditor;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.PortalBlockEditor;
import de.codingair.warpsystem.spigot.features.portals.listeners.EditorListener;
import de.codingair.warpsystem.spigot.features.portals.old.EffectPortal;
import de.codingair.warpsystem.spigot.features.portals.old.nativeportals.NativePortal;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import de.codingair.warpsystem.spigot.features.portals.utils.PortalListener;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PortalManager implements Manager {
    private List<Portal> portals = new ArrayList<>();
    private List<Player> noTeleport = new ArrayList<>();
    private TimeList<String> goingToDelete = new TimeList<>();
    private TimeList<String> goingToEdit = new TimeList<>();

    private double maxParticleDistance;
    private long hologramUpdateInterval;

    public static PortalManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
    }

    @Override
    public boolean load(boolean loader) {
        WarpSystem.log("  > Loading Portals");

        Bukkit.getPluginManager().registerEvents(new EditorListener(), WarpSystem.getInstance());
        Bukkit.getPluginManager().registerEvents(new de.codingair.warpsystem.spigot.features.portals.listeners.PortalListener(), WarpSystem.getInstance());

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        this.maxParticleDistance = file.getConfig().getDouble("WarpSystem.Portals.ParticleDistance", 70);
        this.hologramUpdateInterval = PlayerWarpManager.convertFromTimeFormat(file.getConfig().getString("WarpSystem.Portals.HologramUpdateInterval", "1m"));

        new CPortals().register(WarpSystem.getInstance());

        this.portals.forEach(p -> {
            p.setVisible(false);
            p.destroy();
        });
        this.portals.clear();

        file = WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        boolean success = importOld();

        int fails = 0;
        List<?> l = file.getConfig().getList("PortalsV2");
        if(l != null)
            for(Object s : l) {
                Portal p = new Portal();

                if(s instanceof Map) {
                    try {
                        JSON json = new JSON((Map<?, ?>) s);
                        p.read(json);
                        addPortal(p);
                    } catch(Exception e) {
                        e.printStackTrace();
                        fails++;
                        success = false;
                    }
                }
            }

        if(fails > 0) WarpSystem.log("    > " + fails + " Error(s)");
        WarpSystem.log("    ...got " + portals.size() + " Portal(s)");

        showAll();

        return success;
    }

    @Override
    public void save(boolean saver) {
        if(!saver) WarpSystem.log("  > Saving Portals");

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        List<JSON> data = new ArrayList<>();

        for(Portal portal : this.portals) {
            JSON json = new JSON();
            portal.write(json);
            data.add(json);
        }

        if(!saver) hideAll();

        file.getConfig().set("PortalsV2", data);
        file.saveConfig();

        if(!saver) WarpSystem.log("    ...saved " + data.size() + " Portal(s)");
    }

    @Override
    public void destroy() {
        for(Portal portal : this.portals) {
            portal.destroy();
        }
        this.portals.clear();
    }

    public String checkName(String name) {
        if(!existsPortal(name)) return name;

        int num = 1;

        name = name.replaceAll("\\p{Blank}\\([0-9]{1,5}?\\)\\z", "");
        name += " (" + num++ + ")";

        while(existsPortal(name)) {
            name = name.replaceAll("\\p{Blank}\\([0-9]{1,5}?\\)\\z", "");
            name += " (" + num++ + ")";
        }

        return name;
    }

    public void hideAll() {
        for(Portal portal : this.portals) {
            portal.setVisible(false);
        }
    }

    public void showAll() {
        for(Portal portal : this.portals) {
            portal.setVisible(true);
        }
    }

    private void initListener(Portal portal) {
        portal.getListeners().clear();
        portal.getListeners().add(new PortalListener() {
            @Override
            public void onEnter(Player player) {
                if(WarpSystem.hasPermission(player, WarpSystem.PERMISSION_MODIFY_PORTALS)) {
                    if(PortalManager.getInstance().isEditing(player) || API.getRemovable(player, PortalEditor.class) != null) {
                        player.setVelocity(player.getLocation().getDirection().normalize().multiply(-0.8));
                        return;
                    } else if(API.getRemovable(player, GUI.class) != null) return;
                    else if(WarpSystem.getInstance().getTeleportManager().isTeleporting(player)) return;

                    if(goingToDelete.contains(player.getName())) {
                        setGoingToDelete(player, 0);
                        noTeleport.add(player);

                        player.setVelocity(player.getLocation().getDirection().normalize().multiply(-0.5));

                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                            portal.setEditMode(true);

                            new DeleteGUI(player, new Callback<Boolean>() {
                                @Override
                                public void accept(Boolean delete) {
                                    if(delete) {
                                        portal.destroy();
                                        PortalManager.getInstance().getPortals().remove(portal);
                                        player.sendMessage(Lang.getPrefix() + Lang.get("Portal_Deleted"));
                                    } else {
                                        portal.setEditMode(false);
                                        player.sendMessage(Lang.getPrefix() + Lang.get("Portal_Not_Deleted"));
                                    }
                                }
                            }, null).open();
                            noTeleport.remove(player);
                        }, 4L);
                        return;
                    } else if(goingToEdit.contains(player.getName())) {
                        setGoingToEdit(player, 0);
                        noTeleport.add(player);

                        player.setVelocity(player.getLocation().getDirection().normalize().multiply(-0.5));

                        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                            portal.setVisible(false);
                            Portal clone = portal.clone();
                            clone.createDestinationIfAbsent().createTeleportSoundIfAbsent();
                            portal.setEditMode(true);
                            clone.setEditMode(true);
                            clone.setVisible(true);

                            new PortalEditor(player, portal, clone).open();
                            noTeleport.remove(player);
                        }, 4L);
                        return;
                    }
                }

                if(!WarpSystem.hasPermission(player, WarpSystem.PERMISSION_USE_PORTALS)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                }

                if(!noTeleport.contains(player)) {
                    portal.perform(player);
                }
            }

            @Override
            public void onLeave(Player player) {
                Teleport t = WarpSystem.getInstance().getTeleportManager().getTeleport(player);
                if(t != null && t.getDestination().equals(portal.getDestination())) {
                    WarpSystem.getInstance().getTeleportManager().cancelTeleport(player);
                }
            }
        });
    }

    public void addPortal(Portal portal) {
        initListener(portal);
        this.portals.add(portal);
    }

    public void setGoingToDelete(Player player, int time) {
        if(time == 0) {
            this.goingToDelete.remove(player.getName());
        } else {
            if(this.goingToDelete.contains(player.getName())) this.goingToDelete.setExpire(player.getName(), time);
            else this.goingToDelete.add(player.getName(), time);
        }
    }

    public void setGoingToEdit(Player player, int time) {
        if(time == 0) {
            this.goingToEdit.remove(player.getName());
        } else {
            if(this.goingToEdit.contains(player.getName())) this.goingToEdit.setExpire(player.getName(), time);
            else this.goingToEdit.add(player.getName(), time);
        }
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public boolean existsPortal(String name) {
        return getPortal(name) != null;
    }

    public Portal getPortal(String name) {
        if(name == null) return null;
        name = ChatColor.stripColor(name).toLowerCase();

        for(Portal portal : this.portals) {
            if(ChatColor.stripColor(portal.getDisplayName()).toLowerCase().equals(name)) return portal;
        }

        return null;
    }

    public PortalBlockEditor getEditor(Player player) {
        return API.getRemovable(player, PortalBlockEditor.class);
    }

    public boolean isEditing(Player player) {
        return getEditor(player) != null;
    }

    public List<Player> getNoTeleport() {
        return noTeleport;
    }

    private boolean importEffectPortals() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        List<EffectPortal> effectPortals = new ArrayList<>();
        boolean success = true;

        List<?> l = file.getConfig().getList("Portals");
        if(l != null)
            for(Object s : l) {
                EffectPortal effectPortal = new EffectPortal();
                EffectPortal link = new EffectPortal();

                if(s instanceof Map) {
                    try {
                        JSON json = new JSON((Map<?, ?>) s);
                        effectPortal.read(json, link);
                        effectPortals.add(effectPortal);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                } else if(s instanceof String) {
                    try {
                        effectPortal.read((JSON) new JSONParser().parse((String) s), link);
                        effectPortals.add(effectPortal);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                }

                if(link.name != null) effectPortals.add(link);
            }

        List<EffectPortal> temp = new ArrayList<>(effectPortals);
        for(EffectPortal p : temp) {
            if(p.linkHelper == null) continue;

            for(EffectPortal ep : effectPortals) {
                if(p.linkHelper.equals(ep.location)) {
                    ep.link = p;
                    p.link = ep;

                    ep.linkHelper = null;
                    p.linkHelper = null;

                    if(!p.useLink && !ep.useLink) p.useLink = true;
                    break;
                }
            }
        }
        temp.clear();

        for(EffectPortal effectPortal : effectPortals) {
            Portal portal = effectPortal.convert();

            String name = checkName(portal.getDisplayName());
            if(!name.equals(portal.getDisplayName())) {
                portal.setDisplayName(name);

                if(effectPortal.link != null) {
                    Portal other = getPortal(effectPortal.link.name);

                    if(other != null) other.getDestination().setId(name);
                    else effectPortal.link.getDestination().setId(name);
                }
            }

            addPortal(portal);
            effectPortal.destroy();
        }

        if(!effectPortals.isEmpty()) WarpSystem.log("    ...imported " + effectPortals.size() + " EffectPortal(s)");
        effectPortals.clear();
        file.getConfig().set("Portals", null);
        file.saveConfig();

        return success;
    }

    private boolean importNativePortals() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        List<NativePortal> portals = new ArrayList<>();
        boolean success = true;

        List<?> l = file.getConfig().getList("NativePortals");
        if(l != null)
            for(Object s : l) {
                NativePortal p = new NativePortal();

                if(s instanceof Map) {
                    try {
                        JSON json = new JSON((Map<?, ?>) s);
                        p.read(json);
                        portals.add(p);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                } else if(s instanceof String) {
                    try {
                        p.read((JSON) new JSONParser().parse((String) s));
                        portals.add(p);
                    } catch(Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                }
            }

        for(NativePortal portal : portals) {
            Portal p = portal.convert();
            p.setDisplayName(checkName(p.getDisplayName()));
            addPortal(p);
            portal.destroy();
        }

        if(!portals.isEmpty()) WarpSystem.log("    ...imported " + portals.size() + " NativePortals(s)");
        portals.clear();
        file.getConfig().set("NativePortals", null);
        file.saveConfig();

        return success;
    }

    private boolean importOld() {
        return importEffectPortals() && importNativePortals();
    }

    public double getMaxParticleDistance() {
        return maxParticleDistance;
    }

    public long getHologramUpdateInterval() {
        return hologramUpdateInterval;
    }
}
