package de.codingair.warpsystem.spigot.base.setupassistant;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.AvailableForSetupAssistant;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.Function;
import de.codingair.warpsystem.spigot.base.setupassistant.utils.SetupAssistant;
import de.codingair.warpsystem.spigot.base.setupassistant.utils.Value;
import de.codingair.warpsystem.spigot.base.utils.PluginVersion;
import de.codingair.warpsystem.spigot.features.FeatureType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AvailableForSetupAssistant(config = "Config", type = "General")
@Function(name = "Language", configPath = "WarpSystem.Language", defaultValue = "ENG", description = "§8 (§7ENG, ES, FRA, GER§8)", clazz = String.class)
@Function(name = "Permissions", configPath = "WarpSystem.Permissions", defaultValue = "false", clazz = Boolean.class)
@Function(name = "Walking in teleports", configPath = "WarpSystem.Teleport.Allow_Move", defaultValue = "false", clazz = Boolean.class)
@Function(name = "Teleport Delay", configPath = "WarpSystem.Teleport.Delay", defaultValue = "3", clazz = Integer.class)
@Function(name = "Animation after teleports", configPath = "WarpSystem.Teleport.Animation_After_Teleport.Enabled", defaultValue = "true", clazz = Boolean.class)
@Function(name = "Public animations", description = "§7Only visible for players who\n§esee you §8(§7Vanish§8)", configPath = "WarpSystem.Teleport.Public_Animations", defaultValue = "true", clazz = Boolean.class)
@Function(name = "CMD suggestion color", configPath = "WarpSystem.Command_Suggestions.Color", defaultValue = "&7", clazz = String.class, since = "v4.2.8")
@Function(name = "CMD argument color", configPath = "WarpSystem.Command_Suggestions.Argument", defaultValue = "&e", clazz = String.class, since = "v4.2.8")
public class SetupAssistantManager {
    private SetupAssistant assistant = null;
    private List<Value> cachedFunctions = null;
    private List<Value> cachedNews = null;
    private final Cache<PluginVersion, List<Value>> cache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    private List<Value> cachedNews() {
        if(cachedNews == null) cachedNews = PluginVersion.getOld() == PluginVersion.getCurrent() ? new ArrayList<>() : listFunctions(PluginVersion.getCurrent());
        return cachedNews;
    }

    private List<Value> cachedFunctions() {
        if(cachedFunctions == null) cachedFunctions = listFunctions(null);
        return cachedFunctions;
    }

    public List<Value> getFunction(PluginVersion version) {
        if(version == null) return cachedFunctions();
        return listFunctions(version);
    }

    private List<Value> listFunctions(PluginVersion version) {
        List<Value> l = version == null ? null : cache.getIfPresent(version);
        if(l != null) return l;

        l = new ArrayList<>();
        checkClass(l, this.getClass(), version);
        for(FeatureType t : FeatureType.values()) {
            checkClass(l, t.getManagerClass(), version);
        }

        if(version != null && version != PluginVersion.getOld() && !l.isEmpty()) cache.put(version, l);

        return l;
    }

    private void checkClass(List<Value> l, Class<?> c, PluginVersion filter) {
        AvailableForSetupAssistant a = c.getDeclaredAnnotation(AvailableForSetupAssistant.class);

        if(a != null) {
            Function[] functions = c.getDeclaredAnnotationsByType(Function.class);
            for(Function f : functions) {
                if(filter == null || PluginVersion.getVersion(f.since()).ordinal() >= filter.ordinal())
                    l.add(new Value(a.type(), f.config().isEmpty() ? a.config() : f.config(), a.configPath(), f.name(), f.since(), f.defaultValue(), f.configPath(), f.description(), f.clazz()));
            }
        }
    }

    public void onJoin(Player player) {
        if(WarpSystem.hasPermission(player, WarpSystem.PERMISSION_MODIFY)) {
            if(cachedNews().isEmpty()) return;

            SimpleMessage m = new SimpleMessage(Lang.getPrefix() + "§eNew features §7can be configured! §8[", WarpSystem.getInstance());
            m.add(new ChatButton("§c§nSetupAssistant", "§7» §eStart §8(§7/warpsystem§8)") {
                @Override
                public void onClick(Player player) {
                    startAssistant(player, false);
                    m.destroy();
                }
            });
            m.add("§8]\n" + Lang.getPrefix() + "§7The chat will be §cunavailable §7during the setup.");

            m.setTimeOut(60);
            m.send(player);
        }
    }

    public void onDisable() {
        if(this.assistant != null){
            this.assistant.onQuit();
        }
    }

    public void clearAssistant() {
        this.assistant = null;
    }

    public void startAssistant(Player player, boolean general) {
        List<Value> l = general ? cachedFunctions() : cachedNews();
        if(l.isEmpty()) return;
        if(this.assistant != null) return;
        this.assistant = new SetupAssistant(player, l, general);
    }

    public SetupAssistant getAssistant() {
        return assistant;
    }

    public static SetupAssistantManager getInstance() {
        return WarpSystem.getInstance().getSetupAssistantManager();
    }
}
