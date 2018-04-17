package de.codingair.warpsystem.gui.affiliations;

import de.codingair.warpsystem.WarpSystem;

import java.io.*;
import java.util.Base64;
import java.util.logging.Level;

public class ActionIconHelper {
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

            String d = new String(data);

            d = d.replace("de.CodingAir.v1_6.CodingAPI.Serializable", "de.codingair.v1_6.codingapi.serializable")
                 .replace("de/CodingAir/v1_6/CodingAPI/Serializable", "de/codingair/v1_6/codingapi/serializable");

            data = d.getBytes();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return (T) o;
        } catch(StreamCorruptedException e) {
            WarpSystem.getInstance().getLogger().log(Level.SEVERE, "Data is to old to read... Please recreate them.");
            return null;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        } catch(ClassNotFoundException e) {
            WarpSystem.getInstance().getLogger().log(Level.SEVERE, "Data is to old to read... Please recreate them.");
            return null;
        }
    }
}
