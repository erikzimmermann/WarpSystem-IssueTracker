package de.codingair.warpsystem.spigot.base.utils.options;

import de.codingair.warpsystem.spigot.base.WarpSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeneralOptions extends Options {
    private Option<String> lang = new Option<>("WarpSystem.Language", "ENG");
    private Option<Integer> teleportDelay = new Option<>("WarpSystem.Teleport.Delay", 5);
    private Option<Boolean> allowMove = new Option<>("WarpSystem.Teleport.Allow_Move", false);

    public GeneralOptions() {
        super("Config");
    }

    public GeneralOptions(GeneralOptions options) {
        super(options.getFile());
        apply(options);
    }

    @Override
    public void write() {
        set(lang);
        set(teleportDelay);
        set(allowMove);
        save();
    }

    @Override
    public void read() {
        get(lang);
        get(teleportDelay);
        get(allowMove);
    }

    @Override
    public void apply(Options options) {
        if(options instanceof GeneralOptions) {
            GeneralOptions o = (GeneralOptions) options;

            this.lang = o.lang.clone();
            this.teleportDelay = o.teleportDelay.clone();
            this.allowMove = o.allowMove.clone();
        }
    }

    public List<String> getLanguages() {
        File file = new File(WarpSystem.getInstance().getDataFolder().getPath() + "/Languages/");
        List<String> languages = new ArrayList<>();

        for(File listFile : file.listFiles()) {
            if(listFile.getName().endsWith(".yml")) {
                languages.add(listFile.getName().replace(".yml", ""));
            }
        }

        return languages;
    }

    @Override
    public Options clone() {
        return new GeneralOptions(this);
    }

    public String getLang() {
        return lang.getValue();
    }

    public void setLang(String lang) {
        this.lang.setValue(lang);
    }

    public int getTeleportDelay() {
        return teleportDelay.getValue();
    }

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay.setValue(teleportDelay);
    }

    public boolean isAllowMove() {
        return allowMove.getValue();
    }

    public void setAllowMove(boolean allowMove) {
        this.allowMove.setValue(allowMove);
    }
}
