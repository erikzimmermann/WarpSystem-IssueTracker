package de.codingair.warpsystem.spigot.features.shortcuts.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.tools.JSON.JSONObject;
import de.codingair.codingapi.tools.JSON.JSONParser;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.BungeeFeature;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CommandAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.shortcuts.commands.CShortcuts;
import de.codingair.warpsystem.spigot.features.shortcuts.commands.ShortcutExecutor;
import de.codingair.warpsystem.spigot.features.shortcuts.listeners.ShortcutListener;
import de.codingair.warpsystem.spigot.features.shortcuts.listeners.ShortcutPacketListener;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShortcutManager implements Manager, BungeeFeature {
    private List<Shortcut> shortcuts = new ArrayList<>();
    private HashMap<Shortcut, ShortcutExecutor> executors = new HashMap<>();
    private ShortcutPacketListener listener;

    @Override
    public boolean load() {
        WarpSystem.getInstance().getBungeeFeatureList().add(this);

        if(WarpSystem.getInstance().getFileManager().getFile("Shortcuts") == null) WarpSystem.getInstance().getFileManager().loadFile("Shortcuts", "/Memory/");

        this.shortcuts.clear();

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Shortcuts");
        FileConfiguration config = file.getConfig();

        WarpSystem.log("  > Loading Shortcuts");

        for(String key : config.getKeys(false)) {
            if(key.equals("Shortcuts")) continue;
            String dest = config.getString(key + ".Destination");

            //Old
            String warpId = config.getString(key + ".WarpId", null);
            String globalWarp = config.getString(key + ".GlobalWarp", null);

            Destination destination;
            if(dest != null) {
                destination = new Destination(dest);
            } else if(warpId != null) {
                destination = new Destination(warpId, DestinationType.SimpleWarp);
            } else if(globalWarp != null) {
                destination = new Destination(globalWarp, DestinationType.GlobalWarp);
            } else continue;

            this.shortcuts.add(new Shortcut(destination, key.replace(" ", "_")));
        }

        List<String> data = config.getStringList("Shortcuts");
        if(data != null) {
            for(String datum : data) {
                try {
                    Shortcut s = new Shortcut();
                    JSONObject json = (JSONObject) new JSONParser().parse(datum);
                    s.read(json);
                    s.setDisplayName(s.getDisplayName().replace(" ", "_"));
                    this.shortcuts.add(s);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new CShortcuts().register(WarpSystem.getInstance());

        for(Shortcut s : this.shortcuts) {
            //create Command
            reloadCommand(s);
        }
        WarpSystem.log("    ...got " + this.shortcuts.size() + " Shortcut(s)");

        Bukkit.getPluginManager().registerEvents(new ShortcutListener(), WarpSystem.getInstance());
        return true;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Shortcuts");
        FileConfiguration config = file.getConfig();

        if(!saver) WarpSystem.log("  > Saving Shortcuts");

        for(String key : config.getKeys(false)) config.set(key, null);

        List<String> data = new ArrayList<>();
        for(Shortcut sc : this.shortcuts) {
            JSONObject json = new JSONObject();
            sc.write(json);
            data.add(json.toJSONString());
        }

        config.set("Shortcuts", data);

        if(!saver) WarpSystem.log("    ...saved " + data.size() + " Shortcut(s)");

        file.saveConfig();
    }

    public boolean hasCommandLoop(Shortcut s, String newCommand) {
        if(s.getDisplayName().equalsIgnoreCase(newCommand.substring(1))) return true;

        return hasCommandLoop(s, s.hasAction(Action.COMMAND) ? new ArrayList<String>(s.getAction(CommandAction.class).getValue()) {{
            add(newCommand);
        }} : new ArrayList<String>() {{
            add(newCommand);
        }}, null);
    }

    public boolean hasCommandLoop(Shortcut s, List<String> newCommands, Shortcut last) {
        List<String> commands;

        if(last == null) commands = newCommands;
        else if(last.getDisplayName().equals(s.getDisplayName())) return true;
        else commands = last.hasAction(Action.COMMAND) ? last.getAction(CommandAction.class).getValue() : null;

        if(commands == null) return false;

        for(String command : commands) {
            Shortcut next = getShortcut(command.substring(1));
            if(next == null) continue;

            if(hasCommandLoop(s, newCommands, next)) return true;
        }

        return false;
    }

    public void reloadCommand(Shortcut s) {
        reloadCommand(s, false);
    }

    public void reloadCommand(Shortcut s, boolean force) {
        ShortcutExecutor executor = executors.remove(s);
        boolean reload;
        if(reload = (executor != null)) executor.unregister(WarpSystem.getInstance());

        executor = new ShortcutExecutor(s);
        executors.put(s, executor);
        executor.register(WarpSystem.getInstance());

        if((reload || force) && Version.getVersion().isBiggerThan(Version.v1_12)) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.updateCommands();
            }
        }
    }

    @Override
    public void destroy() {
        List<Shortcut> l = new ArrayList<>(this.shortcuts);

        for(Shortcut shortcut : l) {
            remove(shortcut, false);
        }

        l.clear();
    }

    public void remove(Shortcut s, boolean forceUpdate) {
        this.shortcuts.remove(s);
        ShortcutExecutor executor = executors.remove(s);
        if(executor != null) executor.unregister(WarpSystem.getInstance());

        if(forceUpdate)
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.updateCommands();
            }
    }

    @Override
    public void onConnect() {
        WarpSystem.getInstance().getDataHandler().register(listener = new ShortcutPacketListener());
    }

    @Override
    public void onDisconnect() {
        if(listener != null) {
            WarpSystem.getInstance().getDataHandler().unregister(listener);
            listener = null;
        }
    }

    public Shortcut getShortcut(String displayName) {
        if(displayName == null) return null;

        for(Shortcut shortcut : this.shortcuts) {
            if(displayName.equalsIgnoreCase(shortcut.getDisplayName())) return shortcut;
        }

        return null;
    }

    public List<Shortcut> getShortcuts() {
        return shortcuts;
    }

    public static ShortcutManager getInstance() {
        return ((ShortcutManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.SHORTCUTS));
    }
}
