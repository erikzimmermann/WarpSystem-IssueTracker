package de.codingair.warpsystem.spigot.features.animations.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.guis.list.GUIList;
import de.codingair.warpsystem.spigot.base.guis.list.ListItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AnimationList extends GUIList<Animation> {
    public AnimationList(Player p) {
        super(p, "§c" + Lang.get("Animations") + "§7- §c" + Lang.get("List") + " §7(%CURRENT%/%MAX%)", true);
    }

    @Override
    public void addListItems(List<ListItem<Animation>> listItems) {
        for(Animation animation : AnimationManager.getInstance().getAnimationList()) {
            listItems.add(new ListItem<Animation>(animation) {
                @Override
                public ItemStack buildItem() {

                    return new ItemBuilder(XMaterial.BLAZE_POWDER)
                            .setName("§7\"§r" + ChatColor.highlight(animation.getName(), getSearched(), "§e§n", "§r", true) + "§7\"")
                            .setLore("")
                            .addLore("§7" + Lang.get("Particle_Effects") + ": " + ChatColor.highlight(animation.getParticleParts().size() + "", getSearched(), "§e§n", "§7", true))
                            .addLore("§7" + Lang.get("Potion_Effects") + ": " + ChatColor.highlight(animation.getBuffList().size() + "", getSearched(), "§e§n", "§7", true))
                            .getItem();
                }

                @Override
                public void onClick(Animation value, ClickType clickType) {
                    AnimationList.this.onClick(value, clickType);
                }

                @Override
                public boolean isSearched(String searching) {
                    searching = searching.toLowerCase();
                    return animation.getName().toLowerCase().contains(searching)
                            || (animation.getParticleParts().size() + "").contains(searching)
                            || (animation.getBuffList().size() + "").contains(searching);
                }
            });
        }
    }
}
