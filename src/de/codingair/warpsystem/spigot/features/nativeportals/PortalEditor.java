package de.codingair.warpsystem.spigot.features.nativeportals;

import de.codingair.codingapi.API;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalBlock;
import de.codingair.warpsystem.spigot.features.nativeportals.utils.PortalType;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PortalEditor implements Removable {
    public static final ItemBuilder PORTAL_ITEM = new ItemBuilder(XMaterial.END_STONE).setName(Lang.get("NativePortalEditor_Place_Blocks"));

    private final UUID uniqueId = UUID.randomUUID();
    private Player player;
    private Portal backup;
    private Portal portal;
    private PortalType type;
    private ItemStack old;

    private PortalEditor(Player player) {
        this.player = player;
        API.addRemovable(this);
        this.backup = null;
        this.portal = new Portal(type);
    }

    public PortalEditor(Player player, PortalType type) {
        this(player);
        this.backup = null;
        this.portal = new Portal(type);
    }

    public PortalEditor(Player player, Portal portal) {
        this(player);
        this.backup = portal;
        this.portal = portal.clone();
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

        this.type = portal.getType();
        this.portal.setType(PortalType.EDIT);

        if(backup != null) this.backup.setVisible(false);
        this.portal.setVisible(true);
    }

    private void update() {
        this.player.getInventory().setItem(this.player.getInventory().getHeldItemSlot(), PORTAL_ITEM.getItem());
        this.portal.update();
    }

    public Portal end() {
        this.player.getInventory().setItem(this.player.getInventory().getHeldItemSlot(), old == null ? new ItemStack(Material.AIR) : old);
        this.player.updateInventory();
        this.portal.setType(this.type);

        API.removeRemovable(this);

        if(backup != null) {
            this.backup.apply(portal);
            portal.setVisible(false);
            backup.setVisible(true);

            return this.backup;
        } else {
            portal.setVisible(true);
            return this.portal;
        }
    }

    public void addPosition(Location location) {
        this.portal.addPortalBlock(new PortalBlock(location));
        update();
    }

    public void removePosition(Location location) {
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
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Portal getBackup() {
        return backup;
    }

    public Portal getPortal() {
        return portal;
    }
}
