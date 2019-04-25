package de.codingair.warpsystem.spigot.features.signs.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.editor.Backup;
import de.codingair.warpsystem.spigot.base.editor.Editor;
import de.codingair.warpsystem.spigot.base.editor.ShowIcon;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.signs.guis.pages.OptionPage;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WarpSignGUI extends Editor<WarpSign> {

    public WarpSignGUI(Player p, WarpSign sign) {
        super(p, WarpSystem.getInstance(), sign.clone(), new Backup<WarpSign>(sign) {
            @Override
            public void applyTo(WarpSign clone) {
                sign.setDestination(clone.getDestination());
                sign.setPermission(clone.getPermission());
            }
        }, new ShowIcon() {
            @Override
            public ItemStack buildIcon() {
                ItemBuilder builder = new ItemBuilder(XMaterial.SIGN);

                Sign s = (Sign) sign.getLocation().getBlock().getState();
                for(String line : s.getLines()) {
                    builder.addText("§7'§r" + line + "§7'");
                }

                return builder.getItem();
            }
        }, new OptionPage(p, sign));
    }

    public static String getMainTitle() {
        return "§c§l" + Lang.get("WarpSigns");
    }
}
