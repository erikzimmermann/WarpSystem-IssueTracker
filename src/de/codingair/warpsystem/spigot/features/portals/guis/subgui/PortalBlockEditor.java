package de.codingair.warpsystem.spigot.features.portals.guis.subgui;

import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.player.gui.PlayerItem;
import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.portals.utils.BlockType;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import de.codingair.warpsystem.spigot.features.portals.utils.PortalBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PortalBlockEditor implements Removable {
    private final UUID uniqueId = UUID.randomUUID();
    private boolean ended = false;
    private final Player player;
    private final Portal portal;
    private ItemStack[] old;

    private BukkitRunnable alignRunnable;
    private final List<Block> alignTo = new ArrayList<>();
    private boolean show = true;

    public PortalBlockEditor(Player player, Portal portal) {
        this.player = player;
        API.addRemovable(this);
        this.portal = portal;

        this.alignRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(!show) return;

                for(Block b : alignTo) {
                    Particle.VILLAGER_HAPPY.send(b.getLocation().add(0.5, 0.5, 0.5), player);
                }
            }
        };

        this.alignRunnable.runTaskTimer(WarpSystem.getInstance(), 5, 5);
    }

    @Override
    public void destroy() {
        this.end();
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public JavaPlugin getPlugin() {
        return WarpSystem.getInstance();
    }

    public void init() {
        this.old = new ItemStack[9];

        for(int i = 0; i < 9; i++) {
            old[i] = this.player.getInventory().getItem(i);
            this.player.getInventory().setItem(i, new ItemStack(Material.AIR));
        }

        int slot = 1;
        for(BlockType value : BlockType.values()) {
            if(value == BlockType.CUSTOM) continue;
            this.player.getInventory().setItem(slot++, value.getEditMaterial().setName(value.getName()).getItem());
        }

        for(PortalBlock block : portal.getBlocks()) {
            if(block.getType() == BlockType.CUSTOM) {
                this.alignTo.add(block.getLocation().getBlock());
            }
        }

        PlayerItem item;
        this.player.getInventory().setItem(++slot, item = new PlayerItem(WarpSystem.getInstance(), player, new ItemBuilder(XMaterial.GHAST_TEAR).setName("§7" + ChatColor.stripColor(BlockType.CUSTOM.getName()) + ": §c-").getItem()) {
            private long last = 0;

            @Override
            public void onInteract(PlayerInteractEvent e) {
                if(System.currentTimeMillis() - last < 50) return;
                else last = System.currentTimeMillis();

                e.setCancelled(true);
                Block b = player.getTargetBlock((Set<Material>) null, 10);
                if(b != null && b.getType() != XMaterial.AIR.parseMaterial() && b.getType() != XMaterial.VOID_AIR.parseMaterial() && b.getType() != XMaterial.CAVE_AIR.parseMaterial() && b.getType() != XMaterial.CHEST.parseMaterial() && b.getType() != XMaterial.TRAPPED_CHEST.parseMaterial()) {
                    Material m = b.getType();

                    IReflection.MethodAccessor isFuel = IReflection.getSaveMethod(Material.class, "isFuel", boolean.class);
                    boolean fuel = isFuel != null && (boolean) isFuel.invoke(m);

                    if(!m.isOccluding() && (fuel || !m.isSolid())) {
                        List<Location> locations = new ArrayList<>();
                        Location l = b.getLocation();
                        locations.add(l);

                        if(m.name().toLowerCase().contains("door")) {
                            Location other = l.clone().add(0, 1, 0);
                            if(!other.getBlock().getType().name().toLowerCase().contains("door")) other = null;

                            if(other == null) {
                                other = l.clone().subtract(0, 1, 0);
                                if(!other.getBlock().getType().name().toLowerCase().contains("door")) other = null;
                            }

                            if(other != null) locations.add(other.getBlock().getLocation());
                        }

                        for(Location location : locations) {
                            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                                if(!alignTo.remove(location.getBlock())) {
                                    alignTo.add(location.getBlock());

                                    portal.addPortalBlock(new PortalBlock(new de.codingair.codingapi.tools.Location(location), BlockType.CUSTOM));

                                    changeToAlignmentBlock(player, location);
                                } else {
                                    sendBlockChange(player, location.getBlock());
                                    portal.removePortalBlock(location);
                                }
                            }, 1);
                        }
                    }
                }
            }

            @Override
            public void onHover(PlayerItemHeldEvent e) {
                setAlignBlocks(true);
            }

            @Override
            public void onUnhover(PlayerItemHeldEvent e) {
                setAlignBlocks(false);
            }
        }.setFreezed(true));

        if(this.player.getInventory().getHeldItemSlot() == 0 || this.player.getInventory().getHeldItemSlot() == 6 || this.player.getInventory().getHeldItemSlot() == 8) {
            this.player.getInventory().setHeldItemSlot(1);
        } else if(this.player.getInventory().getHeldItemSlot() == 7) {
            item.onHover(null);
        }
    }

    public void update() {
        int slot = 1;
        for(BlockType value : BlockType.values()) {
            if(value == BlockType.CUSTOM) continue;
            this.player.getInventory().setItem(slot++, value.getEditMaterial().setName(value.getName()).getItem());
        }
    }

    public Portal end() {
        if(ended) return null;
        ended = true;

        if(alignRunnable != null) {
            alignRunnable.cancel();
            alignRunnable = null;

            setAlignBlocks(false);
            alignTo.clear();
        }

        List<PlayerItem> items = API.getRemovables(getPlayer(), PlayerItem.class);
        for(PlayerItem item : items) {
            item.destroy();
        }
        items.clear();

        int slot = 0;
        for(ItemStack itemStack : old) {
            this.player.getInventory().setItem(slot++, itemStack == null ? new ItemStack(Material.AIR) : itemStack);
        }
        this.player.updateInventory();

        API.removeRemovable(this);
        return portal;
    }

    public void addPosition(Location location, BlockType type) {
        this.portal.addPortalBlock(new PortalBlock(new de.codingair.codingapi.tools.Location(location), type));
        update();
    }

    public boolean removePosition(Location location) {
        PortalBlock block = null;

        for(PortalBlock b : this.portal.getBlocks()) {
            if(b.getLocation().equals(location)) {
                block = b;
                break;
            }
        }

        if(block != null) {
            portal.removePortalBlock(block);
            update();
            return true;
        }

        return false;
    }

    public void setAlignBlocks(boolean show) {
        this.show = show;

        List<Block> alignTo = new ArrayList<>(this.alignTo);
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            if(show) {
                for(Block b : alignTo) {
                    changeToAlignmentBlock(getPlayer(), b.getLocation());
                }
            } else {
                for(Block b : alignTo) {
                    sendBlockChange(getPlayer(), b);
                }
            }

            alignTo.clear();
        }, 1);
    }

    private void sendBlockChange(Player player, Block b) {
        if(Version.getVersion().isBiggerThan(Version.v1_12)) {
            //block data
            Class<?> blockDataClass = IReflection.getClass(IReflection.ServerPacket.BUKKIT_PACKET, "block.data.BlockData");
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[] {org.bukkit.Location.class, blockDataClass});
            IReflection.MethodAccessor getData = IReflection.getMethod(Block.class, "getBlockData", blockDataClass, new Class[0]);

            sendBlockChange.invoke(player, b.getLocation(), getData.invoke(b));
        } else {
            //loc, mat, byte
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[] {org.bukkit.Location.class, Material.class, byte.class});
            IReflection.MethodAccessor getData = IReflection.getMethod(Block.class, "getData", byte.class, new Class[0]);
            sendBlockChange.invoke(player, b.getLocation(), b.getType(), getData.invoke(b));
        }
    }

    private void changeToAlignmentBlock(Player player, Location loc) {
        if(Version.getVersion().isBiggerThan(Version.v1_12)) {
            //block data
            Class<?> blockDataClass = IReflection.getClass(IReflection.ServerPacket.BUKKIT_PACKET, "block.data.BlockData");
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[] {org.bukkit.Location.class, blockDataClass});
            IReflection.MethodAccessor createBlockData = IReflection.getMethod(Material.class, "createBlockData", blockDataClass, new Class[0]);

            sendBlockChange.invoke(player, loc, createBlockData.invoke(XMaterial.GLASS.parseMaterial()));
        } else {
            //loc, mat, byte
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[] {org.bukkit.Location.class, Material.class, byte.class});
            sendBlockChange.invoke(player, loc, XMaterial.GLASS.parseMaterial(), (byte) 1);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Portal getPortal() {
        return portal;
    }
}
