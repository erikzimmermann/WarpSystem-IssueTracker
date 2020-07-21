package de.codingair.warpsystem.spigot.base.utils.options.specific;

import de.codingair.warpsystem.spigot.api.StringFormatter;
import de.codingair.warpsystem.spigot.base.utils.options.Option;
import de.codingair.warpsystem.spigot.base.utils.options.Options;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.v2.TeleportDelay;
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
    private Option<String> cooldownTpa = new Option<>("WarpSystem.Cooldown.Tpa", "5m");
    private Option<String> cooldownBack = new Option<>("WarpSystem.Cooldown.Back", "0s");
    private Option<String> cooldownRandomTP = new Option<>("WarpSystem.Cooldown.Tpa", "5m");
    private Option<String> delayDisplay = new Option<>("WarpSystem.Teleport.Delay_Display", "ACTION_BAR");

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
        set(cooldownTpa);
        set(cooldownBack);
        set(cooldownRandomTP);
        set(delayDisplay);
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
        get(cooldownTpa);
        get(cooldownBack);
        get(cooldownRandomTP);
        get(delayDisplay);

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
            this.cooldownTpa = o.cooldownTpa.clone();
            this.cooldownBack = o.cooldownBack.clone();
            this.cooldownRandomTP = o.cooldownRandomTP.clone();
            this.delayDisplay = o.delayDisplay.clone();
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

    public TeleportDelay.Display getDelayDisplay() {
        try {
            return TeleportDelay.Display.valueOf(delayDisplay.getValue());
        } catch(Exception ex) {
            return TeleportDelay.Display.ACTION_BAR;
        }
    }

    public long getCooldownTpa() {
        return StringFormatter.convertFromTimeFormat(cooldownTpa.getValue(), 300000);
    }

    public long getCooldownBack() {
        return StringFormatter.convertFromTimeFormat(cooldownBack.getValue(), 0);
    }

    public long getCooldownRandomTP() {
        return StringFormatter.convertFromTimeFormat(cooldownRandomTP.getValue(), 300000);
    }

    public long getCooldown(Origin origin) {
        if(origin == Origin.TeleportRequest) return getCooldownTpa();
        else if(origin == Origin.TeleportCommand) return getCooldownBack();
        else if(origin == Origin.RandomTP) return getCooldownRandomTP();
        return 0;
    }
}
