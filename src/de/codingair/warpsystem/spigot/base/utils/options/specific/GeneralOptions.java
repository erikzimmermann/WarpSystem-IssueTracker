package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeneralOptions extends Options {
    private Option<String> lang = new Option<>("WarpSystem.Language", "ENG");
    private Option<Integer> teleportDelay = new Option<>("WarpSystem.Teleport.Delay");
    private Option<Boolean> allowMove = new Option<>("WarpSystem.Teleport.Allow_Move");
    private Option<Integer> chunkPreLoadRadius = new Option<>("WarpSystem.Teleport.Chunk_Pre_Loading.Chunk_Radius");
    private Option<Boolean> chunkPreLoad = new Option<>("WarpSystem.Teleport.Chunk_Pre_Loading.Enabled");
    private Option<Boolean> chunkPreLoadingLimitedByPerm = new Option<>("WarpSystem.Teleport.Chunk_Pre_Loading.Limit_by_Permission");
    private Option<Boolean> afterEffects = new Option<>("WarpSystem.Teleport.Animation_After_Teleport.Enabled");

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
        set(chunkPreLoadRadius);
        set(chunkPreLoad);
        set(chunkPreLoadingLimitedByPerm);
        set(afterEffects);
        save();
    }

    @Override
    public void read() {
        get(lang);
        get(teleportDelay);
        get(allowMove);
        get(chunkPreLoadRadius);
        get(chunkPreLoad);
        get(chunkPreLoadingLimitedByPerm);
        get(afterEffects);
    }

    @Override
    public void apply(Options options) {
        if(options instanceof GeneralOptions) {
            GeneralOptions o = (GeneralOptions) options;

            this.lang = o.lang.clone();
            this.teleportDelay = o.teleportDelay.clone();
            this.allowMove = o.allowMove.clone();
            this.chunkPreLoadRadius = o.chunkPreLoadRadius.clone();
            this.chunkPreLoad = o.chunkPreLoad.clone();
            this.chunkPreLoadingLimitedByPerm = o.chunkPreLoadingLimitedByPerm.clone();
            this.afterEffects = o.afterEffects.clone();
        }
    }

    public static List<String> getLanguages() {
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

    public int getChunkPreLoadRadius() {
        return chunkPreLoadRadius.getValue();
    }

    public void setChunkPreLoadRadius(int radius) {
        chunkPreLoadRadius.setValue(radius);
    }

    public boolean isChunkPreLoadEnabled() {
        return chunkPreLoad.getValue();
    }

    public void setChunkPreLoad(boolean enabled) {
        chunkPreLoad.setValue(enabled);
    }

    public boolean isChunkPreLoadingLimitedByPerm() {
        return chunkPreLoadingLimitedByPerm.getValue();
    }

    public void setChunkPreLoadingLimitedByPerm(boolean limited) {
        chunkPreLoadingLimitedByPerm.setValue(limited);
    }

    public boolean isAfterEffects() {
        return afterEffects.getValue();
    }

    public void setAfterEffects(boolean afterEffects) {
        this.afterEffects.setValue(afterEffects);
    }
}
