package de.codingair.warpsystem.spigot.features.animations.guis.editor;

import de.codingair.codingapi.particles.animations.movables.MovableMid;
import de.codingair.codingapi.particles.animations.movables.PlayerMid;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.animations.utils.AnimationPlayer;
import de.codingair.warpsystem.spigot.features.animations.utils.MenuHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Menu extends HotbarGUI {
    private Animation animation;
    private Animation clone;
    private AnimationPlayer animPlayer;
    private MovableMid mid;

    private MenuParts[] menuParts;
    private MenuHook hook;
    private Particles particles;
    private Buffs buffs;
    private Sounds sounds;
    private HotbarGUI link;

    public static String MINUS_PLUS(String s) {
        return ACTION_BAR(s, "-", "+");
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

    public enum MenuParts {
        PARTICLES(Particles.class, new ItemBuilder(XMaterial.BEACON).setName("§7» §e" + Lang.get("Particle_Effects") + "§7 «").getItem()),
        BUFFS(Buffs.class, new ItemBuilder(XMaterial.SPLASH_POTION).setName("§7» §e" + Lang.get("Potion_Effects") + "§7 «").getItem()),
        SOUNDS(Sounds.class, new ItemBuilder(XMaterial.NOTE_BLOCK).setName("§7» §e" + Lang.get("Sounds") + "§7 «").getItem()),
        ;

        private Class<?> clazz;
        private ItemStack item;

        MenuParts(Class<?> clazz, ItemStack item) {
            this.clazz = clazz;
            this.item = item;
        }

        public ItemStack getItem() {
            return item.clone();
        }
    }

    public Menu(Player player, String name) {
        this(player, new Animation(name));
    }

    public Menu(Player player, String name, HotbarGUI link) {
        this(player, new Animation(name), link);
    }

    public Menu(Player player, Animation animation) {
        this(player, animation, animation.clone());
    }

    public Menu(Player player, Animation animation, HotbarGUI link, MenuParts... menuParts) {
        this(player, animation, animation.clone(), link, menuParts);
    }

    public Menu(Player player, MovableMid mid, Animation animation, HotbarGUI link, MenuParts... menuParts) {
        this(player, mid, animation, animation.clone(), link, menuParts);
    }

    public Menu(Player player, Animation animation, Animation clone) {
        this(player, animation, clone, null, MenuParts.PARTICLES, MenuParts.BUFFS, MenuParts.SOUNDS);
    }

    public Menu(Player player, Animation animation, Animation clone, HotbarGUI link, MenuParts... menuParts) {
        this(player, new PlayerMid(player), animation, clone, link, menuParts);
    }

    public Menu(Player player, MovableMid mid, Animation animation, Animation clone, HotbarGUI link) {
        this(player, mid, animation, clone, link, MenuParts.PARTICLES, MenuParts.BUFFS, MenuParts.SOUNDS);
    }

    public Menu(Player player, MovableMid mid, Animation animation, Animation clone, HotbarGUI link, MenuParts... menuParts) {
        this(player, mid, animation, clone, link, null, menuParts);
    }

    public Menu(Player player, MovableMid mid, Animation animation, Animation clone, HotbarGUI link, MenuHook hook, MenuParts... menuParts) {
        super(player, WarpSystem.getInstance(), 1);

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.LEVEL_UP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.CLICK, 0.5F, 1F));

        this.hook = hook;
        this.menuParts = menuParts;
        this.animation = animation;
        this.clone = clone;
        this.link = link;
        this.mid = mid;

        boolean sound = false;
        for(MenuParts part : menuParts) {
            if(part == MenuParts.SOUNDS) {
                sound = true;
                break;
            }
        }

        this.animPlayer = new AnimationPlayer(player, mid, clone, 5, null, sound);

        particles = new Particles(getPlayer(), this);
        buffs = new Buffs(getPlayer(), this);
        sounds = new Sounds(getPlayer(), this);

        this.animPlayer.setLoop(true);
        this.animPlayer.setRunning(true);

        initialize();
    }

    public void initialize() {
        getPlayer().getInventory().setHeldItemSlot(0);

        setItem(0, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));
        setItem(1, new ItemComponent(new ItemBuilder(XMaterial.NAME_TAG).setName("§7" + Lang.get("Name") + ": '§r" + clone.getName() + "§7'").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    @Override
                    public void onClick(AnvilClickEvent e) {
                        String input = e.getInput();

                        if(input == null) {
                            player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                            return;
                        }

                        if(AnimationManager.getInstance().existsAnimation(input)) {
                            player.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                            return;
                        }

                        clone.setName(input);
                        updateDisplayName(ic, "§7" + Lang.get("Name") + ": '§r" + clone.getName() + "§7'");
                        e.setClose(true);
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                    }
                }, new ItemBuilder(Material.PAPER).setName(clone.getName().replace("§", "&")).getItem());
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(getPlayer(), "§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Change_Name"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(getPlayer());
            }
        }));

        int slot = 2;
        for(MenuParts menuPart : menuParts) {
            if(slot == 5) break;

            ItemComponent ic = new ItemComponent(menuPart.getItem());
            switch(menuPart) {
                case PARTICLES:
                    ic.setLink(this.particles);
                    break;

                case BUFFS:
                    ic.setLink(this.buffs);
                    break;

                case SOUNDS:
                    ic.setLink(this.sounds);
                    break;
            }
            setItem(slot, ic, false);

            slot++;
        }

        setItem(6, new ItemComponent(new ItemBuilder(XMaterial.LIME_TERRACOTTA).setName("§7» §a" + Lang.get("Save") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                String id = animation.getName();

                animation.apply(clone);
                clone.destroy();

                if(!AnimationManager.getInstance().existsAnimation(id)) {
                    //register
                    AnimationManager.getInstance().addAnimation(animation);
                }

                getPlayer().sendMessage(Lang.getPrefix() + "§a" + Lang.get("Changes_have_been_saved"));

                animPlayer.setLoop(false);
                animPlayer.setRunning(false);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
            }
        }).setLink(link).setCloseOnClick(link == null));

        setItem(7, new ItemComponent(new ItemBuilder(XMaterial.RED_TERRACOTTA).setName("§7» §c" + Lang.get("Cancel") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                animPlayer.setLoop(false);
                animPlayer.setRunning(false);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
            }
        }).setLink(link).setCloseOnClick(link == null));

        if(hook != null) hook.onInitialize(this, getPlayer());
    }

    public Animation getAnimation() {
        return animation;
    }

    public Animation getClone() {
        return clone;
    }

    public AnimationPlayer getAnimPlayer() {
        return animPlayer;
    }

    Particles getParticles() {
        return particles;
    }

    Buffs getBuffs() {
        return buffs;
    }

    @Override
    public void open(boolean sound) {
        super.open(sound);
        setStartSlot(-1);
    }
}
