package de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils;

import de.codingair.codingapi.serializable.SerializableLocation;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.transfer.serializeable.icons.SActionObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.util.Base64;

public class ActionIconHelper {
    public static boolean load = false;

    public static String toString(Serializable serializable) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(serializable);
            oos.close();

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromString(String s) {
        try {
            byte[] data = Base64.getDecoder().decode(s);

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();

            return (T) o;
        } catch(EOFException | StreamCorruptedException | ClassNotFoundException e) {
            if(load) {
                System.out.println("      > Couldn't handle some Icon-Data.");
                load = false;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ActionObject translate(SActionObject s) {
        IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);

        ActionObject action = new ActionObject();

        action.action = Action.getById(s.getAction());

        switch(action.action) {
            case SWITCH_SERVER:
                action.value = s.getServer();
                break;
            case RUN_COMMAND:
                action.value = s.getCommand();
                break;
            case OPEN_CATEGORY:
                action.value = manager.getCategory(s.getCategory());
                break;
            case TELEPORT_TO_WARP:
                action.value = new SerializableLocation(new Location(Bukkit.getWorld(s.getWorld()), s.getX(), s.getY(), s.getZ(), s.getYaw(), s.getPitch()));
                break;
        }

        return action;
    }
    
    public static SActionObject translate(ActionObject actionObject) {
        SActionObject s = new SActionObject();

        s.setAction(actionObject.action.getId());

        if(actionObject.value != null) {
            switch(actionObject.action) {
                case RUN_COMMAND:
                    s.setCommand((String) actionObject.value);
                    break;
                case OPEN_CATEGORY:
                    s.setCategory(((Category) actionObject.value).getName());
                    break;
                case TELEPORT_TO_WARP:
                    SerializableLocation loc = (SerializableLocation) actionObject.value;
                    Location l = loc.getLocation();

                    s.setWorld(l.getWorld().getName());
                    s.setX(l.getX());
                    s.setY(l.getY());
                    s.setZ(l.getZ());
                    s.setYaw(l.getYaw());
                    s.setPitch(l.getPitch());
                    break;
                case SWITCH_SERVER:
                    s.setServer((String) actionObject.value);
                    break;
            }
        }

        return s;
    }
}
