package de.codingair.warpsystem.spigot.features.nativeportals;

import de.codingair.codingapi.API;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalBlock;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PortalEditor implements Removable {
    public static final ItemBuilder PORTAL_ITEM = new ItemBuilder(XMaterial.END_STONE).setName(Lang.get("NativePortalEditor_Place_Blocks"));

    private boolean ended = false;
    private final UUID uniqueId = UUID.randomUUID();
    private Player player;
    private NativePortal backup;
    private NativePortal nativePortal;
    private PortalType type;
    private ItemStack old;

    private PortalEditor(Player player) {
        this.player = player;
        API.addRemovable(this);
        this.backup = null;
        this.nativePortal = new NativePortal(type);
    }

    public PortalEditor(Player player, PortalType type) {
        this.player = player;
        API.addRemovable(this);
        this.backup = null;
        this.nativePortal = new NativePortal(type);
    }

    public PortalEditor(Player player, NativePortal nativePortal) {
        this.player = player;
        API.addRemovable(this);
        this.backup = nativePortal;
        this.nativePortal = nativePortal.clone();
    }

    @Override
    public void destroy() {
        this.end();
    }

    @Override
    public Class<? extends Removable> getAbstractClass() {
        return PortalEditor.class;
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
        ItemStack item = this.player.getInventory().getItem(this.player.getInventory().getHeldItemSlot());
        this.old = item == null ? null : item.clone();
        this.player.getInventory().setItem(this.player.getInventory().getHeldItemSlot(), PORTAL_ITEM.getItem());
        this.player.updateInventory();

        this.type = nativePortal.getType();
        this.nativePortal.setType(PortalType.EDIT);

        if(backup != null) this.backup.setVisible(false);
        this.nativePortal.setVisible(true);
    }

    private void update() {
        this.player.getInventory().setItem(this.player.getInventory().getHeldItemSlot(), PORTAL_ITEM.getItem());
        this.nativePortal.update();
    }

    public NativePortal end() {
        if(ended) return null;
        ended = true;

        this.player.getInventory().setItem(this.player.getInventory().getHeldItemSlot(), old == null ? new ItemStack(Material.AIR) : old);
        this.player.updateInventory();
        this.nativePortal.setType(this.type);

        API.removeRemovable(this);

        if(backup != null) {
            Destination dest = this.backup.getDestination();
            this.backup.apply(nativePortal);
            backup.setDestination(dest);

            nativePortal.setVisible(false);
            backup.setVisible(true);

            return this.backup;
        } else {
            nativePortal.setVisible(true);
            return this.nativePortal;
        }
    }

    public void addPosition(Location location) {
        this.nativePortal.addPortalBlock(new PortalBlock(location));
        update();
    }

    public void removePosition(Location location) {
        PortalBlock block = null;

        for(PortalBlock b : this.nativePortal.getBlocks()) {
            if(b.getLocation().equals(location)) {
                block = b;
                break;
            }
        }

        if(block != null) {
            nativePortal.removePortalBlock(block);
            update();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public NativePortal getBackup() {
        return backup;
    }

    public NativePortal getNativePortal() {
        return nativePortal;
    }
}
