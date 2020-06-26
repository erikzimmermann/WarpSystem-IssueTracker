package de.codingair.warpsystem.spigot.features.portals.guis;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.MusicData;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.DestinationPage;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.TeleportSoundPage;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.TeleportSoundAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.portals.guis.pages.PAnimations;
import de.codingair.warpsystem.spigot.features.portals.guis.pages.PAppearance;
import de.codingair.warpsystem.spigot.features.portals.guis.pages.POptions;
import de.codingair.warpsystem.spigot.features.portals.managers.PortalManager;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PortalEditor extends Editor<Portal> {
    public static final HashMap<String, PortalEditor> EDITORS = new HashMap<>();
    private final Portal clone;

    public PortalEditor(Player p, Portal portal) {
        this(p, portal.setEditMode(false), portal.clone().setEditMode(true).createDestinationIfAbsent().createTeleportSoundIfAbsent());
    }

    public PortalEditor(Player p, Portal portal, Portal clone) {
        super(p, clone, new Backup<Portal>(portal) {
            @Override
            public void applyTo(Portal value) {
                if(clone.getDestination() != null && clone.getDestination().getId() == null) clone.setDestination(null);
                if(clone.hasAction(Action.TELEPORT_SOUND) && TeleportSoundPage.isStandardSound(clone.getAction(TeleportSoundAction.class).getValue())) clone.removeAction(Action.TELEPORT_SOUND);

                String oldName = ChatColor.stripColor(portal.getDisplayName());
                String newName = ChatColor.stripColor(clone.getDisplayName());

                portal.apply(clone);
                clone.destroy();

                portal.setEditMode(false);
                portal.setVisible(true);

                if(!PortalManager.getInstance().getPortals().contains(portal)) {
                    PortalManager.getInstance().addPortal(portal);
                } else {
                    if(!oldName.equals(newName)) {
                        for(Portal p : PortalManager.getInstance().getPortals()) {
                            if(p.getDestination().getType() == DestinationType.Portal) {
                                if(p.getDestination().getId().equals(oldName)) {
                                    p.getDestination().setId(newName);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void cancel(Portal value) {
                clone.destroy();
                if(PortalManager.getInstance().getPortals().contains(portal)) {
                    portal.setEditMode(false);
                    portal.setVisible(true);
                } else portal.destroy();
            }
        }, () -> new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem(), new PAppearance(p, clone), new POptions(p, clone), new PAnimations(p, clone), new DestinationPage(p, getMainTitle(), clone.getDestination(), Origin.Portal, new SyncButton(0) {
            @Override
            public ItemStack craftItem() {
                Portal target = clone.getDestination().getType() != DestinationType.Portal ? null : PortalManager.getInstance().getPortal(clone.getDestination().getId());

                ItemBuilder builder = new ItemBuilder(XMaterial.END_PORTAL_FRAME)
                        .setName(Editor.ITEM_TITLE_COLOR + Lang.get("Portals"))
                        .addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Current") + ": " + (target == null ? "§c" + Lang.get("Not_Set") : "§7'§f" + ChatColor.translateAlternateColorCodes('&', target.getDisplayName()) + "§7'"));

                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + (target == null ? Lang.get("Set") : Lang.get("Change")));
                if(target != null) builder.addLore(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": §c" + Lang.get("Remove"));

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isLeftClick()) {
                    PortalEditor editor = API.getRemovable(player, PortalEditor.class);
                    editor.changeGUI(new PortalList(player) {
                        @Override
                        public void onClick(Portal value, ClickType clickType) {
                            clone.getDestination().setType(DestinationType.Portal);
                            clone.getDestination().setId(ChatColor.stripColor(value.getDisplayName()));
                            clone.getDestination().setAdapter(DestinationType.Portal.getInstance());
                            editor.updatePage();
                            fallBack();
                        }

                        @Override
                        public void onClose() {
                        }

                        @Override
                        public void buildItemDescription(List<String> lore) {
                            lore.add("");
                            lore.add(Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Leftclick") + ": §a" + Lang.get("Select"));
                        }
                    }, true);
                } else {
                    clone.getDestination().destroy();
                    this.update();
                }
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        }), new TeleportSoundPage(p, getMainTitle(), clone.getAction(TeleportSoundAction.class).getValue()));

        portal.setEditing(clone);
        this.clone = clone;
        updateControllButtons();

        setCancelSound(new SoundData(Sound.ENTITY_ITEM_BREAK, 0.7F, 1F));
        setOpenSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 1.5F));

        MusicData music0 = new MusicData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 0.9F, 0);
        MusicData music1 = new MusicData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 1.2F, 1);
        music0.setFollower(music1);
        setSuccessSound(music0);
    }

    public static String getMainTitle() {
        return Editor.TITLE_COLOR + Lang.get("Portals_Editor");
    }

    @Override
    public void open(Player player) {
        if(!openForFirstTime) EDITORS.put(player.getName(), this);

        super.open(player);
    }

    @Override
    public void destroy() {
        if(!isClosingForGUI()) EDITORS.remove(getPlayer().getName());
        super.destroy();
    }

    @Override
    public boolean canFinish() {
        return clone == null || !clone.getBlocks().isEmpty() || !clone.getAnimations().isEmpty();
    }

    @Override
    public List<String> finishButtonLoreAddition() {
        if(clone == null) return super.finishButtonLoreAddition();
        return new ArrayList<String>() {{
            add("");
            add("§7" + Lang.get("Portal_Blocks") + ": " + (canFinish() ? "§7" : "§c") + clone.getBlocks().size());
            add("§7" + Lang.get("Animations") + ": " + (canFinish() ? "§7" : "§c") + clone.getAnimations().size());
        }};
    }
}
