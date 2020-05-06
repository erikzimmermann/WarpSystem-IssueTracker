package de.codingair.warpsystem.spigot.features.spawn.utils;

import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.spigot.api.PAPI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.transfer.packets.general.TeleportSpawnPacket;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spawn extends FeatureObject {
    private Usage usage;
    private RespawnUsage respawnUsage;
    //first join actions
    private List<String> broadCastMessages;
    private boolean randomFireWorks;

    private String displayName;

    public Spawn() {
        usage = Usage.LOCAL;
        broadCastMessages = new ArrayList<>();
        randomFireWorks = false;
        respawnUsage = RespawnUsage.DISABLED;
        displayName = "Spawn";
    }

    public boolean isValid() {
        return hasAction(Action.WARP) || switchServer();
    }

    public Location getLocation() {
        if(!hasAction(Action.WARP)) return null;

        return getAction(WarpAction.class).getValue().buildLocation();
    }

    private boolean switchServer() {
        return SpawnManager.getInstance().getSpawnServer() != null && !SpawnManager.getInstance().getSpawnServer().equals(WarpSystem.getInstance().getCurrentServer());
    }

    public Spawn clone() {
        Spawn clone = new Spawn();
        clone.apply(this);
        return clone;
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        if(object instanceof Spawn) {
            Spawn other = (Spawn) object;
            this.usage = other.usage;
            this.broadCastMessages.clear();
            this.broadCastMessages.addAll(other.broadCastMessages);
            this.randomFireWorks = other.randomFireWorks;
            this.respawnUsage = other.respawnUsage;
            this.displayName = other.displayName;
        }
    }

    @Override
    public void prepareTeleportOptions(String player, TeleportOptions options) {
        super.prepareTeleportOptions(player, options);
        options.setDisplayName(displayName);
    }

    public void firstJoin(PlayerJoinEvent e) {
        TeleportOptions options = new TeleportOptions();
        options.setMessage(null);
        options.setAfterEffects(false);
        options.setSkip(true);

        super.perform(e.getPlayer(), options);
        spawnFireWorks();
        broadcast(e.getPlayer());
    }

    private void spawnFireWorks() {
        if(randomFireWorks) {
            Location l = getLocation();
            if(l != null) {
                Random r = new Random();
                FireworkEffect.Type type = FireworkEffect.Type.BALL;

                int r1i = r.nextInt(17) + 1;
                int r2i = r.nextInt(17) + 1;
                Color c1 = Environment.getColor(r1i);
                Color c2 = Environment.getColor(r2i);

                FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

                Firework fw;

                try {
                    fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
                } catch(Exception ex) {
                    return;
                }

                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.setPower(1);

                fwm.addEffect(effect);

                fw.setFireworkMeta(fwm);
            }
        }
    }

    private void broadcast(Player player) {
        if(this.broadCastMessages != null && !this.broadCastMessages.isEmpty()) {
            for(String s : this.broadCastMessages) {
                Bukkit.broadcastMessage(prepareBroadcastMessage(s, player));
            }
        }
    }

    public static String prepareBroadcastMessage(String s, Player player) {
        return PAPI.convert(ChatColor.translateAlternateColorCodes('&', s), player).replace("\\n", "\n").replace("%player%", player.getName());
    }

    @Override
    public FeatureObject perform(Player player, TeleportOptions options) {
        if(switchServer()) {
            //switch
            WarpSystem.getInstance().getDataHandler().send(new TeleportSpawnPacket(player.getName(), false));
            return this;
        }

        return super.perform(player, options);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        boolean success = super.read(d);

        this.usage = Usage.values()[d.getInteger("usage", 0)];
        this.respawnUsage = RespawnUsage.values()[d.getInteger("respawn", 0)];
        this.randomFireWorks = d.getBoolean("fireworks", false);
        this.broadCastMessages = d.getList("broadcast");
        this.displayName = d.getString("displayname", "Spawn");

        return success;
    }

    @Override
    public void write(DataWriter d) {
        super.write(d);

        d.put("usage", this.usage.id);
        d.put("respawn", this.respawnUsage.id);
        d.put("fireworks", this.randomFireWorks);
        d.put("broadcast", this.broadCastMessages);
        d.put("displayname", this.displayName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public List<String> getBroadCastMessages() {
        return broadCastMessages;
    }

    public void setBroadCastMessages(List<String> broadCastMessages) {
        if(broadCastMessages == null) {
            this.broadCastMessages.clear();
            return;
        }

        this.broadCastMessages = broadCastMessages;
    }

    public boolean isRandomFireWorks() {
        return randomFireWorks;
    }

    public void setRandomFireWorks(boolean randomFireWorks) {
        this.randomFireWorks = randomFireWorks;
    }

    public RespawnUsage getRespawnUsage() {
        return respawnUsage;
    }

    public void setRespawnUsage(RespawnUsage respawnUsage) {
        this.respawnUsage = respawnUsage;
    }

    public enum Usage {
        LOCAL("§b" + Lang.get("Local") + " §7(/spawn)", 0),
        LOCAL_FIRST_JOIN("§b" + Lang.get("Local") + " §7(/spawn) §8+ " + "§7" + Lang.get("First_Join"), 1),
        LOCAL_EVERY_JOIN("§b" + Lang.get("Local") + " §7(/spawn) §8+ " + "§7" + Lang.get("Every_Join"), 2),
        GLOBAL("§e" + Lang.get("Global") + " §7(/spawn)", 3, true),
        GLOBAL_FIRST_JOIN("§e" + Lang.get("Global") + " §7(/spawn) §8+ " + "§7" + Lang.get("First_Join"), 4, true),
        GLOBAL_EVERY_JOIN("§e" + Lang.get("Global") + " §7(/spawn) §8+ " + "§7" + Lang.get("Every_Join"), 5, true),
        EVERY_JOIN("§7" + Lang.get("Every_Join"), 6),
        FIRST_JOIN("§7" + Lang.get("First_Join"), 7),
        DISABLED("§c" + Lang.get("Disabled"), 8);

        private String name;
        private int id;
        private boolean bungee;

        Usage(String name, int id, boolean bungee) {
            this.name = name;
            this.id = id;
            this.bungee = bungee;
        }

        Usage(String name, int id) {
            this(name, id, false);
        }

        public Usage next() {
            int next = id + 1;
            if(next == values().length) next = 0;
            return values()[next];
        }

        public Usage previous() {
            int previous = id - 1;
            if(previous < 0) previous = values().length - 1;
            return values()[previous];
        }

        public Usage getLocal() {
            return valueOf(name().replace("GLOBAL", "LOCAL"));
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public boolean isBungee() {
            return bungee;
        }
    }

    public enum RespawnUsage {
        DISABLED("§c" + Lang.get("Disabled"), 0),
        LOCAL("§b" + Lang.get("Local"), 1),
        GLOBAL("§e" + Lang.get("Global"), 2, true);

        private String name;
        private int id;
        private boolean bungee;

        RespawnUsage(String name, int id, boolean bungee) {
            this.name = name;
            this.id = id;
            this.bungee = bungee;
        }

        RespawnUsage(String name, int id) {
            this(name, id, false);
        }

        public RespawnUsage next() {
            int next = id + 1;
            if(next == values().length) next = 0;
            return values()[next];
        }

        public RespawnUsage getLocal() {
            return valueOf(name().replace("GLOBAL", "LOCAL"));
        }

        public RespawnUsage previous() {
            int previous = id - 1;
            if(previous < 0) previous = values().length - 1;
            return values()[previous];
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public boolean isBungee() {
            return bungee;
        }
    }
}
