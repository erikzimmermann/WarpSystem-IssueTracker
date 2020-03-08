package de.codingair.warpsystem.bungee.features.playerwarps.utils;

import de.codingair.codingapi.tools.io.JSON.BungeeJSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.transfer.serializeable.Serializable;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerWarp implements Serializable, de.codingair.codingapi.tools.io.utils.Serializable {
    private String name;

    private User owner;
    private List<User> trusted;

    private String type, skullId;
    private byte red, green, blue, data;

    private boolean isPublic;
    private String teleportMessage;
    private double teleportCosts;
    private List<Byte> classes;
    private List<String> description;

    private long born, started, time;

    private String creatorKey;
    private boolean notify;

    private int performed;
    private String server, world;
    private double x, y, z;
    private float yaw, pitch;

    public PlayerWarp() {
        owner = new User();
        trusted = new ArrayList<>();
        classes = new ArrayList<>();
        description = new ArrayList<>();
    }

    public void apply(PlayerWarp w) {
        this.owner = w.owner;

        if(this.trusted == null) this.trusted = new ArrayList<>(w.trusted);
        else {
            this.trusted.clear();
            this.trusted.addAll(w.trusted);
        }

        if(this.description == null) this.description = new ArrayList<>(w.description);
        else {
            this.description.clear();
            this.description.addAll(w.description);
        }

        if(this.classes == null) this.classes = new ArrayList<>(w.classes);
        else {
            this.classes.clear();
            this.classes.addAll(w.classes);
        }

        this.name = w.name;
        this.teleportMessage = w.teleportMessage;

        this.type = w.type;
        this.skullId = w.skullId;
        this.red = w.red;
        this.green = w.green;
        this.blue = w.blue;
        this.data = w.data;

        this.isPublic = w.isPublic;
        this.teleportCosts = w.teleportCosts;
        this.born = w.born;
        this.started = w.started;
        this.time = w.time;
        this.creatorKey = w.creatorKey;
        this.notify = w.notify;

        this.performed = w.performed;
        this.server = w.server;
        this.world = w.world;
        this.x = w.x;
        this.y = w.y;
        this.z = w.z;
        this.yaw = w.yaw;
        this.pitch = w.pitch;
    }

    @Override
    public void write(DataOutputStream o) throws IOException {
        //byte mask: PUBLIC | ITEM_SKULL | ITEM_COLOR | ITEM_DATA | TELEPORT_MESSAGE | BORN == START | CREATOR_KEY | NOTIFY
        byte b = (byte) (isPublic ? 1 : 0);
        b |= (skullId != null ? 1 : 0) << 1;
        b |= (red != 0 || green != 0 || blue != 0 ? 1 : 0) << 2;
        b |= (data != (byte) 0 ? 1 : 0) << 3;
        b |= (teleportMessage != null ? 1 : 0) << 4;
        b |= (born == started ? 1 : 0) << 5;
        b |= (creatorKey != null ? 1 : 0) << 6;
        b |= (teleportCosts > 0 ? 1 : 0) << 7;

        o.writeByte(b);                                                 //options

        this.owner.write(o);                                            //owner
        o.writeUTF(this.name);                                          //name
        o.writeByte(description.size());                                //description
        for(String s : description) {
            o.writeUTF(s);
        }
        if(teleportMessage != null) o.writeUTF(teleportMessage);        //teleportMessage

        o.writeUTF(type);                              //item
        if(data != (byte) 0) o.writeByte(data);
        if(red != 0 || green != 0 || blue != 0) {
            o.writeByte(red);
            o.writeByte(green);
            o.writeByte(blue);
        }
        if(skullId != null) o.writeUTF(skullId);

        if(teleportCosts > 0) o.writeDouble(teleportCosts);             //teleport costs
        o.writeBoolean(notify);                                         //notify
        o.writeLong(born);                                              //born
        if(born != started) o.writeLong(started);                       //started
        o.writeLong(time);                                              //time
        if(creatorKey != null) o.writeUTF(creatorKey);                  //creator key

        o.writeByte(trusted.size());                                    //trusted members
        for(User user : trusted) {
            user.write(o);
        }

        o.writeByte(classes.size());                                    //classes
        for(byte c : classes) {
            o.writeByte(c);
        }

        o.writeInt(performed);                                     //feature object data values
        o.writeUTF(server);
        o.writeUTF(world);
        o.writeDouble(x);
        o.writeDouble(y);
        o.writeDouble(z);
        o.writeFloat(yaw);
        o.writeFloat(pitch);
    }

    @Override
    public void read(DataInputStream i) throws IOException {
        if(this.trusted == null) this.trusted = new ArrayList<>();
        else this.trusted.clear();

        if(this.classes == null) this.classes = new ArrayList<>();
        else this.classes.clear();

        if(this.description == null) this.description = new ArrayList<>();
        else this.description.clear();

        byte options = i.readByte();                                    //options
        this.isPublic = (options & 1) != 0;
        boolean skull = (options & (1 << 1)) != 0;
        boolean color = (options & (1 << 2)) != 0;
        boolean data = (options & (1 << 3)) != 0;
        boolean teleportMessage = (options & (1 << 4)) != 0;
        boolean differentStart = (options & (1 << 5)) != 0;
        boolean hasKey = (options & (1 << 6)) != 0;
        boolean teleportCosts = (options & (1 << 7)) != 0;

        this.owner.read(i);                                             //owner
        this.name = i.readUTF();                                        //name MAX = 16

        int size = i.readByte();                                        //description
        for(int i1 = 0; i1 < size; i1++) {
            description.add(i.readUTF());
        }

        if(teleportMessage) {
            this.teleportMessage = i.readUTF();         //teleport message
        }

        this.type = i.readUTF();                                        //item
        if(data) this.data = i.readByte();
        if(color) {
            this.red = i.readByte();
            this.green = i.readByte();
            this.blue = i.readByte();
        }
        if(skull) this.skullId = i.readUTF();

        if(teleportCosts) this.teleportCosts = i.readDouble();          //teleport costs
        this.notify = i.readBoolean();                                  //notify
        this.born = i.readLong();                                       //born
        if(differentStart) {
            this.started = i.readLong();                 //start
        }
        this.time = i.readLong();                                       //time
        if(hasKey) this.creatorKey = i.readUTF();                       //creator key

        size = i.readByte();                                            //trusted members
        for(int i1 = 0; i1 < size; i1++) {
            User user = new User();
            user.read(i);
            trusted.add(user);
        }

        size = i.readByte();                                            //classes
        for(int i1 = 0; i1 < size; i1++) {
            classes.add(i.readByte());
        }

        performed = i.readInt();                                        //feature object data values
        server = i.readUTF();
        world = i.readUTF();
        x = i.readDouble();
        y = i.readDouble();
        z = i.readDouble();
        yaw = i.readFloat();
        pitch = i.readFloat();
    }

    @Override
    public void write(DataWriter d) {
        JSONArray trustedMembers = new JSONArray();
        for(PlayerWarp.User user : this.trusted) {
            BungeeJSON userJson = new BungeeJSON();
            user.write(userJson);
            trustedMembers.add(userJson);
        }

        JSONArray classes = new JSONArray();
        classes.addAll(this.classes);

        this.owner.write(d);
        d.put("name", name);
        d.put("description", description);
        d.put("tpmsg", teleportMessage);

        d.put("type", type);
        d.put("data", data);
        d.put("skull", skullId);
        d.put("red", red);
        d.put("green", green);
        d.put("blue", blue);

        d.put("public", isPublic);
        d.put("tpcosts", teleportCosts);
        d.put("born", born);
        d.put("started", started == born ? 0 : started);
        d.put("time", time);
        d.put("key", creatorKey);
        d.put("notify", notify);
        d.put("trusted", trustedMembers);
        d.put("classes", classes);

        d.put("performed", performed);
        d.put("server", server);
        d.put("world", world);
        d.put("x", x);
        d.put("y", y);
        d.put("z", z);
        d.put("yaw", yaw);
        d.put("pitch", pitch);
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        if(this.trusted == null) this.trusted = new ArrayList<>();
        else this.trusted.clear();

        if(this.classes == null) this.classes = new ArrayList<>();
        else this.classes.clear();

        this.owner.read(d);
        this.name = d.get("name");
        this.description = d.getList("description");
        this.teleportMessage = d.get("tpmsg");

        this.type = d.get("type");
        this.data = d.getInteger("data").byteValue();
        this.skullId = d.get("skull");
        this.red = d.getInteger("red").byteValue();
        this.green = d.getInteger("green").byteValue();
        this.blue = d.getInteger("blue").byteValue();

        this.isPublic = d.getBoolean("public");
        this.teleportCosts = d.getLong("tpcosts");
        this.born = d.getLong("born");
        this.started = d.getLong("started");
        this.time = d.getLong("time");
        this.creatorKey = d.get("key");
        this.notify = d.getBoolean("notify");

        if(born > 0 && started == 0) started = born;

        JSONArray array = d.getList("trusted");
        if(array != null)
            for(Object o : array) {
                BungeeJSON data = new BungeeJSON((Map<?, ?>) o);
                PlayerWarp.User user = new PlayerWarp.User();
                user.read(data);
                trusted.add(user);
            }

        array = d.getList("classes");
        if(array != null)
            for(Object o : array) {
                this.classes.add((byte) o);
            }

        performed = d.getInteger("performed");
        server = d.get("server", null, true);
        world = d.get("world", null, true);
        x = d.getDouble("x");
        y = d.getDouble("y");
        z = d.getDouble("z");
        yaw = d.getFloat("yaw");
        pitch = d.getFloat("pitch");
        return true;
    }

    @Override
    public void destroy() {

    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public List<User> getTrusted() {
        return trusted;
    }

    public String getType() {
        return type;
    }

    public String getSkullId() {
        return skullId;
    }

    public byte getRed() {
        return red;
    }

    public byte getGreen() {
        return green;
    }

    public byte getBlue() {
        return blue;
    }

    public byte getData() {
        return data;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getTeleportMessage() {
        return teleportMessage;
    }

    public double getTeleportCosts() {
        return teleportCosts;
    }

    public List<Byte> getClasses() {
        return classes;
    }

    public List<String> getDescription() {
        return description;
    }

    public long getBorn() {
        return born;
    }

    public long getStarted() {
        return started;
    }

    public long getTime() {
        return time;
    }

    public String getCreatorKey() {
        return creatorKey;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setStarted(long time) {
        this.started = time;
    }

    public boolean born() {
        if(this.born > 0) return false;
        this.born = System.currentTimeMillis();
        return true;
    }

    public static PlayerWarp readInitially(DataInputStream i) throws IOException {
        PlayerWarp w = new PlayerWarp();
        w.read(i);
        return w;
    }

    public static class User implements de.codingair.codingapi.tools.io.utils.Serializable, de.codingair.warpsystem.transfer.serializeable.Serializable {
        private String jsonPrefix;
        private String name;
        private UUID id;

        public User() {
        }

        public User(ProxiedPlayer player) {
            this.name = player.getName();
            this.id = player.getUniqueId();
        }

        protected User(String jsonPrefix) {
            this.jsonPrefix = jsonPrefix;
        }

        protected User(String jsonPrefix, String name, UUID id) {
            this.jsonPrefix = jsonPrefix;
            this.name = name;
            this.id = id;
        }

        @Override
        public void write(DataOutputStream o) throws IOException {
            o.writeUTF(this.name);
            o.writeLong(this.id.getMostSignificantBits());
            o.writeLong(this.id.getLeastSignificantBits());
        }

        @Override
        public void read(DataInputStream i) throws IOException {
            this.name = i.readUTF();
            this.id = new UUID(i.readLong(), i.readLong());
        }

        @Override
        public boolean read(DataWriter d) {
            this.name = d.get(p() + "name");
            this.id = UUID.fromString(d.get(p() + "id"));

            return true;
        }

        @Override
        public void write(DataWriter d) {
            d.put(p() + "name", name);
            d.put(p() + "id", id.toString());
        }

        private String p() {
            return jsonPrefix == null ? "" : jsonPrefix + ".";
        }

        @Override
        public void destroy() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public ProxiedPlayer getPlayer() {
            ProxiedPlayer p = BungeeCord.getInstance().getPlayer(id);
            if(p != null && !p.getName().equals(name)) name = p.getName();
            return p;
        }
    }
}
