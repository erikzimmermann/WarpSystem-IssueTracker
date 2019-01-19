package de.codingair.warpsystem.spigot.features.signs.listeners;

import de.codingair.codingapi.player.gui.sign.SignGUI;
import de.codingair.codingapi.player.gui.sign.SignTools;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.signs.managers.SignManager;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.DecoIcon;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.GUIListener;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(e.getClickedBlock() == null || e.getClickedBlock().getState() instanceof Sign) {
            Sign s = e.getClickedBlock() == null ? null : (Sign) e.getClickedBlock().getState();

            SignManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIGNS);
            WarpSign sign = manager.getByLocation(s.getLocation());
            if(sign != null) {
                if(e.getPlayer().hasPermission(WarpSystem.PERMISSION_MODIFY_WarpSigns) && e.getPlayer().getGameMode().equals(GameMode.CREATIVE) && e.getPlayer().getItemInHand().getType().equals(Material.SIGN)) {
                    String[] lines = s.getLines();
                    for(int i = 0; i < 4; i++) {
                        lines[i] = lines[i].replace("§", "&");
                    }

                    SignTools.updateSign(s, lines);

                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> new SignGUI(e.getPlayer(), s, WarpSystem.getInstance()) {
                        @Override
                        public void onSignChangeEvent(String[] lines) {
                            Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                                for(int i = 0; i < 4; i++) {
                                    lines[i] = ChatColor.translateAlternateColorCodes('&', lines[i]);
                                }

                                SignTools.updateSign(s, lines);
                            });
                            close();

                            e.getPlayer().sendMessage(Lang.getPrefix() + "§7" + Lang.get("WarpSign_Edited"));
                        }
                    }.open(), 2L);

                    return;
                }

                if(!e.getPlayer().hasPermission(WarpSystem.PERMISSION_USE_WarpSigns)) {
                    e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                    return;
                }
                WarpSystem.getInstance().getTeleportManager().tryToTeleport(e.getPlayer(), sign.getWarp());
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        SignManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIGNS);

        try {
            Sign s = e.getBlock() == null ? null : (Sign) e.getBlock().getState();
            if(s == null) return;
            WarpSign sign = manager.getByLocation(s.getLocation());
            if(sign == null) return;

            if(!e.getPlayer().hasPermission(WarpSystem.PERMISSION_MODIFY) || !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                e.setCancelled(true);
            } else {
                manager.getWarpSigns().remove(sign);
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("WarpSign_Removed"));
            }
        } catch(Exception ignored) {
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onPlace(SignChangeEvent e) {
        SignManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.SIGNS);
        if(!e.getPlayer().hasPermission(WarpSystem.PERMISSION_MODIFY) || !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;

        if(e.getLine(0).equalsIgnoreCase("[warps]")) {
            e.getPlayer().sendMessage(" ");
            e.getPlayer().sendMessage(Lang.getPrefix() + "§7" + Lang.get("WarpSign_Choose_Warp"));
            e.getPlayer().sendMessage(" ");
            new GWarps(e.getPlayer(), null, false, new GUIListener() {
                @Override
                public String getTitle() {
                    return Lang.get("WarpSign_Choose_Warp_GUI");
                }

                @Override
                public void onClose() {

                }

                @Override
                public Task onClickOnWarp(Warp warp, boolean editing) {
                    Sign s = (Sign) e.getBlock().getState();
                    SignTools.updateSign(s, new String[] {"", "§4§n" + Lang.get("Description"), "", ""});

                    Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                        new SignGUI(e.getPlayer(), s, WarpSystem.getInstance()) {
                            @Override
                            public void onSignChangeEvent(String[] lines) {

                                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                                    for(int i = 0; i < 4; i++) {
                                        lines[i] = ChatColor.translateAlternateColorCodes('&', lines[i]);
                                    }

                                    SignTools.updateSign(s, lines);
                                }, 2L);
                                close();

                                e.getPlayer().sendMessage(Lang.getPrefix() + "§7" + Lang.get("WarpSign_Finish"));
                            }
                        }.open();

                        WarpSign sign = new WarpSign(Location.getByLocation(s.getLocation()), warp);
                        manager.getWarpSigns().add(sign);
                    }, 2L);

                    return new Task();
                }
            }, false, GlobalWarp.class, DecoIcon.class).open();
        }
    }

}
