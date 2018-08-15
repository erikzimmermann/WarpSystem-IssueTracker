package de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils;

import de.codingair.codingapi.serializable.SerializableItemStack;
import de.codingair.codingapi.server.Color;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.transfer.serializeable.icons.SIcon;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public abstract class Icon implements Serializable {
    static final long serialVersionUID = 1L;

    String name;
    SerializableItemStack item;
    int slot;
    boolean disabled = false;

    public Icon() {
    }

    public Icon(String name, ItemStack item, int slot) {
        this.name = name;
        this.item = new SerializableItemStack(item);
        this.slot = slot;
    }

    public Icon(SIcon i) {
        this.name = i.getName();
        this.slot = i.getSlot();
        this.item = new SerializableItemStack(
                ItemBuilder
                        .getFromJSON(
                                i.getItemData()
                        )
                        .getItem());

//        ItemBuilder b = new ItemBuilder(Material.valueOf(i.getType()));
//        b.setData(i.getData());
//        b.setDurability(i.getDurability());
//        b.setAmount(i.getAmount());
//
//        if(i.getEnchantments() != null) i.getEnchantments().forEach((ench, level) -> b.addEnchantment(Enchantment.getByName(ench), level));
//        if(i.getColor() != null) b.setColor(DyeColor.valueOf(i.color));
//        if(i.getLore() != null && !i.getLore().isEmpty()) b.addLore(i.getLore());
//
//        b.setHideEnchantments(i.isHideEnchantments());
//        b.setHideName(i.isHideName());
//        b.setHideStandardLore(i.isHideStandardLore());
//
//        if(i.getPotionData() != null)
    }

    public SIcon getSerializable() {
        SIcon s = new SIcon();

        s.setName(this.name);
        s.setSlot(this.slot);
        s.setItemData(this.item.getData());

//        ItemStack item = this.item.getItem();
//
//        s.type = item.getType().name();
//        s.data = item.getData().getData();
//        s.durability = item.getDurability();
//        s.amount = item.getAmount();
//
//        if(item.getEnchantments().size() > 0) {
//            s.enchantments = new HashMap<>();
//            item.getEnchantments().forEach((ench, level) -> {
//                s.enchantments.put(ench.getName(), level);
//            });
//        }
//
//        if(item.hasItemMeta()) {
//            if(s.enchantments == null) s.enchantments = new HashMap<>();
//            item.getItemMeta().getEnchants().forEach((ench, level) -> {
//                s.enchantments.put(ench.getName(), level);
//            });
//
//            try {
//                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
//                s.color = DyeColor.getByColor(meta.getColor()).name();
//            } catch(Exception ignored) {
//            }
//
//            if(item.getItemMeta().hasLore()) {
//                s.lore = new ArrayList<>();
//                s.lore.addAll(item.getItemMeta().getLore());
//            }
//            s.hideEnchantments = item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS);
//            s.hideStandardLore = (item.getItemMeta().getItemFlags().size() == 1 && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) || item.getItemMeta().getItemFlags().size() > 1;
//            if(item.getItemMeta().getDisplayName() != null)
//                s.hideName = item.getItemMeta().getDisplayName().equals("§0");
//        }
//
//        if(item.getType().name().toUpperCase().contains("POTION")) {
//            s.potionData = new PotionData(item);
//        }
//
//        try {
//            ItemMeta meta = item.getItemMeta();
//            IReflection.FieldAccessor profile = IReflection.getField(meta.getClass(), "profile");
//            s.skullOwner = (GameProfile) profile.get(meta);
//        } catch(Exception ignored) {
//        }

        return s;
    }

    public void setItem(ItemStack item) {
        this.item = new SerializableItemStack(item);
    }

    public abstract IconType getType();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItem() {
        return new ItemBuilder(item.getItem()).setHideStandardLore(true).getItem();
    }

    public String getNameWithoutColor() {
        return Color.removeColor(ChatColor.translateAlternateColorCodes('&', this.name));
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
