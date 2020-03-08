package de.codingair.warpsystem.spigot.features.nativeportals.managers;

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
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.nativeportals.NativePortal;
import de.codingair.warpsystem.spigot.features.nativeportals.PortalEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.commands.CNativePortals;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.DeleteGUI;
import de.codingair.warpsystem.spigot.features.nativeportals.guis.NPEditor;
import de.codingair.warpsystem.spigot.features.nativeportals.listeners.EditorListener;
import de.codingair.warpsystem.spigot.features.nativeportals.listeners.PortalListener;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NativePortalManager implements Manager {
    private List<NativePortal> nativePortals = new ArrayList<>();
    private List<Player> noTeleport = new ArrayList<>();
    private TimeList<String> goingToDelete = new TimeList<>();
    private TimeList<String> goingToEdit = new TimeList<>();

    private boolean sendMessage;

    @Override
    public boolean load() {
        ConfigFile config = WarpSystem.getInstance().getFileManager().getFile("Config");
        Object test = config.getConfig().get("WarpSystem.Send.Teleport_Message.NativePortals", null);
        if(test == null) {
            config.getConfig().set("WarpSystem.Send.Teleport_Message.GlobalWarps", true);
            config.getConfig().set("WarpSystem.Send.Teleport_Message.Warps", true);
            config.getConfig().set("WarpSystem.Send.Teleport_Message.NativePortals", true);
            config.getConfig().set("WarpSystem.Send.Teleport_Message.Portals", true);
            sendMessage = true;
        } else if(test instanceof Boolean) {
            sendMessage = (boolean) test;
        }

        Bukkit.getPluginManager().registerEvents(new EditorListener(), WarpSystem.getInstance());
        Bukkit.getPluginManager().registerEvents(new PortalListener(), WarpSystem.getInstance());
        new CNativePortals().register(WarpSystem.getInstance());

        boolean success = true;

        this.nativePortals.forEach(p -> {
            p.setVisible(false);
            p.clear();
        });
        this.nativePortals.clear();

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");

        WarpSystem.log("  > Loading NativePortals");
        int fails = 0;
        List<?> l = file.getConfig().getList("NativePortals");
        if(l != null)
            for(Object s : l) {
                NativePortal p = new NativePortal();

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
                } else if(s instanceof String) {
                    try {
                        p.read((JSON) new JSONParser().parse((String) s));
                        addPortal(p);
                    } catch(Exception e) {
                        e.printStackTrace();
                        fails++;
                        success = false;
                    }
                }
            }

        if(fails > 0) WarpSystem.log("    > " + fails + " Error(s)");
        WarpSystem.log("    ...got " + nativePortals.size() + " NativePortal(s)");

        showAll();

        return success;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        if(!saver) WarpSystem.log("  > Saving NativePortals...");

        List<JSON> data = new ArrayList<>();

        for(NativePortal nativePortal : this.nativePortals) {
            JSON json = new JSON();
            nativePortal.write(json);
            data.add(json);
        }

        if(!saver) hideAll();

        file.getConfig().set("NativePortals", data);
        file.saveConfig();

        if(!saver) WarpSystem.log("    ...saved " + data.size() + " NativePortal(s)");
    }

    @Override
    public void destroy() {
        this.goingToDelete.clear();
        this.goingToEdit.clear();
        this.noTeleport.clear();
        this.nativePortals.clear();
    }

    public void hideAll() {
        for(NativePortal nativePortal : this.nativePortals) {
            nativePortal.setVisible(false);
            nativePortal.clear();
        }
    }

    public void showAll() {
        for(NativePortal nativePortal : this.nativePortals) {
            nativePortal.setVisible(true);
        }
    }

    public List<PortalEditor> getEditors() {
        return API.getRemovables(PortalEditor.class);
    }

    public PortalEditor getEditor(Player player) {
        return API.getRemovable(player, PortalEditor.class);
    }

    public boolean isEditing(Player player) {
        return getEditor(player) != null;
    }

    private void init(NativePortal nativePortal) {
        nativePortal.getListeners().clear();
        nativePortal.getListeners().add(new de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalListener() {
            @Override
            public void onEnter(Player player) {
                if(!player.hasPermission(WarpSystem.PERMISSION_USE_NATIVE_PORTALS)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                }

                if(NativePortalManager.getInstance().isEditing(player) || API.getRemovable(player, NPEditor.class) != null) {
                    player.setVelocity(player.getLocation().getDirection().normalize().multiply(-0.8));
                    return;
                } else if(API.getRemovable(player, GUI.class) != null) return;
                else if(WarpSystem.getInstance().getTeleportManager().isTeleporting(player)) return;

                if(goingToDelete.contains(player.getName())) {
                    setGoingToDelete(player, 0);
                    noTeleport.add(player);

                    player.setVelocity(player.getLocation().getDirection().normalize().multiply(-0.8));

                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                        nativePortal.setEditMode(true);

                        new DeleteGUI(player, new Callback<Boolean>() {
                            @Override
                            public void accept(Boolean delete) {
                                if(delete) {
                                    nativePortal.clear();
                                    NativePortalManager.getInstance().getNativePortals().remove(nativePortal);
                                    player.sendMessage(Lang.getPrefix() + Lang.get("NativePortal_Deleted"));
                                } else {
                                    nativePortal.setEditMode(false);
                                    player.sendMessage(Lang.getPrefix() + Lang.get("NativePortal_Not_Deleted"));
                                }
                            }
                        }, null).open();
                        noTeleport.remove(player);
                    }, 4L);

                } else if(goingToEdit.contains(player.getName())) {
                    setGoingToEdit(player, 0);
                    noTeleport.add(player);

                    player.setVelocity(player.getLocation().getDirection().normalize().multiply(-0.8));

                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                        nativePortal.setVisible(false);
                        NativePortal clone = nativePortal.clone();
                        if(clone.getDestination() == null) clone.setDestination(new Destination());
                        clone.setEditMode(true);
                        clone.setVisible(true);

                        new NPEditor(player, nativePortal, clone).open();
                        noTeleport.remove(player);
                    }, 4L);
                } else if(!noTeleport.contains(player)) {
                    nativePortal.perform(player);
                }
            }

            @Override
            public void onLeave(Player player) {
                Teleport t = WarpSystem.getInstance().getTeleportManager().getTeleport(player);
                if(t != null && t.getDestination().equals(nativePortal.getDestination())) {
                    WarpSystem.getInstance().getTeleportManager().cancelTeleport(player);
                }
            }
        });
    }

    public void addPortal(NativePortal nativePortal) {
        init(nativePortal);
        this.nativePortals.add(nativePortal);
    }

    public List<NativePortal> getNativePortals() {
        return nativePortals;
    }

    public static NativePortalManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.NATIVE_PORTALS);
    }

    public TimeList<String> getGoingToDelete() {
        return goingToDelete;
    }

    public void setGoingToDelete(Player player, int time) {
        if(time == 0) {
            this.goingToDelete.remove(player.getName());
        } else {
            if(this.goingToDelete.contains(player.getName())) this.goingToDelete.setExpire(player.getName(), time);
            else this.goingToDelete.add(player.getName(), time);
        }
    }

    public TimeList<String> getGoingToEdit() {
        return goingToEdit;
    }

    public void setGoingToEdit(Player player, int time) {
        if(time == 0) {
            this.goingToEdit.remove(player.getName());
        } else {
            if(this.goingToEdit.contains(player.getName())) this.goingToEdit.setExpire(player.getName(), time);
            else this.goingToEdit.add(player.getName(), time);
        }
    }

    public List<Player> getNoTeleport() {
        return noTeleport;
    }

    public boolean isSendMessage() {
        return sendMessage;
    }
}
