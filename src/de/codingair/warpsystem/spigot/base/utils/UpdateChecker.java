package de.codingair.warpsystem.spigot.base.utils;

import de.codingair.warpsystem.spigot.base.WarpSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
    private String version = null;
    private String download = null;
    private String updateInfo = null;

    private final static String premium = "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-13.66035/updates";
    private final static String free = "https://www.spigotmc.org/resources/warps-portals-and-more-warp-teleport-system-1-8-1-13.29595/updates";

    private boolean needsUpdate = false;

    public boolean needsUpdate() {
        this.version = null;
        this.download = null;

        try {
            URLConnection con = new URL((WarpSystem.getInstance().isPremium() ? premium : free).replace("/updates", "/history")).openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);
            con.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = input.readLine()) != null) {
                if(this.version != null && this.download != null) break;

                if(line.contains("<td class=\"version\">") && this.version == null) {
                    this.version = line.split(">")[1].split("<")[0];
                }
            }

            if(this.version == null) return false;
        } catch(Exception ex) {
            return false;
        }

        needsUpdate = !WarpSystem.getInstance().getDescription().getVersion().equals(this.version);
        if(needsUpdate) checkUpdateInfo();
        return needsUpdate && !notStable();
    }

    public boolean notStable() {
        if(this.updateInfo == null) {
            checkUpdateInfo();
            return notStable();
        } else return this.updateInfo.toLowerCase().startsWith("not stable");
    }

    public String checkUpdateInfo() {
        if(!needsUpdate) return null;
        if(updateInfo != null) return updateInfo.toLowerCase().startsWith("not stable") ? null : updateInfo;

        String url = WarpSystem.getInstance().isPremium() ? premium : free;

        try {
            URLConnection con = new URL(url).openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);
            con.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

            updateInfo = null;
            boolean atUpdates = false;
            boolean atInfo = false;

            String line;
            while((line = input.readLine()) != null) {

                if(atUpdates) {
                    if(atInfo) {
                        this.download = "https://www.spigotmc.org/" + line.substring(9, line.indexOf('>') - 1);

                        line = line.replace("</a>", "");
                        line = line.substring(line.lastIndexOf(">") + 1);
                        updateInfo = line;
                        break;
                    }

                    if(line.contains("textHeading")) atInfo = true;
                }

                if(line.contains("updateContainer")) atUpdates = true;
            }

            if(updateInfo.toLowerCase().startsWith("not stable")) return null;
            return updateInfo;
        } catch(Exception ex) {
            return null;
        }
    }

    public String getDownload() {
        return download;
    }

    public String getVersion() {
        return version;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }
}
