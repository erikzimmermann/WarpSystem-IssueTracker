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

    private final static String premium = "https://github.com/CodingAir/WarpSystem-IssueTracker/releases/latest";
    private final static String free = "https://www.spigotmc.org/resources/warps-portals-and-more-warp-teleport-system-1-8-1-13.29595/updates";

    private boolean needsUpdate = false;
    private UpdateCheckerAdapter adapter;

    public UpdateChecker() {
        this.adapter = WarpSystem.getInstance().isPremium() ? new PremiumUpdateChecker() : new FreeUpdateChecker();
    }

    public boolean needsUpdate() {
        return adapter.needsUpdate();
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

    private interface UpdateCheckerAdapter {
        boolean needsUpdate();
    }

    private class PremiumUpdateChecker implements UpdateCheckerAdapter {
        @Override
        public boolean needsUpdate() {
            version = null;
            download = null;

            try {
                URLConnection con = new URL(premium).openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setConnectTimeout(5000);
                con.connect();

                BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while((line = input.readLine()) != null) {
                    if(version != null && download != null) break;

                    if(line.contains("<a href=\"/CodingAir/WarpSystem-IssueTracker/tree/") && version == null) {
                        version = line.split("/tree/")[1].split("\"")[0];
                    }
                }

                if(version == null) return false;
            } catch(Exception ex) {
                return false;
            }

            String current = WarpSystem.getInstance().getDescription().getVersion();
            if(current.startsWith("v")) current = current.replaceFirst("v", "");
            String newV = version.startsWith("v") ? version.replaceFirst("v", "") : version;

            needsUpdate = !current.equals(newV);
            if(needsUpdate) checkUpdateInfo();
            return needsUpdate && !notStable();
        }

        public boolean notStable() {
            if(updateInfo == null) {
                checkUpdateInfo();
                return notStable();
            } else return updateInfo.toLowerCase().startsWith("not stable");
        }

        public String checkUpdateInfo() {
            if(!needsUpdate) return null;
            if(updateInfo != null) return updateInfo.toLowerCase().startsWith("not stable") ? null : updateInfo;

            try {
                URLConnection con = new URL(premium).openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setConnectTimeout(5000);
                con.connect();

                BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

                updateInfo = null;
                download = "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-13.66035/update?update=";

                String line;
                while((line = input.readLine()) != null) {
                    if(line.contains("<a href=\"/CodingAir/WarpSystem-IssueTracker/releases/tag/" + version + "\">")) {
                        updateInfo = line.split(">")[1].split("<")[0];
                    }

                    if(line.contains("Download id: ")) {
                        download += line.split(": ")[1].split("<")[0];
                        break;
                    }
                }

                if(updateInfo.toLowerCase().startsWith("not stable")) return null;
                return updateInfo;
            } catch(Exception ex) {
                return null;
            }
        }
    }

    private class FreeUpdateChecker implements UpdateCheckerAdapter {

        @Override
        public boolean needsUpdate() {
            version = null;
            download = null;

            try {
                URLConnection con = new URL(free.replace("/updates", "/history")).openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setConnectTimeout(5000);
                con.connect();

                BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while((line = input.readLine()) != null) {
                    if(version != null && download != null) break;

                    if(line.contains("<td class=\"version\">") && version == null) {
                        version = line.split(">")[1].split("<")[0];
                    }
                }

                if(version == null) return false;
            } catch(Exception ex) {
                return false;
            }

            needsUpdate = !WarpSystem.getInstance().getDescription().getVersion().equals(version);
            if(needsUpdate) checkUpdateInfo();
            return needsUpdate && !notStable();
        }

        public boolean notStable() {
            if(updateInfo == null) {
                checkUpdateInfo();
                return notStable();
            } else return updateInfo.toLowerCase().startsWith("not stable");
        }

        public String checkUpdateInfo() {
            if(!needsUpdate) return null;
            if(updateInfo != null) return updateInfo.toLowerCase().startsWith("not stable") ? null : updateInfo;

            try {
                URLConnection con = new URL(free).openConnection();
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
                            download = "https://www.spigotmc.org/" + line.substring(9, line.indexOf('>') - 1);

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
    }
}
