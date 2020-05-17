package de.codingair.warpsystem.spigot.features.spawn.guis;

import de.codingair.codingapi.server.sounds.MusicData;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.spawn.guis.pages.POptions;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import org.bukkit.entity.Player;

public class SpawnEditor extends Editor<Spawn> {
    public SpawnEditor(Player p, Spawn spawn) {
        this(p, spawn, spawn.clone());
    }

    private SpawnEditor(Player p, Spawn spawn, Spawn clone) {
        super(p, clone, new Backup<Spawn>(spawn) {
            @Override
            public void applyTo(Spawn clone) {
                if(WarpSystem.getInstance().isOnBungeeCord() && clone.getUsage().getName().contains("/spawn")) {
                    String server = SpawnManager.getInstance().getSpawnServer();

                    if(server != null && !WarpSystem.getInstance().getCurrentServer().equals(server)) {
                        clone.setUsage(clone.getUsage().getWithoutSpawnCommand());
                    }
                }

                spawn.apply(clone);

                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    String s = WarpSystem.getInstance().getCurrentServer();

                    String spawnServer = SpawnManager.getInstance().getSpawnServer();
                    String respawnServer = SpawnManager.getInstance().getRespawnServer();


                    if(spawn.getUsage().isBungee()) spawnServer = s;
                    else if(s.equals(spawnServer)) spawnServer = null;

                    if(spawn.getRespawnUsage().isBungee()) respawnServer = s;
                    else if(s.equals(respawnServer)) respawnServer = null;

                    SpawnManager.getInstance().updateGlobalOptions(spawnServer, respawnServer);
                }
            }

            @Override
            public void cancel(Spawn clone) {
                clone.destroy();
            }
        }, () -> new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem(), new POptions(p, clone));

        setCancelSound(new SoundData(Sound.ENTITY_ITEM_BREAK, 0.7F, 1F));
        setOpenSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 1.5F));

        MusicData music0 = new MusicData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 0.9F, 0);
        MusicData music1 = new MusicData(Sound.ENTITY_PLAYER_LEVELUP, 0.7F, 1.2F, 1);
        music0.setFollower(music1);
        setSuccessSound(music0);
    }

    public static String getMainTitle() {
        return Editor.TITLE_COLOR + Lang.get("Spawn");
    }
}
