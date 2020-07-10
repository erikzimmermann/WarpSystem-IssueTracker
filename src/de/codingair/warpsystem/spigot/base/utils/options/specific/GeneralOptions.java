package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import org.bukkit.ChatColor;

import java.util.function.IntPredicate;

public class GeneralOptions extends Options {
    private Option<String> lang = new Option<>("WarpSystem.Language", "ENG");
    private Option<Integer> teleportDelay = new Option<>("WarpSystem.Teleport.Delay");
    private Option<Boolean> allowMove = new Option<>("WarpSystem.Teleport.Allow_Move");
    private Option<Boolean> afterEffects = new Option<>("WarpSystem.Teleport.Animation_After_Teleport.Enabled");
    private Option<Boolean> publicAnimations = new Option<>("WarpSystem.Teleport.Public_Animations");
    private Option<String> cmdSugColor = new Option<>("WarpSystem.Command_Suggestions.Color", "&7");
    private Option<String> cmdArgColor = new Option<>("WarpSystem.Command_Suggestions.Argument", "&e");

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
        set(afterEffects);
        set(publicAnimations);
        set(cmdSugColor);
        set(cmdArgColor);
        save();
    }

    @Override
    public void read() {
        get(lang);
        get(teleportDelay);
        get(allowMove);
        get(afterEffects);
        get(publicAnimations);
        get(cmdSugColor);
        get(cmdArgColor);

        IntPredicate test = new IntPredicate() {
            private boolean color = false;

            @Override
            public boolean test(int value) {
                char c = (char) value;

                if(color) color = false;
                else if(c == '&') color = true;
                else return false;

                return true;
            }
        };

        StringBuilder s = new StringBuilder();
        for(int c : cmdSugColor.getValue().trim().chars().filter(test).toArray()) {
            s.append((char) c);
        }
        cmdSugColor.setValue(s.toString());

        s = new StringBuilder();
        for(int c : cmdArgColor.getValue().trim().chars().filter(test).toArray()) {
            s.append((char) c);
        }
        cmdArgColor.setValue(s.toString());
    }

    @Override
    public void apply(Options options) {
        if(options instanceof GeneralOptions) {
            GeneralOptions o = (GeneralOptions) options;

            this.lang = o.lang.clone();
            this.teleportDelay = o.teleportDelay.clone();
            this.allowMove = o.allowMove.clone();
            this.afterEffects = o.afterEffects.clone();
            this.publicAnimations = o.publicAnimations.clone();
            this.cmdSugColor = o.cmdSugColor.clone();
            this.cmdArgColor = o.cmdArgColor.clone();
        }
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

    public boolean isAllowMove() {
        return allowMove.getValue();
    }

    public boolean isAfterEffects() {
        return afterEffects.getValue();
    }

    public boolean isPublicAnimations() {
        return publicAnimations.getValue();
    }

    public String cmdSug() {
        return ChatColor.translateAlternateColorCodes('&', cmdSugColor.getValue());
    }

    public String cmdArg() {
        return ChatColor.translateAlternateColorCodes('&', cmdArgColor.getValue());
    }
}
