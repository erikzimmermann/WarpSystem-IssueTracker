package de.codingair.warpsystem.api;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportAPI {

    public static void teleport(Player player, Location location, String locationName, Runnable teleported) {
        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(location)), locationName);
        options.addCallback(new Callback<Result>() {
            @Override
            public void accept(Result object) {
                teleported.run();
            }
        });

        WarpSystem.getInstance().getTeleportManager().teleport(player, options);
    }

}
