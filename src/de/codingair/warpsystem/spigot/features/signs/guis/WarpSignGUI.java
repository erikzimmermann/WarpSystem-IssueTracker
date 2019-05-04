package de.codingair.warpsystem.spigot.features.signs.guis;

import de.codingair.codingapi.player.gui.sign.SignTools;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.editor.Backup;
import de.codingair.warpsystem.spigot.base.editor.Editor;
import de.codingair.warpsystem.spigot.base.editor.ShowIcon;
import de.codingair.warpsystem.spigot.base.editor.pages.DestinationPage;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.signs.guis.pages.OptionPage;
import de.codingair.warpsystem.spigot.features.signs.managers.SignManager;
import de.codingair.warpsystem.spigot.features.signs.utils.WarpSign;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WarpSignGUI extends Editor<WarpSign> {

    public WarpSignGUI(Player p, WarpSign sign, WarpSign clone) {
        super(p, WarpSystem.getInstance(), clone, new Backup<WarpSign>(sign) {
            private String[] backupLines = ((Sign) sign.getLocation().getBlock().getState()).getLines();

            @Override
            public void applyTo(WarpSign clone) {
                sign.setDestination(clone.getDestination());
                sign.setPermission(clone.getPermission());

                if(SignManager.getInstance().getByLocation(sign.getLocation()) == null) {
                    SignManager.getInstance().getWarpSigns().add(sign);
                }

                Sign s = (Sign) sign.getLocation().getBlock().getState();

                SignTools.updateSign(s, s.getLines());
            }

            @Override
            public void cancel(WarpSign value) {
                Sign s = ((Sign) sign.getLocation().getBlock().getState());


                SignTools.updateSign(s, backupLines);
            }
        }, new ShowIcon(((Sign) sign.getLocation().getBlock().getState()).getLines()), new OptionPage(p, clone), new DestinationPage(p, getMainTitle(), clone.getDestination()));

    }

    public static class ShowIcon implements de.codingair.warpsystem.spigot.base.editor.ShowIcon {
        private String[] lines;

        public ShowIcon(String[] lines) {
            this.lines = lines;
        }

        public void applyLines(String[] lines) {
            this.lines = lines;
        }

        @Override
        public ItemStack buildIcon() {
            ItemBuilder builder = new ItemBuilder(XMaterial.OAK_SIGN);

            for(String line : lines) {
                builder.addText("§7'§r" + ChatColor.translateAlternateColorCodes('&', line) + "§7'");
            }

            return builder.getItem();
        }
    }

    public static String getMainTitle() {
        return "§c§l" + Lang.get("WarpSigns");
    }
}
