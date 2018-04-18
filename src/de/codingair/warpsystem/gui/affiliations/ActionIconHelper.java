package de.codingair.warpsystem.gui.affiliations;

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
                System.out.println("    Couldn't handle some Icon-Data.");
                load = false;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
