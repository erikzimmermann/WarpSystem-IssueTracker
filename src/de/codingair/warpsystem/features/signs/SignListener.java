package de.codingair.warpsystem.features.signs;

import de.codingair.codingapi.player.gui.sign.SignGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        if(e.getClickedBlock() == null || e.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
            Sign s = e.getClickedBlock() == null ? null : (Sign) e.getClickedBlock().getState();

            new SignGUI(e.getPlayer(), s) {
                @Override
                public void onSignChangeEvent(String[] lines) {
                    if(s == null) return;

                    for(int i = 0; i < 4; i++) {
                        s.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
                    }

                    s.setLine(0, "§8[§cWarps§8]");

                    s.update(true);
                }
            }.open();
        }
    }

}
