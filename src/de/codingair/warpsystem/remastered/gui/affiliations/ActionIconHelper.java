package de.codingair.warpsystem.remastered.gui.affiliations;

import java.io.*;
import java.util.Base64;

public class ActionIconHelper {
    public static String toString(ActionIcon icon) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(icon);
            oos.close();

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ActionIcon fromString(String s) {
        try {
            byte[] data = Base64.getDecoder().decode(s);

            System.out.println(new String(data));

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return (ActionIcon) o;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
