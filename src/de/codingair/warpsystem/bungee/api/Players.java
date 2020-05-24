package de.codingair.warpsystem.bungee.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Players {
    private static final Cache<String, ProxiedPlayer> CACHE = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build();

    public static ProxiedPlayer getPlayer(String name) {
        ProxiedPlayer found = BungeeCord.getInstance().getPlayer(name);
        if(found != null) return found;
        String lowerName = name.toLowerCase(Locale.ENGLISH);

        found = CACHE.getIfPresent(lowerName);
        if(found != null) return found;

        int delta = 2147483647;
        for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
            if(player.getName().toLowerCase(Locale.ENGLISH).startsWith(lowerName)) {
                int curDelta = Math.abs(player.getName().length() - lowerName.length());
                if(curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }

                if(curDelta == 0) {
                    break;
                }
            }
        }

        if(found != null) CACHE.put(lowerName, found);
        return found;
    }
}
