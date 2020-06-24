package de.codingair.warpsystem.spigot.base.setupassistant.utils;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class Value {
    private final String type;
    private final String config;
    private final String configPath;
    private final String name;
    private final String since;
    private final String defaultValue;
    private final String valuePath;
    private final String description;
    private final Class<?> clazz;
    private Object startValue = null;
    private ConfigFile cache = null;

    public Value(String type, String config, String configPath, String name, String since, String defaultValue, String valuePath, String description, Class<?> clazz) {
        this.type = type;
        this.config = config;
        this.configPath = configPath;
        this.name = name;
        this.since = since;
        this.defaultValue = defaultValue;
        this.valuePath = valuePath;
        this.description = description;
        this.clazz = clazz;
    }

    private ConfigFile config() {
        if(cache == null) cache = WarpSystem.getInstance().getFileManager().loadFile(config, configPath);
        return cache;
    }

    public Object getCurrentValue() {
        return config().getConfig().get(valuePath, defaultValue);
    }

    public boolean set(String s) {
        if(startValue == null) startValue = getCurrentValue();
        s = s.trim();

        if(s.startsWith("\"") || s.startsWith("'")) s = s.substring(1);
        if(s.endsWith("\"") || s.endsWith("'")) s = s.substring(0, s.length() - 1);

        try {
            config().getConfig().set(valuePath, Adapter.transform(clazz, s));
            config().saveConfig();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean hasBeenChanged() {
        return startValue != null && !Objects.equals(startValue, getCurrentValue());
    }

    public String getType() {
        try {
            return ChatColor.stripColor(Lang.get(type.replace(" ", "_")).trim());
        } catch(IllegalStateException ignored) {
            return type;
        }
    }

    public String getConfig() {
        return config;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getName() {
        try {
            return ChatColor.stripColor(Lang.get(name.replace(" ", "_")).trim());
        } catch(IllegalStateException ignored) {
            return name;
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public Class<?> getValueClass() {
        return clazz;
    }

    private enum Adapter {
        Boolean(Boolean.class, "parseBoolean"),
        Byte(Byte.class, "parseByte"),
        Short(Short.class, "parseShort"),
        Integer(Integer.class, "parseInt"),
        Long(Long.class, "parseLong"),
        Float(Float.class, "parseFloat", true),
        Double(Double.class, "parseDouble", true),
        UNKNOWN(null, null),
        ;

        private final Class<?> clazz;
        private final String method;
        private final boolean comma;

        Adapter(Class<?> clazz, String method) {
            this(clazz, method, false);
        }

        Adapter(Class<?> clazz, String method, boolean comma) {
            this.clazz = clazz;
            this.method = method;
            this.comma = comma;
        }

        public static Object transform(Class<?> clazz, String value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IllegalArgumentException {
            Adapter a = getByClass(clazz);
            if(a == UNKNOWN) return value;
            return a.transform(value);
        }

        public static Adapter getByClass(Class<?> clazz) {
            for(Adapter value : values()) {
                if(clazz.equals(value.clazz)) return value;
            }

            return UNKNOWN;
        }

        public Object transform(String s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
            if(this.equals(Boolean)) {
                if(s.equalsIgnoreCase("true")) return true;
                else if(s.equalsIgnoreCase("false")) return false;
                else throw new IllegalArgumentException("'" + s + "' is not a Boolean.");
            }

            Method m = clazz.getDeclaredMethod(method, String.class);

            if(comma) s = s.replace(",", ".");
            if(Number.class.isInstance(clazz) && s.contains(" ")) s = s.split(" ")[0];

            return m.invoke(null, s);
        }
    }
}
