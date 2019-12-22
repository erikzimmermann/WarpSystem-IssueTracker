package de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis;

import de.codingair.codingapi.server.sounds.MusicData;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.tradesystem.utils.money.AdapterType;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.money.MoneyAdapterType;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.pages.PAppearance;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.pages.POptions;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.guis.pages.PTrusted;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.tempwarps.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;

public class PWEditor extends Editor<PlayerWarp> {
    private PlayerWarp old;
    private PlayerWarp clone;
    private boolean creating;

    public PWEditor(Player p, String name) {
        this(p, new PlayerWarp(p, name).setPublic(PlayerWarpManager.getInstance().isFirstPublic()).setTime(PlayerWarpManager.getInstance().getMinTime()));
    }

    public PWEditor(Player p, PlayerWarp warp) {
        this(p, warp, warp.clone());
    }

    public PWEditor(Player p, PlayerWarp warp, PlayerWarp clone) {
        super(p, clone, new Backup<PlayerWarp>(warp) {
            @Override
            public void applyTo(PlayerWarp clone) {
                boolean creating = !PlayerWarpManager.getInstance().exists(warp.getName());
                int costs = calculateCosts(creating, warp, clone);
                MoneyAdapterType.getActive().withdraw(p, costs);

                warp.apply(clone);
                clone.destroy();

                if(creating) PlayerWarpManager.getInstance().add(warp);
            }

            @Override
            public void cancel(PlayerWarp clone) {
                clone.destroy();
            }
        }, () -> clone.getItem().getItem(), new PAppearance(p, clone), new POptions(p, clone), new PTrusted(p, clone));

        this.old = warp;
        this.clone = clone;
        this.creating = !PlayerWarpManager.getInstance().exists(warp.getName());

        setOpenSound(new SoundData(Sound.LEVEL_UP, 0.7F, 1.5F));
        setCancelSound(new SoundData(Sound.ITEM_BREAK, 0.7F, 1F));

        MusicData music0 = new MusicData(Sound.LEVEL_UP, 0.7F, 0.9F, 0);
        MusicData music1 = new MusicData(Sound.LEVEL_UP, 0.7F, 1.2F, 1);
        music0.setFollower(music1);
        setSuccessSound(music0);

        initControllButtons();
    }

    private int calculateCosts() {
        return calculateCosts(creating, old, clone);
    }

    private static int calculateCosts(boolean creating, PlayerWarp old, PlayerWarp clone) {
        if(clone == null) return 0;
        int costs = 0;

        if(creating) {
            if(clone.getTeleportMessage() != null) costs += PlayerWarpManager.getInstance().getConfig().getMessageCosts();
            costs += clone.getTeleportCosts() * ((double) PlayerWarpManager.getInstance().getTeleportCosts()) / 100D;
            if(clone.isPublic()) costs += PlayerWarpManager.getInstance().getConfig().getPublicCosts();
        } else {
            if(!old.getName().equals(clone.getName())) costs += PlayerWarpManager.getInstance().getNameChangeCosts();
        }

        return costs;
    }

    @Override
    public void open() {
        super.open();
        setOpenSound(null);
    }

    @Override
    public boolean canFinish() {
        if(this.clone == null) return false;
        return canPay(getPlayer(), calculateCosts());
    }

    @Override
    public String finishButtonNameAddition() {
        int costs = calculateCosts();
        return costs == 0 ? "" : "§7 - " + (canPay(getPlayer(), costs) ? "§a" : "§c") + costs + " " + Lang.get("Coins");
    }

    private static boolean canPay(Player player, double costs) {
        return MoneyAdapterType.getActive().getMoney(player) >= costs;
    }

    @Override
    public String getSuccessMessage() {
        int costs = calculateCosts();
        if(costs == 0) return super.getSuccessMessage();
        else if(creating) return Lang.getPrefix() + Lang.get("Warp_Created").replace("%NAME%", clone.getName()).replace("%PRICE%", costs + "");
        else return Lang.getPrefix() + Lang.get("Warp_Edited_Pay").replace("%NAME%", clone.getName()).replace("%PRICE%", costs + "");
    }

    public PlayerWarp getClone() {
        return clone;
    }

    public static String getMainTitle() {
        return Editor.TITLE_COLOR + Lang.get("Player_Warp_Editor");
    }
}

