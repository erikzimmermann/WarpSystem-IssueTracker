package de.codingair.warpsystem.spigot.features.animations.guis.editor;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.utils.Buff;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Buffs extends HotbarGUI {
    private Menu menu;
    private List<Buff> parts;
    private BuffPart[] buffs = new BuffPart[5];

    public Buffs(Player player, Menu menu) {
        super(player, WarpSystem.getInstance(), 2);

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));

        this.menu = menu;
        this.parts = menu.getClone().getBuffList();

        for(int i = 0; i < 5; i++) {
            this.buffs[i] = new BuffPart(player, i, menu);
        }

        initialize();
    }

    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(menu), false);
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        for(int i = 0; i < 5; i++) {
            int id = i;

            if(i < parts.size() + 1) {
                setItem(id + 2, new ItemComponent(new ItemBuilder(parts.size() >= id + 1 ? XMaterial.SPLASH_POTION : XMaterial.BARRIER)
                        .setName("§c" + Lang.get("Potion_Effect") + " #" + (id + 1))
                        .setData((byte) id)
                        .getItem(), new ItemListener() {
                    @Override
                    public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                        if(clickType == ClickType.LEFT_CLICK) {
                            ic.setLink(buffs[id]);

                            if(menu.getClone().getBuffList().size() == id) {
                                menu.getClone().getBuffList().add(new Buff(PotionEffectType.ABSORPTION, 1, 0, 0));
                                buffs[id].initialize();
                                menu.getAnimPlayer().update();

                                ic.setItem(new ItemBuilder(parts.size() >= id + 1 ? XMaterial.SPLASH_POTION : XMaterial.BARRIER)
                                        .setName("§c" + Lang.get("Potion_Effect") + " #" + (id + 1))
                                        .setData((byte) id)
                                        .getItem());

                                initialize();
                            } else buffs[id].initialize();
                        } else {
                            ic.setLink(null);
                            if(clickType == ClickType.RIGHT_CLICK && parts.size() >= id + 1) {
                                parts.remove(id);
                                menu.getAnimPlayer().update();
                                onHover(gui, ic, ic, player);
                                initialize();
                            }
                        }
                    }

                    @Override
                    public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                        if(parts.size() >= id + 1) {
                            MessageAPI.sendActionBar(getPlayer(), Menu.ACTION_BAR(Lang.get("Potion_Effect") + " #" + (id + 1), "§e" + Lang.get("Edit"), "§c" + Lang.get("Delete")), WarpSystem.getInstance(), Integer.MAX_VALUE);
                        } else MessageAPI.sendActionBar(getPlayer(), "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"), WarpSystem.getInstance(), Integer.MAX_VALUE);
                    }

                    @Override
                    public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                        MessageAPI.stopSendingActionBar(getPlayer());
                    }
                }).setLink(this.buffs[id]));
            } else setItem(id + 2, new ItemComponent(new ItemStack(Material.AIR)));
        }
    }

    public Menu getMenuGUI() {
        return menu;
    }
}