package de.codingair.warpsystem.spigot.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.gui.affiliations.Warp;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.spigot.features.portals.Portal;
import de.codingair.warpsystem.spigot.features.signs.WarpSign;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import de.codingair.warpsystem.spigot.utils.Teleport;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeleportManager {
    private List<Portal> portals = new ArrayList<>();
    private List<WarpSign> warpSigns = new ArrayList<>();
    private List<Particle> particles = new ArrayList<>();
    private List<Teleport> teleports = new ArrayList<>();
    private boolean canMove = false;
    private int seconds = 5;
    private int particleId = 0;
    private double radius = 1.5;

    public TeleportManager() {
        particles.add(Particle.FIREWORKS_SPARK);
        particles.add(Particle.SUSPENDED_DEPTH);
        particles.add(Particle.CRIT);
        particles.add(Particle.CRIT_MAGIC);
        particles.add(Particle.SMOKE_NORMAL);
        particles.add(Particle.SMOKE_LARGE);
        particles.add(Particle.SPELL);
        particles.add(Particle.SPELL_INSTANT);
        particles.add(Particle.SPELL_MOB);
        particles.add(Particle.SPELL_WITCH);
        particles.add(Particle.DRIP_WATER);
        particles.add(Particle.DRIP_LAVA);
        particles.add(Particle.VILLAGER_ANGRY);
        particles.add(Particle.VILLAGER_HAPPY);
        particles.add(Particle.TOWN_AURA);
        particles.add(Particle.NOTE);
        particles.add(Particle.ENCHANTMENT_TABLE);
        particles.add(Particle.FLAME);
        particles.add(Particle.CLOUD);
        particles.add(Particle.REDSTONE);
        particles.add(Particle.SNOW_SHOVEL);
        particles.add(Particle.HEART);
        particles.add(Particle.DRAGON_BREATH);
        particles.add(Particle.END_ROD);
        particles.add(Particle.DAMAGE_INDICATOR);
    }

    /**
     * Have to be launched after the IconManager (see WarpSign.class - fromJSONString method - need warps and categories)
     */
    public void load() {
        this.particleId = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getInt("WarpSystem.Teleport.Animation", 17);
        this.seconds = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getInt("WarpSystem.Teleport.Delay", 5);
        this.canMove = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Teleport.Allow_Move", false);

        //stop and clear old teleporters & load teleporters

        this.portals.forEach(Portal::destroy);
        this.portals.clear();

        this.warpSigns.clear();

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        
        WarpSystem.log("  > Loading Portals (from Teleporters).");
        for(String s : file.getConfig().getStringList("Teleporters")) {
            this.portals.add(Portal.getByJSONString(s));
        }

        WarpSystem.log("  > Loading Portals (from Portals).");
        for(String s : file.getConfig().getStringList("Portals")) {
            this.portals.add(Portal.getByJSONString(s));
        }

        //Check duplicates
        List<Portal> duplicates = new ArrayList<>();
        for(Portal p0 : this.portals) {
            if(duplicates.contains(p0)) continue;

            for(Portal p1 : this.portals) {
                if(duplicates.contains(p1) || p0.equals(p1)) continue;

                if(p0.getStart().equals(p1.getStart()) && p0.getDestination().equals(p1.getDestination())) {
                    if(!duplicates.contains(p1)) duplicates.add(p1);
                }
            }
        }

        if(!duplicates.isEmpty()) {
            WarpSystem.log("    > " + duplicates.size() + " duplicated Portal(s) - Removing...");
            this.portals.removeAll(duplicates);
            duplicates.clear();
        }

        WarpSystem.log("    > Verify that worlds are available");
        for(Portal portal : this.portals) {
            if(portal.getStart().getWorld() == null || portal.getDestination().getWorld() == null) portal.setDisabled(true);
        }

        WarpSystem.log("    > Verify that portals are enabled");
        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Functions.Portals", true)) {
            this.portals.forEach(p -> p.setRunning(true));
        }

        WarpSystem.log("  > Loading WarpSigns.");
        for(String s : file.getConfig().getStringList("WarpSigns")) {
            WarpSign warpSign = WarpSign.fromJSONString(s);

            if(warpSign != null) {
                if(warpSign.getLocation() != null && warpSign.getLocation().getWorld() != null && warpSign.getLocation().getBlock() != null) {
                    if(warpSign.getLocation().getBlock().getType().equals(XMaterial.SIGN.parseMaterial()) || warpSign.getLocation().getBlock().getType().equals(Material.WALL_SIGN)) {
                        this.warpSigns.add(warpSign);
                    } else WarpSystem.log("    > Loaded WarpSign at location without sign! (Skip)");
                } else WarpSystem.log("    > Loaded WarpSign with missing world! (Skip)");
            } else WarpSystem.log("    > Could not load WarpSign! (Skip)");
        }

        //Remove old portals
        file.getConfig().set("Teleporters", null);
        file.saveConfig();
    }

    public void save(boolean saver) {
        FileConfiguration config = WarpSystem.getInstance().getFileManager().getFile("Config").getConfig();

        config.set("WarpSystem.Teleport.Animation", this.particleId);
        config.set("WarpSystem.Teleport.Delay", this.seconds);
        config.set("WarpSystem.Teleport.Allow_Move", this.canMove);

        WarpSystem.getInstance().getFileManager().getFile("Config").saveConfig();


        //Save teleporters
        config = WarpSystem.getInstance().getFileManager().getFile("Teleporters").getConfig();

        if(!saver) WarpSystem.log("  > Saving Portals.");
        List<String> data = new ArrayList<>();

        for(Portal portal : this.portals) {
            if(portal.getStart().getWorld() == null || portal.getDestination().getWorld() == null) continue;
            data.add(portal.toJSONString());
        }

        config.set("Portals", data);

        if(!saver) WarpSystem.log("  > Saving WarpSigns.");
        data = new ArrayList<>();
        for(WarpSign s : this.warpSigns) {
            data.add(s.toJSONString());
        }

        config.set("WarpSigns", data);

        WarpSystem.getInstance().getFileManager().getFile("Teleporters").saveConfig();
    }

    public void teleport(Player player, Warp warp) {
        if(isTeleporting(player)) {
            Teleport teleport = getTeleport(player);
            long diff = System.currentTimeMillis() - teleport.getStartTime();
            if(diff > 50)
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting", new Example("ENG", "&cYou are already teleporting!"), new Example("GER", "&cDu wirst bereits teleportiert!")));
            return;
        }

        player.closeInventory();

        Teleport teleport = new Teleport(player, warp);
        this.teleports.add(teleport);

        if(seconds == 0 || (WarpSystem.OP_CAN_SKIP_DELAY && player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Delay))) teleport.teleport();
        else teleport.start();
    }

    public void teleport(Player player, String globalWarp, String globalWarpDisplayName, double costs) {
        if(isTeleporting(player)) {
            Teleport teleport = getTeleport(player);
            long diff = System.currentTimeMillis() - teleport.getStartTime();
            if(diff > 50)
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Is_Already_Teleporting", new Example("ENG", "&cYou are already teleporting!"), new Example("GER", "&cDu wirst bereits teleportiert!")));
            return;
        }

        String targetServer = WarpSystem.getInstance().getGlobalWarpManager().getGlobalWarps().get(globalWarp);

        if(WarpSystem.getInstance().getCurrentServer().equalsIgnoreCase(targetServer)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Player_Is_Already_On_Target_Server", new Example("ENG", "&cYou are already on the target server."), new Example("GER", "&cDu befindest dich bereits auf dem Ziel-Server.")));
            return;
        }

        player.closeInventory();

        Teleport teleport = new Teleport(player, globalWarp, globalWarpDisplayName, costs);
        this.teleports.add(teleport);

        if(seconds == 0 || (WarpSystem.OP_CAN_SKIP_DELAY && player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Delay))) teleport.teleport();
        else teleport.start();
    }

    public void cancelTeleport(Player p) {
        if(!isTeleporting(p)) return;

        Teleport teleport = getTeleport(p);
        teleport.cancel(true, false);
        this.teleports.remove(teleport);

        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Cancel_Message", true)) {
            MessageAPI.sendActionBar(p, Lang.get("Teleport_Cancelled", new Example("ENG", "&cThe teleport was cancelled."), new Example("GER", "&cDer Teleport wurde abgebrochen.")));
        }
    }

    public Teleport getTeleport(Player p) {
        for(Teleport teleport : teleports) {
            if(teleport.getPlayer().getName().equalsIgnoreCase(p.getName())) return teleport;
        }

        return null;
    }

    public boolean isTeleporting(Player p) {
        return getTeleport(p) != null;
    }

    public WarpSign getByLocation(Location location) {
        for(WarpSign warpSign : this.warpSigns) {
            if(warpSign.getLocation().getBlock().getLocation().equals(location.getBlock().getLocation())) return warpSign;
        }

        return null;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getParticleId() {
        return particleId;
    }

    public Particle getParticle() {
        return particles.get(particleId);
    }

    public void setParticleId(int particleId) {
        this.particleId = particleId;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public List<WarpSign> getWarpSigns() {
        return warpSigns;
    }

    public List<Teleport> getTeleports() {
        return teleports;
    }
}
