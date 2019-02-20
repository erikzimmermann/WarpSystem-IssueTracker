package de.codingair.warpsystem.bungee.base;

import de.codingair.codingapi.bungeecord.files.FileManager;
import de.codingair.codingapi.time.TimeFetcher;
import de.codingair.codingapi.time.Timer;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.bungee.base.listeners.MainListener;
import de.codingair.warpsystem.bungee.base.managers.DataManager;
import de.codingair.warpsystem.bungee.base.managers.ServerManager;
import de.codingair.warpsystem.transfer.bungee.BungeeDataHandler;
import de.codingair.warpsystem.utils.Manager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

public class WarpSystem extends Plugin {
    public static final String PERMISSION_TELEPORT_COMMAND = "WarpSystem.TeleportCommand";

    private static WarpSystem instance;
    private BungeeDataHandler dataHandler = new BungeeDataHandler(this);
    private FileManager fileManager = new FileManager(this);
    private ServerManager serverManager = new ServerManager();
    private DataManager dataManager = new DataManager();
    private Timer timer = new Timer();

    @Override
    public void onEnable() {
        instance = this;
        timer.start();

        log(" ");
        log("________________________________________________________");
        log(" ");
        log("                   WarpSystem [" + getDescription().getVersion() + "]");
        log(" ");
        log("Status:");
        log(" ");
        log("Initialize SpigotConnector");

        this.fileManager.loadFile("Config", "/", "bungee/");
        try {
            Lang.initPreDefinedLanguages(this);
        } catch(IOException e) {
            e.printStackTrace();
        }

        this.dataHandler.onEnable();
        MainListener listener = new MainListener();
        BungeeCord.getInstance().getPluginManager().registerListener(this, listener);
        this.dataHandler.register(listener);
        this.serverManager.run();

        log("Loading features");
        boolean createBackup = false;
        if(!this.dataManager.load()) createBackup = true;

        if(createBackup) {
            log("Loading with errors > Create backup...");
            createBackup();
            log("Backup successfully created");
        }

        this.startAutoSaver();

        timer.stop();

        log(" ");
        log("Done (" + timer.getLastStoppedTime() + "s)");
        log(" ");
        log("________________________________________________________");
        log(" ");
    }

    @Override
    public void onDisable() {
        this.dataHandler.onDisable();
        save(false);
        destroy();
    }

    private void startAutoSaver() {
        WarpSystem.log("Starting AutoSaver");
        BungeeCord.getInstance().getScheduler().schedule(this, () -> save(true), 20, 20, TimeUnit.MINUTES);
    }

    private void destroy() {
        this.dataManager.getManagers().forEach(Manager::destroy);
    }

    private void save(boolean saver) {
        try {
            if(!saver) {
                timer.start();

                log(" ");
                log("________________________________________________________");
                log(" ");
                log("                   WarpSystem [" + getDescription().getVersion() + "]");
                log(" ");
                log("Status:");
                log(" ");
            }

            if(!saver) log("Saving features");
            this.dataManager.save(saver);

            if(!saver) {
                timer.stop();

                log(" ");
                log("Done (" + timer.getLastStoppedTime() + "s)");
                log(" ");
                log("________________________________________________________");
                log(" ");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createBackup() {
        try {
            getDataFolder().createNewFile();
        } catch(IOException e) {
            e.printStackTrace();
        }

        File backupFolder = new File(getDataFolder().getPath() + "/Backups/", TimeFetcher.getYear() + "_" + (TimeFetcher.getMonthNum() + 1) + "_" + TimeFetcher.getDay() + " " + TimeFetcher.getHour() + "_" + TimeFetcher.getMinute() + "_" + TimeFetcher.getSecond());
        backupFolder.mkdirs();

        for(File file : getDataFolder().listFiles()) {
            if(file.getName().equals("Backups") || file.getName().equals("ErrorReport.txt")) continue;
            File dest = new File(backupFolder, file.getName());

            try {
                if(file.isDirectory()) {
                    copyFolder(file, dest);
                    continue;
                }

                copyFileUsingFileChannels(file, dest);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyFolder(File source, File dest) throws IOException {
        dest.mkdirs();
        for(File file : source.listFiles()) {
            File copy = new File(dest, file.getName());

            if(file.isDirectory()) {
                copyFolder(file, copy);
                continue;
            }

            copyFileUsingFileChannels(file, copy);
        }
    }

    private void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    public static WarpSystem getInstance() {
        return instance;
    }

    public BungeeDataHandler getDataHandler() {
        return dataHandler;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public static void log(String message) {
        System.out.println(message);
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
