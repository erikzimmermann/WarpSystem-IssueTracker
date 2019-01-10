package de.codingair.warpsystem.spigot.api;

import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.reflections.PacketUtils;
import de.codingair.warpsystem.spigot.api.blocks.listeners.RuleListener;
import de.codingair.warpsystem.spigot.api.packetreader.GlobalPacketReaderListener;
import de.codingair.warpsystem.spigot.api.packetreader.GlobalPacketReaderManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotAPI {
    private static SpigotAPI instance;

    private GlobalPacketReaderManager globalPacketReaderManager = new GlobalPacketReaderManager();

    public static SpigotAPI getInstance() {
        if(instance == null) instance = new SpigotAPI();
        return instance;
    }

    public void onEnable(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new RuleListener(), plugin);
        this.globalPacketReaderManager.onEnable();

        Bukkit.getPluginManager().registerEvents(new GlobalPacketReaderListener(), plugin);
    }

    public void onDisable(JavaPlugin plugin) {
        this.globalPacketReaderManager.onDisable();
    }

    public GlobalPacketReaderManager getGlobalPacketReaderManager() {
        return globalPacketReaderManager;
    }

    public boolean silentTeleport(Player player, Location to) {
        Location from = player.getLocation();

        Object entity = PacketUtils.getEntityPlayer(player);

        if(player.getHealth() != 0.0D && !player.isDead()) {
            IReflection.MethodAccessor isDisconnected = IReflection.getMethod(PacketUtils.PlayerConnectionClass, "isDisconnected", boolean.class, new Class[] {});
            IReflection.MethodAccessor mount = IReflection.getMethod(PacketUtils.EntityPlayerClass, "mount", new Class[] {PacketUtils.EntityClass});
            IReflection.MethodAccessor teleport = IReflection.getMethod(PacketUtils.PlayerConnectionClass, "teleport", new Class[] {Location.class});
            IReflection.FieldAccessor activeContainer = IReflection.getField(PacketUtils.EntityPlayerClass, "activeContainer");
            IReflection.FieldAccessor defaultContainer = IReflection.getField(PacketUtils.EntityPlayerClass, "defaultContainer");
            IReflection.FieldAccessor dimension = IReflection.getField(PacketUtils.WorldServerClass, "dimension");

            if(PacketUtils.playerConnection.get(entity) != null && !((boolean) isDisconnected.invoke(PacketUtils.playerConnection.get(entity)))) {

                mount.invoke(entity, (Object) null);
                Object fromWorld = PacketUtils.getWorldServer(from.getWorld());
                Object toWorld = PacketUtils.getWorldServer(to.getWorld());

                if(activeContainer.get(entity) != defaultContainer.get(entity)) {
                    player.closeInventory();
                }

                if(fromWorld == toWorld) teleport.invoke(PacketUtils.playerConnection.get(entity), to);
                else PacketUtils.moveToWorld.invoke(PacketUtils.getHandleCraftServer.invoke(PacketUtils.CraftServerClass.cast(player.getServer())), entity, dimension.get(toWorld), true, to, true);

                return true;
            } else return false;
        } else return false;
    }
}
