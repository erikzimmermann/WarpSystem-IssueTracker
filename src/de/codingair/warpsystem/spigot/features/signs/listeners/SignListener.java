package de.codingair.warpsystem.spigot.features.signs.listeners;

import de.codingair.codingapi.player.gui.sign.SignGUI;
import de.codingair.codingapi.player.gui.sign.SignTools;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.signs.managers.SignManager;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.GUIListener;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Task;
import de.codingair.warpsystem.spigot.base.language.Example;
import de.codingair.warpsystem.spigot.base.language.Lang;
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
                if(e.getPlayer().hasPermission(WarpSystem.PERMISSION_MODIFY) && e.getPlayer().getGameMode().equals(GameMode.CREATIVE) && e.getPlayer().getItemInHand().getType().equals(Material.SIGN)) {
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

                            e.getPlayer().sendMessage(Lang.getPrefix() + "§7" + Lang.get("WarpSign_Edited", new Example("ENG", "The WarpSign was edited successfully!"), new Example("GER", "Das Warp-Schild wurde erfolgreich bearbeitet!")));
                        }
                    }.open(), 2L);

                    return;
                }

                WarpSystem.getInstance().getTeleportManager().teleport(e.getPlayer(), sign.getWarp());
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
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("No_Permission", new Example("GER", "&cDu hast keine Berechtigungen für diese Aktion!"), new Example("ENG", "&cYou don't have permissions for that action!"), new Example("FRE", "&cDésolé mais vous ne possédez la permission pour exécuter cette action!")));
                e.setCancelled(true);
            } else {
                manager.getWarpSigns().remove(sign);
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("WarpSign_Removed", new Example("GER", "&7Das Warp-Schild wurde &centfernt&7!"), new Example("ENG", "&7The WarpSign was &cremoved&7!")));
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
            e.getPlayer().sendMessage(Lang.getPrefix() + "§7" + Lang.get("WarpSign_Choose_Warp", new Example("ENG", "Choose a warp for the sign!"), new Example("GER", "Wähle ein Warp für das Warp-Schild aus!")));
            e.getPlayer().sendMessage(" ");
            new GWarps(e.getPlayer(), null, false, new GUIListener() {
                @Override
                public String getTitle() {
                    return Lang.get("WarpSign_Choose_Warp_GUI", new Example("ENG", "&4&lChoose a warp!"), new Example("GER", "&4&lWähle ein Warp!"));
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

                                e.getPlayer().sendMessage(Lang.getPrefix() + "§7" + Lang.get("WarpSign_Finish", new Example("ENG", "The WarpSign was set successfully!"), new Example("GER", "Das Warp-Schild wurde erfolgreich gesetzt!")));
                            }
                        }.open();

                        WarpSign sign = new WarpSign(Location.getByLocation(s.getLocation()), warp);
                        manager.getWarpSigns().add(sign);
                    }, 2L);

                    return new Task();
                }
            }).open();
        }
    }

}
