package de.codingair.warpsystem.spigot.base.utils;

import de.codingair.warpsystem.spigot.base.WarpSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
    private String link;
    private URL url;
    private String version = null;
    private String download = null;
    private String updateInfo = null;

    private boolean needsUpdate = false;

    public UpdateChecker(String url) {
        this.link = url;

        try {
            this.url = new URL(url);
        } catch(MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    public static int getLatestVersionID() {
        try {
            URL url = new URL("https://www.spigotmc.org/resources/warps-portals-and-warpsigns-warp-system-only-gui.29595/updates");

            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);
            con.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

            int status = 0;

            String line;
            while((line = input.readLine()) != null) {
                switch(status) {
                    case 0:
                        if(line.equals("<div class=\"updateContainer\">")) status = 1;
                        break;
                    case 1:
                        if(line.startsWith("<li class=\"primaryContent messageSimple resourceUpdate\" id=\"")) {
                            String part = line.replace("<li class=\"primaryContent messageSimple resourceUpdate\" id=\"", "").split("\"")[0].split("-")[1];
                            return Integer.parseInt(part);
                        }
                        break;
                }
            }

            return -1;
        } catch(IOException e) {
            return -1;
        }
    }

    public boolean needsUpdate() {
        if(this.url == null) return false;

        this.version = null;
        this.download = null;

        try {
            URLConnection con = this.url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);
            con.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = input.readLine()) != null) {

                if(this.version != null && this.download != null) break;

                if(line.contains("<td class=\"version\">") && this.version == null) {
                    this.version = line.split(">")[1].split("<")[0];
                } else if(line.contains("<td class=\"dataOptions download\">") && download == null) {
                    this.download = "https://www.spigotmc.org/" + line.split("href=\"")[1].split("\"")[0];
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

        String url = this.link;
        url = url.replace("/history", "/updates");

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

    public URL getUrl() {
        return url;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }
}
