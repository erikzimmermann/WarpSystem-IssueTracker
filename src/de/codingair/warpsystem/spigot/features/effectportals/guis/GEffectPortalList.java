package de.codingair.warpsystem.spigot.features.effectportals.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.list.GUIList;
import de.codingair.warpsystem.spigot.base.guis.list.ListItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.effectportals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.effectportals.utils.EffectPortal;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class GEffectPortalList extends GUIList<EffectPortal> {
    public GEffectPortalList(Player p) {
        super(p, "§c" + Lang.get("Effect_Portal") + "§7- §c" + Lang.get("List") + " §7(%CURRENT%/%MAX%)", true);
    }

    @Override
    public void addListItems(List<ListItem<EffectPortal>> listItems) {
        for(EffectPortal value : PortalManager.getInstance().getEffectPortals()) {
            listItems.add(new ListItem<EffectPortal>(value) {
                @Override
                public ItemStack buildItem() {
                    String destType = null;
                    String dest = null;

                    if(value.getDestination() != null) {
                        dest = value.getDestination().getId();

                        switch(value.getDestination().getType()) {
                            case Server: {
                                destType = Lang.get("Server");
                                break;
                            }

                            case SimpleWarp: {
                                destType = Lang.get("SimpleWarp");
                                break;
                            }

                            case GlobalWarp: {
                                destType = Lang.get("GlobalWarp");
                                break;
                            }

                            case EffectPortal: {
                                destType = Lang.get("Effect_Portal");
                                dest = value.getDestinationName();
                                break;
                            }
                        }

                        destType = ChatColor.highlight(destType, getSearched(), "§e§n", "§7", true);
                        dest = ChatColor.highlight(dest, getSearched(), "§e§n", "§7", true);
                    }

                    return new ItemBuilder(Material.ENDER_PEARL)
                            .setName("§7\"§r" + ChatColor.highlight(value.getStartName(), getSearched(), "§e§n", "§r", true) + "§7\"")
                            .setLore("§7" + Lang.get("Destination") + ": §7" + (destType == null ? "§c" + Lang.get("Not_Set") : dest + " (" + destType + ")"))
                            .getItem();
                }

                @Override
                public void onClick(EffectPortal value, ClickType clickType) {
                    GEffectPortalList.this.onClick(value, clickType);
                }

                @Override
                public boolean isSearched(String searching) {String destType = null;
                    String dest = null;

                    if(value.getDestination() != null) {
                        dest = value.getDestination().getId();

                        switch(value.getDestination().getType()) {
                            case Server: {
                                destType = Lang.get("Server");
                                break;
                            }

                            case SimpleWarp: {
                                destType = Lang.get("SimpleWarp");
                                break;
                            }

                            case GlobalWarp: {
                                destType = Lang.get("GlobalWarp");
                                break;
                            }

                            case EffectPortal: {
                                destType = Lang.get("Effect_Portal");
                                dest = value.getDestinationName();
                                break;
                            }
                        }
                    }

                    return value.getStartName().toLowerCase().contains(searching.toLowerCase())
                            || (dest != null && (dest.toLowerCase().contains(searching.toLowerCase()) || destType.toLowerCase().contains(searching.toLowerCase())));
                }
            });
        }
    }
}
