package com.dt.lBT.utils;

import com.dt.lBT.Main;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private Main plugin;
    private int resourceid;

    public UpdateChecker(Main plugin, int resourceid){
        this.plugin = plugin;
        this.resourceid = resourceid;
    }
    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceid).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    String latestVersion = scanner.next();
                    consumer.accept(latestVersion);
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Update checker is broken, can't find an update!" + exception.getMessage());
            }
        });
    }

}
