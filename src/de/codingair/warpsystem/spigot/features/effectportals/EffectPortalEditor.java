package de.codingair.warpsystem.spigot.features.effectportals;

import de.codingair.codingapi.API;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.effectportals.guis.editor.Menu;
import de.codingair.warpsystem.spigot.features.effectportals.managers.EffectPortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class EffectPortalEditor implements Removable {
    private final UUID uniqueId = UUID.randomUUID();
    private Player player;
    private EffectPortal effectPortal;
    private EffectPortal backupEffectPortal;
    private boolean finished = false;
    private Menu menu;

    public EffectPortalEditor(Player player, EffectPortal effectPortal) {
        this.player = player;
        this.backupEffectPortal = effectPortal;
        this.effectPortal = new EffectPortal(this.backupEffectPortal);
        if(this.effectPortal.getLink() != null) this.effectPortal.setLink(new EffectPortal(this.effectPortal.getLink()));
        if(this.effectPortal.getDestination() == null) this.effectPortal.addAction(new WarpAction(new Destination()));
        menu = new Menu(this.player, this);
    }

    public EffectPortalEditor(Player player, String name) {
        this.player = player;
        this.effectPortal = new EffectPortal(Location.getByLocation(player.getLocation()), new Destination(), null, name, true, null);
        menu = new Menu(this.player, this);
    }

    public static String MINUS_PLUS(String s) {
        return ACTION_BAR(s, "-", "+");
    }

    public static String MINUS_PLUS_SHIFT(String s, String shift) {
        return ACTION_BAR(s, "§7(§e" + shift + " §7|§e " + Lang.get("Shift") + "§7) §e-", "+ §7(§e" + Lang.get("Shift") + " §7|§e " + shift + "§7)");
    }

    public static String MINUS_PLUS_SHIFT(String s) {
        return ACTION_BAR(s, "§7(§e" + Lang.get("Shift") + "§7) §e-", "+ §7(§e" + Lang.get("Shift") + "§7)");
    }

    public static String PREVIOUS_NEXT(String s) {
        return ACTION_BAR(s, "«", "»");
    }

    public static String PREVIOUS_NEXT_SHIFT(String s) {
        return ACTION_BAR(s, "§7(§e" + Lang.get("Shift") + "§7) §e«", "» §7(§e" + Lang.get("Shift") + "§7)");
    }

    public static String ACTION_BAR(String s, String left, String right) {
        return ChatColor.YELLOW.toString() + left + ChatColor.GRAY + " " + Lang.get("Leftclick") + " | " + ChatColor.RED + s + ChatColor.GRAY + " | " + ChatColor.GRAY + Lang.get("Rightclick") + " " + ChatColor.YELLOW + right;
    }

    @Override
    public void destroy() {
        exit(true);
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public JavaPlugin getPlugin() {
        return WarpSystem.getInstance();
    }

    public void start() {
        API.addRemovable(this);

        if(this.backupEffectPortal != null) this.backupEffectPortal.setRunning(false);
        if(this.backupEffectPortal != null && this.backupEffectPortal.getLink() != null) this.backupEffectPortal.getLink().setRunning(false);
        this.effectPortal.setRunning(true);
        if(this.effectPortal.getLink() != null) this.effectPortal.getLink().setRunning(true);
        this.menu.open(true);

        this.player.sendMessage(Lang.getPrefix() + Lang.get("Entering_Portal_Editor"));
    }

    private void quit() {
        this.menu.close(true);
    }

    public void finish() {
        EffectPortalManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.EFFECT_PORTALS);
        if(finished) return;

        if(effectPortal.getAnimation() == null) {
            if(backupEffectPortal != null) backupEffectPortal.unregister();
            exit(false);
        } else {
            EffectPortal oldLink = this.backupEffectPortal == null ? null : this.backupEffectPortal.getLink();
            EffectPortal newLink = this.effectPortal.getLink();

            if(this.backupEffectPortal == null) manager.getEffectPortals().add(this.effectPortal);  //register portal
            else this.backupEffectPortal.apply(this.effectPortal);                                  //apply portal

            if(oldLink == null && newLink != null) {
                manager.getEffectPortals().add(newLink);                                        //register link
                if(backupEffectPortal != null) backupEffectPortal.setLink(newLink);
                else effectPortal.setLink(newLink);
            } else if(oldLink != null && newLink == null) {
                oldLink.unregister();                                                            //unregister link
                backupEffectPortal.setLink(newLink);
            } else if(oldLink != null) {
                oldLink.apply(newLink);                                                          //apply link
                backupEffectPortal.setLink(oldLink);
            }

            exit(true);
        }
    }

    public void exit() {
        exit(effectPortal.isRegistered() || (backupEffectPortal != null && backupEffectPortal.isRegistered()));
    }

    private void exit(boolean running) {
        if(finished) return;
        finished = true;

        this.effectPortal.setRunning(this.backupEffectPortal == null && running);
        if(this.effectPortal.getLink() != null) this.effectPortal.getLink().setRunning(this.backupEffectPortal == null && running);
        if(this.backupEffectPortal != null) {
            this.backupEffectPortal.setRunning(running);
            if(this.backupEffectPortal.getLink() != null) this.backupEffectPortal.getLink().setRunning(running);
        }

        quit();
        API.removeRemovable(this);
    }

    public Player getPlayer() {
        return player;
    }

    public EffectPortal getEffectPortal() {
        return effectPortal;
    }

    public EffectPortal getBackupEffectPortal() {
        return backupEffectPortal;
    }
}
