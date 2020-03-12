package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.codingapi.tools.io.JSON.BungeeJSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.warpsystem.transfer.serializeable.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class PlayerWarpData implements Serializable, de.codingair.codingapi.tools.io.utils.Serializable {
    protected String name;

    protected User owner;
    protected List<User> trusted;

    protected String type, skullId;
    protected Byte red, green, blue, data;

    protected Boolean isPublic;
    protected String teleportMessage;
    protected Double teleportCosts;
    protected List<Byte> classes;
    protected List<String> description;

    protected Long born, started, time;

    protected String creatorKey;
    protected Boolean notify;

    protected Integer performed;
    protected String server, world;
    protected Double x, y, z;
    protected Float yaw, pitch;

    public PlayerWarpData() {
        owner = new User();
        trusted = new ArrayList<>();
        classes = new ArrayList<>();
        description = new ArrayList<>();
    }

    public void apply(PlayerWarpData w) {
        if(w.trusted != null) {
            if(this.trusted == null) this.trusted = new ArrayList<>(w.trusted);
            else {
                this.trusted.clear();
                this.trusted.addAll(w.trusted);
            }
        }

        if(w.description != null) {
            if(this.description == null) this.description = new ArrayList<>(w.description);
            else {
                this.description.clear();
                this.description.addAll(w.description);
            }
        }

        if(w.classes != null) {
            if(this.classes == null) this.classes = new ArrayList<>(w.classes);
            else {
                this.classes.clear();
                this.classes.addAll(w.classes);
            }
        }

        if(w.owner != null) this.owner = w.owner;
        if(w.name != null) this.name = w.name;
        if(w.teleportMessage != null) this.teleportMessage = w.teleportMessage;

        if(w.type != null) this.type = w.type;
        if(w.skullId != null) this.skullId = w.skullId;
        if(w.red != null) this.red = w.red;
        if(w.green != null) this.green = w.green;
        if(w.blue != null) this.blue = w.blue;
        if(w.data != null) this.data = w.data;

        if(w.isPublic != null) this.isPublic = w.isPublic;
        if(w.teleportCosts != null) this.teleportCosts = w.teleportCosts;
        if(w.born != null) this.born = w.born;
        if(w.started != null) this.started = w.started;
        if(w.time != null) this.time = w.time;
        if(w.creatorKey != null) this.creatorKey = w.creatorKey;
        if(w.notify != null) this.notify = w.notify;

        if(w.performed != null) this.performed = w.performed;
        if(w.server != null) this.server = w.server;
        if(w.world != null) this.world = w.world;
        if(w.x != null) this.x = w.x;
        if(w.y != null) this.y = w.y;
        if(w.z != null) this.z = w.z;
        if(w.yaw != null) this.yaw = w.yaw;
        if(w.pitch != null) this.pitch = w.pitch;
    }

    public PlayerWarpUpdate diff(PlayerWarpData oldData) {
        PlayerWarpUpdate u = new PlayerWarpUpdate(oldData.getName(), oldData.getOwner().getId());
        u.apply(this);

        if(Objects.equals(owner, oldData.owner)) u.owner = null;
        if(Objects.equals(name, oldData.name)) u.name = null;
        if(Objects.equals(teleportMessage, oldData.teleportMessage)) u.teleportMessage = null;
        if(Objects.equals(type, oldData.type)) u.type = null;
        if(Objects.equals(skullId, oldData.skullId)) u.skullId = null;
        if(Objects.equals(red, oldData.red) && Objects.equals(green, oldData.green) && Objects.equals(blue, oldData.blue)) {
            u.red = null;
            u.green = null;
            u.blue = null;
        }
        if(Objects.equals(data, oldData.data)) u.data = null;
        if(Objects.equals(teleportCosts, oldData.teleportCosts)) u.teleportCosts = null;
        if(Objects.equals(born, oldData.born)) u.born = null;
        if(Objects.equals(started, oldData.started)) u.started = null;
        if(Objects.equals(time, oldData.time)) u.time = null;
        if(Objects.equals(creatorKey, oldData.creatorKey)) u.creatorKey = null;
        if(Objects.equals(performed, oldData.performed)) u.performed = null;
        if(Objects.equals(server, oldData.server)) u.server = null;
        if(Objects.equals(world, oldData.world)) u.world = null;
        if(Objects.equals(x, oldData.x)) u.x = null;
        if(Objects.equals(y, oldData.y)) u.y = null;
        if(Objects.equals(z, oldData.z)) u.z = null;
        if(Objects.equals(yaw, oldData.yaw)) u.yaw = null;
        if(Objects.equals(pitch, oldData.pitch)) u.pitch = null;
        if(Objects.equals(classes, oldData.classes)) u.classes = null;
        if(Objects.equals(trusted, oldData.trusted)) u.trusted = null;
        if(Objects.equals(description, oldData.description)) u.description = null;

        return u;
    }

    @Override
    public void write(DataOutputStream o) throws IOException {
        //byte mask: PUBLIC | ITEM_SKULL | ITEM_COLOR | ITEM_DATA | TELEPORT_MESSAGE | BORN == START | CREATOR_KEY | NOTIFY
        byte b = (byte) (isPublic ? 1 : 0);
        b |= (skullId != null ? 1 : 0) << 1;
        b |= (red != null && green != null && blue != null ? 1 : 0) << 2;
        b |= (data != null ? 1 : 0) << 3;
        b |= (teleportMessage != null ? 1 : 0) << 4;
        b |= (!born.equals(started) ? 1 : 0) << 5;
        b |= (creatorKey != null ? 1 : 0) << 6;
        b |= (teleportCosts != null ? 1 : 0) << 7;

        o.writeByte(b);                                                 //options

        this.owner.write(o);                                            //owner
        o.writeUTF(this.name);                                          //name
        o.writeByte(description.size());                                //description
        for(String s : description) {
            o.writeUTF(s);
        }
        if(teleportMessage != null) o.writeUTF(teleportMessage);        //teleportMessage

        o.writeUTF(type);                              //item
        if(data != null) o.writeByte(data);
        if(red != null && green != null && blue != null) {
            o.writeByte(red);
            o.writeByte(green);
            o.writeByte(blue);
        }
        if(skullId != null) o.writeUTF(skullId);

        if(teleportCosts != null) o.writeDouble(teleportCosts);             //teleport costs
        o.writeBoolean(notify);                                         //notify
        o.writeLong(born);                                              //born
        if(!born.equals(started)) o.writeLong(started);                       //started
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

        if(teleportMessage) this.teleportMessage = i.readUTF();         //teleport message

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
        if(differentStart) this.started = i.readLong();                 //start
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
        for(PlayerWarpData.User user : this.trusted) {
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
        this.name = d.getString("name");
        this.description = d.getList("description");
        this.teleportMessage = d.getString("tpmsg");

        this.type = d.getString("type");
        this.data = d.getInteger("data").byteValue();
        this.skullId = d.getString("skull");
        this.red = d.getInteger("red").byteValue();
        this.green = d.getInteger("green").byteValue();
        this.blue = d.getInteger("blue").byteValue();

        this.isPublic = d.getBoolean("public");
        this.teleportCosts = d.getDouble("tpcosts");
        this.born = d.getLong("born");
        this.started = d.getLong("started");
        this.time = d.getLong("time");
        this.creatorKey = d.getString("key");
        this.notify = d.getBoolean("notify");

        if(born > 0 && started == 0) started = born;

        JSONArray array = d.getList("trusted");
        if(array != null)
            for(Object o : array) {
                BungeeJSON data = new BungeeJSON((Map<?, ?>) o);
                PlayerWarpData.User user = new PlayerWarpData.User();
                user.read(data);
                trusted.add(user);
            }

        array = d.getList("classes");
        if(array != null)
            for(Object o : array) {
                Number n = (Number) o;
                this.classes.add(n.byteValue());
            }

        performed = d.getInteger("performed");
        server = d.getString("server");
        world = d.getString("world");
        x = d.getDouble("x");
        y = d.getDouble("y");
        z = d.getDouble("z");
        yaw = d.getFloat("yaw");
        pitch = d.getFloat("pitch");
        return true;
    }

    @Override
    public void destroy() {
        if(this.classes != null) this.classes.clear();
        if(this.trusted != null) this.trusted.clear();
        if(this.description != null) this.description.clear();

        name = null;
        owner = null;
        trusted = null;
        type = null;
        skullId = null;
        red = null;
        green = null;
        blue = null;
        data = null;
        isPublic = null;
        teleportMessage = null;
        teleportCosts = null;
        classes = null;
        description = null;
        born = null;
        started = null;
        time = null;
        creatorKey = null;
        notify = null;
        performed = null;
        server = null;
        world = null;
        x = null;
        y = null;
        z = null;
        yaw = null;
        pitch = null;
    }

    public long getExpireDate() {
        if(this.started == 0) return 0;
        return this.started + this.time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getTrusted() {
        return trusted;
    }

    public void setTrusted(List<User> trusted) {
        this.trusted = trusted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSkullId() {
        return skullId;
    }

    public void setSkullId(String skullId) {
        this.skullId = skullId;
    }

    public Byte getRed() {
        return red;
    }

    public void setRed(Byte red) {
        this.red = red;
    }

    public Byte getGreen() {
        return green;
    }

    public void setGreen(Byte green) {
        this.green = green;
    }

    public Byte getBlue() {
        return blue;
    }

    public void setBlue(Byte blue) {
        this.blue = blue;
    }

    public Byte getData() {
        return data;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getTeleportMessage() {
        return teleportMessage;
    }

    public void setTeleportMessage(String teleportMessage) {
        this.teleportMessage = teleportMessage;
    }

    public Double getTeleportCosts() {
        return teleportCosts;
    }

    public void setTeleportCosts(Double teleportCosts) {
        this.teleportCosts = teleportCosts;
    }

    public List<Byte> getClasses() {
        return classes;
    }

    public void setClasses(List<Byte> classes) {
        this.classes = classes;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Long getBorn() {
        return born;
    }

    public void setBorn(Long born) {
        this.born = born;
    }

    public Long getStarted() {
        return started;
    }

    public void setStarted(Long started) {
        this.started = started;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getCreatorKey() {
        return creatorKey;
    }

    public void setCreatorKey(String creatorKey) {
        this.creatorKey = creatorKey;
    }

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }

    public Integer getPerformed() {
        return performed;
    }

    public void setPerformed(Integer performed) {
        this.performed = performed;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Float getYaw() {
        return yaw;
    }

    public void setYaw(Float yaw) {
        this.yaw = yaw;
    }

    public Float getPitch() {
        return pitch;
    }

    public void setPitch(Float pitch) {
        this.pitch = pitch;
    }

    public boolean born() {
        if(this.born > 0) return false;
        this.born = System.currentTimeMillis();
        return true;
    }

    public static class User implements de.codingair.codingapi.tools.io.utils.Serializable, de.codingair.warpsystem.transfer.serializeable.Serializable {
        private String jsonPrefix;
        private String name;
        private UUID id;

        public User() {
        }

        public User(String jsonPrefix) {
            this.jsonPrefix = jsonPrefix;
        }

        public User(String jsonPrefix, String name, UUID id) {
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

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(jsonPrefix, user.jsonPrefix) &&
                    Objects.equals(name, user.name) &&
                    Objects.equals(id, user.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(jsonPrefix, name, id);
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
    }
}
