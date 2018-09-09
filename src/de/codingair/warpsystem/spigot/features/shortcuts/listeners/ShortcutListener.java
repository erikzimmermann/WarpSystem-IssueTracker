package de.codingair.warpsystem.spigot.features.shortcuts.listeners;

import de.codingair.warpsystem.spigot.features.shortcuts.managers.ShortcutManager;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ShortcutListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String label = e.getMessage().split(" ")[0].replaceFirst("/", "");
        Shortcut shortcut = ShortcutManager.getInstance().getShortcut(label);
        if(shortcut != null && shortcut.isActive()) {
            shortcut.run(e.getPlayer());
            e.setCancelled(true);
        }
    }

}
