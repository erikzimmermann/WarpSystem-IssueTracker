package de.codingair.warpsystem.transfer.serializeable.icons;

import de.codingair.warpsystem.transfer.serializeable.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SIcon implements Serializable {
    private String name;
    private int slot;
    private String itemData;

//    public String type;
//    public byte data = 0;
//    public short durability = 0;
//    public int amount = 1;
//    public String color = null;
//    public PotionData potionData = null;
//
//    public GameProfile skullOwner = null;
//    public List<String> lore = null;
//    public HashMap<String, Integer> enchantments = null;
//    public boolean hideStandardLore = false;
//    public boolean hideEnchantments = false;
//    public boolean hideName = false;

    public SIcon() {
    }

    public SIcon(SIcon s) {
        this.name = s.name;
        this.slot = s.slot;
        this.itemData = s.itemData;
//        this.type = s.type;
//        this.data = s.data;
//        this.durability = s.durability;
//        this.amount = s.amount;
//        this.color = s.color;
//        this.potionData = s.potionData;
//        this.skullOwner = s.skullOwner;
//        this.lore = s.lore;
//        this.enchantments = s.enchantments;
//        this.hideStandardLore = s.hideStandardLore;
//        this.hideEnchantments = s.hideEnchantments;
//        this.hideName = s.hideName;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.slot);
        out.writeUTF(this.itemData);
//            out.writeUTF(type);
//            out.writeByte(this.data);
//            out.writeShort(this.durability);
//            out.writeInt(this.amount);
//
//            out.writeBoolean(this.color != null);
//            if(this.color != null) out.writeUTF(this.color);
//
//            out.writeBoolean(this.potionData != null);
//            if(this.potionData != null) {
//                out.writeUTF(this.potionData.toJSONString());
//            }
//
//            out.writeBoolean(this.skullOwner != null);
//            if(this.skullOwner != null) {
//                out.writeUTF(GameProfileUtils.gameProfileToString(this.skullOwner));
//            }
//
//            out.writeInt(this.lore == null ? 0 : this.lore.size());
//            for(String s : this.lore) {
//                out.writeUTF(s);
//            }
//
//            out.writeInt(this.enchantments == null ? 0 : this.enchantments.size());
//            for(String e : this.enchantments.keySet()) {
//                out.writeUTF(e);
//            }
//            for(int level : this.enchantments.values()) {
//                out.writeInt(level);
//            }
//
//            out.writeBoolean(this.hideStandardLore);
//            out.writeBoolean(this.hideEnchantments);
//            out.writeBoolean(this.hideName);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.slot = in.readInt();
        this.itemData = in.readUTF();
//            this.type = in.readUTF();
//            this.data = in.readByte();
//            this.durability = in.readShort();
//            this.amount = in.readInt();
//            this.color = in.readBoolean() ? in.readUTF() : null;
//            this.potionData = in.readBoolean() ? PotionData.fromJSONString(in.readUTF()) : null;
//            this.skullOwner = in.readBoolean() ? GameProfileUtils.gameProfileFromJSON(in.readUTF()) : null;
//
//            this.lore = new ArrayList<>();
//            int size = in.readInt();
//            for(int i = 0; i < size; i++) {
//                lore.add(in.readUTF());
//            }
//
//            List<String> enchantmentList = new ArrayList<>();
//            this.enchantments = new HashMap<>();
//            size = in.readInt();
//            for(int i = 0; i < size; i++) {
//                enchantmentList.add(in.readUTF());
//            }
//
//            for(int i = 0; i < size; i++) {
//                enchantments.put(enchantmentList.remove(0), in.readInt());
//            }
//
//            this.hideStandardLore = in.readBoolean();
//            this.hideEnchantments = in.readBoolean();
//            this.hideName = in.readBoolean();
    }

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

    public String getItemData() {
        return itemData;
    }

    public void setItemData(String itemData) {
        this.itemData = itemData;
    }
    //    public String getType() {
//        return type;
//    }
//
//    public void setType(String String) {
//        this.type = type;
//    }
//
//    public byte getData() {
//        return data;
//    }
//
//    public void setData(byte data) {
//        this.data = data;
//    }
//
//    public short getDurability() {
//        return durability;
//    }
//
//    public void setDurability(short durability) {
//        this.durability = durability;
//    }
//
//    public int getAmount() {
//        return amount;
//    }
//
//    public void setAmount(int amount) {
//        this.amount = amount;
//    }
//
//    public String getColor() {
//        return color;
//    }
//
//    public void setColor(String color) {
//        this.color = color;
//    }
//
//    public PotionData getPotionData() {
//        return potionData;
//    }
//
//    public void setPotionData(PotionData potionData) {
//        this.potionData = potionData;
//    }
//
//    public GameProfile getSkullOwner() {
//        return skullOwner;
//    }
//
//    public void setSkullOwner(GameProfile skullOwner) {
//        this.skullOwner = skullOwner;
//    }
//
//    public List<String> getLore() {
//        return lore;
//    }
//
//    public void setLore(List<String> lore) {
//        this.lore = lore;
//    }
//
//    public HashMap<String, Integer> getEnchantments() {
//        return enchantments;
//    }
//
//    public void setEnchantments(HashMap<String, Integer> enchantments) {
//        this.enchantments = enchantments;
//    }
//
//    public boolean isHideStandardLore() {
//        return hideStandardLore;
//    }
//
//    public void setHideStandardLore(boolean hideStandardLore) {
//        this.hideStandardLore = hideStandardLore;
//    }
//
//    public boolean isHideEnchantments() {
//        return hideEnchantments;
//    }
//
//    public void setHideEnchantments(boolean hideEnchantments) {
//        this.hideEnchantments = hideEnchantments;
//    }
//
//    public boolean isHideName() {
//        return hideName;
//    }
//
//    public void setHideName(boolean hideName) {
//        this.hideName = hideName;
//    }
}
