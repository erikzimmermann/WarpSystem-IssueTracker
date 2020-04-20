package de.codingair.warpsystem.spigot.features.playerwarps.utils;

import de.codingair.warpsystem.transfer.serializeable.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerWarpUpdate extends PlayerWarpData implements Serializable {
    //25 variables |  24/8bits = 3 bytes
    //private byte b0; //name, trusted, type, skullId, color, data, public, teleportMessage
    //private byte b1; //teleportCosts, classes, description, started, time, creatorKey, notify, performed
    //private byte b2; //server, world, x, y, z, yaw, pitch
    private String originName;
    private UUID id;

    public PlayerWarpUpdate() {
        super();
        owner = null;
        trusted = null;
        classes = null;
        description = null;
    }

    public PlayerWarpUpdate(String originName, UUID id) {
        this();
        this.originName = originName;
        this.id = id;
    }

    private byte getFirstOption() {
        byte b = (byte) (name != null ? 1 : 0);                       //options 1/3
        b |= (trusted != null ? 1 : 0) << 1;
        b |= (type != null ? 1 : 0) << 2;
        b |= (skullId != null ? 1 : 0) << 3;
        b |= (rgb != null ? 1 : 0) << 4;
        b |= (data != null ? 1 : 0) << 5;
        b |= (isPublic != null && isPublic ? 1 : 0) << 6;
        b |= (teleportMessage != null ? 1 : 0) << 7;

        return b;
    }

    private byte getSecondOption() {
        byte b = (byte) (teleportCosts != null ? 1 : 0);                    //options 2/3
        b |= (classes != null ? 1 : 0) << 1;
        b |= (description != null ? 1 : 0) << 2;
        b |= (started != null ? 1 : 0) << 3;
        b |= (time != null ? 1 : 0) << 4;
        b |= (creatorKey != null ? 1 : 0) << 5;
        b |= (notify != null && notify ? 1 : 0) << 6;
        b |= (performed != null ? 1 : 0) << 7;

        return b;
    }

    private byte getThirdFirstOption() {
        byte b = (byte) (server != null ? 1 : 0);                           //options 3/3
        b |= (world != null ? 1 : 0) << 1;
        b |= (x != null ? 1 : 0) << 2;
        b |= (y != null ? 1 : 0) << 3;
        b |= (z != null ? 1 : 0) << 4;
        b |= (yaw != null ? 1 : 0) << 5;
        b |= (pitch != null ? 1 : 0) << 6;
        b |= (inactiveSales != null ? 1 : 0) << 7;
        return b;
    }

    public boolean isEmpty() {
        return getFirstOption() + getSecondOption() + getThirdFirstOption() == -3;
    }

    @Override
    public void write(DataOutputStream o) throws IOException {
        o.writeUTF(originName);
        o.writeLong(id.getMostSignificantBits());
        o.writeLong(id.getLeastSignificantBits());

        byte b = getFirstOption();                                  //options 1/3
        o.writeByte(b);

        if(name != null) o.writeUTF(this.name);

        if(trusted != null) {
            o.writeByte(trusted.size());
            for(User user : trusted) {
                user.write(o);
            }
        }

        if(type != null) o.writeUTF(type);
        if(skullId != null) o.writeUTF(skullId);
        if(rgb != null) o.writeInt(rgb);
        if(data != null) o.writeByte(data);

        if(teleportMessage != null) o.writeUTF(teleportMessage);

        b = getSecondOption();                                      //options 2/3
        o.writeByte(b);

        if(teleportCosts != null) o.writeDouble(teleportCosts);

        if(classes != null) {
            o.writeByte(classes.size());
            for(byte c : classes) {
                o.writeByte(c);
            }
        }

        if(description != null) {
            o.writeByte(description.size());
            for(String s : description) {
                o.writeUTF(s);
            }
        }

        if(started != null) o.writeLong(started);
        if(time != null) o.writeLong(time);
        if(creatorKey != null) o.writeUTF(creatorKey);

        if(performed != null) o.writeInt(performed);

        b = getThirdFirstOption();                                  //options 3/3
        o.writeByte(b);

        if(server != null) o.writeUTF(server);
        if(world != null) o.writeUTF(world);
        if(x != null) o.writeDouble(x);
        if(y != null) o.writeDouble(y);
        if(z != null) o.writeDouble(z);
        if(yaw != null) o.writeFloat(yaw);
        if(pitch != null) o.writeFloat(pitch);
        if(inactiveSales != null) o.writeByte(inactiveSales);
    }

    @Override
    public void read(DataInputStream i) throws IOException {
        this.originName = i.readUTF();
        this.id = new UUID(i.readLong(), i.readLong());

        byte options = i.readByte();                                        //options 1/3

        if((options & 1) != 0) name = i.readUTF();
        if((options & (1 << 1)) != 0) {
            if(this.trusted == null) this.trusted = new ArrayList<>();
            else this.trusted.clear();

            int size = i.readByte();
            for(int i1 = 0; i1 < size; i1++) {
                User user = new User();
                user.read(i);
                trusted.add(user);
            }
        }
        if((options & (1 << 2)) != 0) type = i.readUTF();
        if((options & (1 << 3)) != 0) skullId = i.readUTF();
        if((options & (1 << 4)) != 0) rgb = i.readInt();
        if((options & (1 << 5)) != 0) data = i.readByte();
        isPublic = (options & (1 << 6)) != 0;
        if((options & (1 << 7)) != 0) teleportMessage = i.readUTF();

        options = i.readByte();                                             //options 2/3

        if((options & 1) != 0) teleportCosts = i.readDouble();
        if((options & (1 << 1)) != 0) {
            if(this.classes == null) this.classes = new ArrayList<>();
            else this.classes.clear();

            int size = i.readByte();
            for(int i1 = 0; i1 < size; i1++) {
                classes.add(i.readByte());
            }
        }
        if((options & (1 << 2)) != 0) {
            if(this.description == null) this.description = new ArrayList<>();
            else this.description.clear();

            int size = i.readByte();
            for(int i1 = 0; i1 < size; i1++) {
                description.add(i.readUTF());
            }
        }
        if((options & (1 << 3)) != 0) started = i.readLong();
        if((options & (1 << 4)) != 0) time = i.readLong();
        if((options & (1 << 5)) != 0) creatorKey = i.readUTF();
        notify = (options & (1 << 6)) != 0;
        if((options & (1 << 7)) != 0) performed = i.readInt();

        options = i.readByte();                                             //options 3/3

        if((options & 1) != 0) server = i.readUTF();
        if((options & (1 << 1)) != 0) world = i.readUTF();
        if((options & (1 << 2)) != 0) x = i.readDouble();
        if((options & (1 << 3)) != 0) y = i.readDouble();
        if((options & (1 << 4)) != 0) z = i.readDouble();
        if((options & (1 << 5)) != 0) yaw = i.readFloat();
        if((options & (1 << 6)) != 0) pitch = i.readFloat();
        if((options & (1 << 7)) != 0) inactiveSales = i.readByte();
    }

    public String getOriginName() {
        return originName;
    }

    public UUID getId() {
        return id;
    }
}
