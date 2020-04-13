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
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class BuffPart extends HotbarGUI {
    private Menu menu;
    private int slot;

    public BuffPart(Player player, int slot, Menu menu) {
        super(player, WarpSystem.getInstance(), 2);

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));

        this.menu = menu;
        this.slot = slot;
    }

    public static PotionEffectType next(PotionEffectType type) {
        for(int i = 0; i < PotionEffectType.values().length; i++) {
            if(PotionEffectType.values()[i].equals(type)) return i + 1 == PotionEffectType.values().length ? PotionEffectType.values()[0] : PotionEffectType.values()[i + 1];
        }

        throw new IllegalArgumentException("Couldn't found PotionEffectType with type=" + type.getName());
    }

    public static PotionEffectType previous(PotionEffectType type) {
        for(int i = 0; i < PotionEffectType.values().length; i++) {
            if(PotionEffectType.values()[i].equals(type)) {
                return i - 1 < 0 ? PotionEffectType.values()[PotionEffectType.values().length - 1] : PotionEffectType.values()[i - 1];
            }
        }

        throw new IllegalArgumentException("Couldn't found PotionEffectType with type=" + type.getName());
    }

    public void initialize() {
        setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(this.menu.getBuffs()), false);
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));
        setItem(2, new ItemComponent(new ItemBuilder(XMaterial.SPLASH_POTION).setName("§7" + Lang.get("Potion_Effect_Type") + ": '§e" + getPotionName() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getBuff().setType(previous(getBuff().getType()));
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getBuff().setType(next(getBuff().getType()));
                } else return;

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Potion_Effect_Type") + ": '§e" + getPotionName() + "§7'");
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.PREVIOUS_NEXT(Lang.get("Potion_Effect_Type")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(3, new ItemComponent(new ItemBuilder(XMaterial.GUNPOWDER).setName("§7" + Lang.get("Level") + ": §e" + getLevel()).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getBuff().setLevel(getBuff().getLevel() - 1);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getBuff().setLevel(getBuff().getLevel() + 1);
                } else return;

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Level") + ": §e" + getLevel());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Level")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(4, new ItemComponent(new ItemBuilder(XMaterial.CLOCK).setName("§7" + Lang.get("Potion_Effect_Time_before_Teleport") + ": §e" + getTimeBeforeTeleport()).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getBuff().setTimeBeforeTeleport(getBuff().getTimeBeforeTeleport() - 1);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getBuff().setTimeBeforeTeleport(getBuff().getTimeBeforeTeleport() + 1);
                } else return;

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Potion_Effect_Time_before_Teleport") + ": §e" + getTimeBeforeTeleport());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Potion_Effect_Time_before_Teleport")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        setItem(5, new ItemComponent(new ItemBuilder(XMaterial.MILK_BUCKET).setName("§7" + Lang.get("Potion_Effect_Time_after_Teleport") + ": §e" + getTimeAfterTeleport()).getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if(clickType == ClickType.LEFT_CLICK) {
                    getBuff().setTimeAfterTeleport(getBuff().getTimeAfterTeleport() - 1);
                } else if(clickType == ClickType.RIGHT_CLICK) {
                    getBuff().setTimeAfterTeleport(getBuff().getTimeAfterTeleport() + 1);
                } else return;

                menu.getAnimPlayer().update();
                updateDisplayName(ic, "§7" + Lang.get("Potion_Effect_Time_after_Teleport") + ": §e" + getTimeAfterTeleport());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), Menu.MINUS_PLUS(Lang.get("Potion_Effect_Time_after_Teleport")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));
    }

    private int getTimeBeforeTeleport() {
        return getBuff() == null ? null : getBuff().getTimeBeforeTeleport();
    }

    private int getTimeAfterTeleport() {
        return getBuff() == null ? null : getBuff().getTimeAfterTeleport();
    }

    private int getLevel() {
        return getBuff() == null ? null : getBuff().getLevel();
    }

    private String getPotionName() {
        String s = getBuff() == null ? null : getBuff().getType() == null ? null : getBuff().getType().getName();
        if(s == null) return null;

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private Buff getBuff() {
        return menu.getClone().getBuffList().size() <= slot ? null : menu.getClone().getBuffList().get(slot);
    }

    public Menu getMenuGUI() {
        return menu;
    }
}