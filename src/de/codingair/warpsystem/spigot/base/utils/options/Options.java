package de.codingair.warpsystem.spigot.base.utils.options;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.warpsystem.spigot.base.WarpSystem;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class Options {
    private ConfigFile file;

    public Options(ConfigFile file) {
        this.file = file;
    }

    public Options(String name) {
        this(name, "/");
    }

    public Options(String name, String path) {
        this(WarpSystem.getInstance().getFileManager().getFile(name));

        if(this.file == null) {
            WarpSystem.getInstance().getFileManager().loadFile(name, path);
            this.file = WarpSystem.getInstance().getFileManager().getFile(name);
        }
    }

    public ConfigFile getFile() {
        return file;
    }

    public UTFConfig getConfig() {
        return this.file.getConfig();
    }

    public void reloadFile(boolean save) {
        if(save) this.file.saveConfig();
        this.file.loadConfig();
    }

    public void save() {
        this.file.saveConfig();
    }

    public void get(Option option) {
        option.setValue(getConfig().get(option.getPath(), option.getDefault()));
    }

    public void set(Option option) {
        if(option.hasChanged()) getConfig().set(option.getPath(), option.getValue());
    }

    public abstract Options clone();

    public abstract void write();

    public abstract void read();

    public abstract void apply(Options options);

    @Override
    public boolean equals(Object obj) {
        if(this.getClass().isInstance(obj)) {
            Options other = (Options) obj;

            for(Field field : getClass().getDeclaredFields()) {
                field.setAccessible(true);

                try {
                    if(!Objects.equals(field.get(this), field.get(other))) return false;
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return true;
        }
        return false;
    }
}
