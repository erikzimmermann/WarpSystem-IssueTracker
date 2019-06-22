package de.codingair.warpsystem.spigot.features.effectportals;

import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.animations.standalone.AnimationType;
import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.Node;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.menu.Menu;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PortalEditor implements Removable {
    public static String MINUS_PLUS(String s) {
        return ACTION_BAR(s, "-", "+");
    }

    public static String NEXT_PREVIOUS(String s) {
        return ACTION_BAR(s, "«", "»");
    }

    public static String ACTION_BAR(String s, String left, String right) {
        return ChatColor.YELLOW.toString() + left + ChatColor.GRAY + " " + Lang.get("Leftclick") + " | " + ChatColor.RED + s + ChatColor.GRAY + " | " + ChatColor.GRAY + Lang.get("Rightclick") + " " + ChatColor.YELLOW + right;
    }

    private final UUID uniqueId = UUID.randomUUID();
    private Player player;
    private EffectPortal effectPortal;
    private EffectPortal backupEffectPortal;
    private boolean finished = false;
    private Menu menu;

    public enum Action {
        INCREASE_TELEPORT_RADIUS, DECREASE_TELEPORT_RADIUS,
        INCREASE_ANIMATION_HEIGHT, DECREASE_ANIMATION_HEIGHT,
        NEXT_ANIMATION_TYPE, PREVIOUS_ANIMATION_TYPE,
        NEXT_PARTICLE, PREVIOUS_PARTICLE,
        INCREASE_HOLOGRAM_HEIGHT, DECREASE_HOLOGRAM_HEIGHT,
        CHANGE_START_NAME, CHANGE_DESTINATION_NAME,
        NEXT_SOUND, PREVIOUS_SOUND,
        INCREASE_VOLUME, DECREASE_VOLUME,
        INCREASE_PITCH, DECREASE_PITCH, CANCEL, SAVE,
        CHANGE_PERMISSION
    }

    public PortalEditor(Player player, EffectPortal effectPortal) {
        this.player = player;
        this.backupEffectPortal = effectPortal;
        this.effectPortal = new EffectPortal(this.backupEffectPortal);
        menu = new Menu(this.player, this);
    }

    public PortalEditor(Player player, Node<String, Location> first) {
        this.player = player;
        this.effectPortal = new EffectPortal(first.getValue(), new Destination(), null, first.getKey(), null, 2.2, true, true, null);
        menu = new Menu(this.player, this);
    }

    @Override
    public void destroy() {
        exit(false);
    }

    @Override
    public Class<? extends Removable> getAbstractClass() {
        return EffectPortal.class;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public JavaPlugin getPlugin() {
        return WarpSystem.getInstance();
    }

    public void doAction(Action action) {
        doAction(action, null);
    }

    public void doAction(Action action, Runnable after) {
        if(API.getRemovable(player, AnvilGUI.class) != null) return;

        switch(action) {
            case CHANGE_PERMISSION:
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        String input = e.getInput();

                        if(input != null && (input.equalsIgnoreCase("NONE") || input.equalsIgnoreCase("NULL"))) input = null;

                        effectPortal.setPermission(input);
                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        e.setPost(after);
                    }
                }, new ItemBuilder(Material.PAPER).setName(effectPortal.getPermission() == null ? "NONE" : effectPortal.getPermission()).getItem());
                break;

            case CHANGE_START_NAME:
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        e.setCancelled(true);
                        String input = e.getInput();

                        if(input == null) {
                            e.setClose(false);
                            player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            return;
                        }

                        effectPortal.setStartName(ChatColor.translateAlternateColorCodes('&', input));
                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        e.setPost(after);
                    }
                }, new ItemBuilder(Material.PAPER).setName(effectPortal.getStartName().replace("§", "&")).getItem());
                break;

            case CHANGE_DESTINATION_NAME:
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        e.setCancelled(true);
                        String input = e.getInput();

                        if(input == null) {
                            e.setClose(false);
                            player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            return;
                        }

                        effectPortal.setDestinationName(ChatColor.translateAlternateColorCodes('&', input));
                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        e.setPost(after);
                    }
                }, new ItemBuilder(Material.PAPER).setName(effectPortal.getDestinationName() == null ? (Lang.get("Name") + "...") : effectPortal.getDestinationName().replace("§", "&")).getItem());
                break;

            case INCREASE_HOLOGRAM_HEIGHT:
                this.effectPortal.setHologramHeight(this.effectPortal.getHologramHeight() + 0.1);
                break;

            case DECREASE_HOLOGRAM_HEIGHT:
                this.effectPortal.setHologramHeight(this.effectPortal.getHologramHeight() - 0.1);
                break;

            case SAVE:
                finish();
                return;

            case CANCEL:
                exit();
        }
    }

    public void start() {
        API.addRemovable(this);

        if(this.backupEffectPortal != null) this.backupEffectPortal.setRunning(false);
        this.effectPortal.setRunning(true);
        this.menu.open(true);

        this.player.sendMessage(Lang.getPrefix() + Lang.get("Entering_Portal_Editor"));
    }

    private void quit() {
        this.menu.close(true);
    }

    public void finish() {
        PortalManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.PORTALS);
        if(finished) return;
        if(this.backupEffectPortal != null) this.backupEffectPortal.apply(this.effectPortal);

        exit(true);

        if(this.backupEffectPortal == null) manager.getEffectPortals().add(this.effectPortal);
    }

    public void exit() {
        exit(effectPortal.isRegistered() || (backupEffectPortal != null && backupEffectPortal.isRegistered()));
    }

    private void exit(boolean running) {
        if(finished) return;
        finished = true;

        this.effectPortal.setRunning(this.backupEffectPortal == null && running);
        if(this.backupEffectPortal != null) this.backupEffectPortal.setRunning(running);

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
