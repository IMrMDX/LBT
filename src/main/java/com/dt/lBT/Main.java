package com.dt.lBT;

import com.dt.lBT.api.CommandManagerimpl;
import com.dt.lBT.config.SettingsConfig;
import com.dt.lBT.events.LBTEvent;
import com.dt.lBT.manager.*;
import com.dt.lBT.uis.AllowedColorsGUI;
import com.dt.lBT.uis.LBTGUI;
import com.dt.lBT.uis.ResetInterval;
import com.dt.lBT.utils.Logger;
import com.dt.lBT.utils.TextHandler;
import com.dt.lBT.utils.UpdateChecker;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.api.exceptions.LuckyBlockNotLoadedException;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class Main extends JavaPlugin {

    @Getter private static Main instance;
    @Getter private SettingsConfig settingsConfig;
    @Getter private PlayerDataManager playerDataManager;
    @Getter private LuckyBlockTimerLimit luckyBlockTimerLimit;
    @Getter private CommandManagerimpl commandManagerimpl;
    @Getter private InventoryManager inventoryManager;
    @Getter private AllowedColorsGUI allowedColorsGUI;

    private LeaderBoardManager leaderboardManager;
    private BukkitTask giveTask;
    ConsoleCommandSender console = Bukkit.getConsoleSender();

    @Override
    public void onEnable() {
        instance = this;
        rh();
        rc();
        if (getServer().getPluginManager().isPluginEnabled("ntdLuckyBlock")) {
            console.sendMessage(TextHandler.colorize("&7 __    __  ________  _______           __        _______  ________"));
            console.sendMessage(TextHandler.colorize("&7 |  \\  |  \\|        \\|       \\         |  \\      |       \\|\\"));
            console.sendMessage(TextHandler.colorize("&7 | $$\\ | $$ \\$$$$$$$$| $$$$$$$\\      | $$      | $$$$$$$    \\\\$$$$$$$$"));
            console.sendMessage(TextHandler.colorize("&7 | $$$\\| $$   | $$   | $$  | $$ ______ | $$      | $$__/ $$    | $$  "));
            console.sendMessage(TextHandler.colorize("&7 | $$$$\\ $$   | $$   | $$  | $$|      \\|$$      | $$    $$     | $$ "));
            console.sendMessage(TextHandler.colorize("&7 | $$\\$$ $$   | $$   | $$  | $$ \\$$$$$$|$$      | $$$$$$$\\     | $$"));
            console.sendMessage(TextHandler.colorize("&7 | $$ \\$$$$   | $$   | $$__/ $$        | $$_____ | $$__/ $$    | $$"));
            console.sendMessage(TextHandler.colorize("&7 | $$ \\$$$$   | $$   | $$__/ $$        | $$_____ | $$__/ $$    | $$"));
            console.sendMessage(TextHandler.colorize("&7 | $$ \\$$$$   | $$   | $$__/ $$        | $$_____ | $$__/ $$    | $$"));
            console.sendMessage(TextHandler.colorize("&7  \\$$   \\$$    \\$$     \\$$$$$$$         \\$$$$$$$$ \\$$$$$$$     | $$"));

            console.sendMessage(TextHandler.format("&7==========================================================================="));
            console.sendMessage(TextHandler.format(" "));
            console.sendMessage(TextHandler.format("&7( &entd-LuckyBlockTimer &8| &aEnabled &7)"));
            console.sendMessage(TextHandler.format(" "));
            console.sendMessage(TextHandler.format("&4&lPlease report any issue here&8: &fhttps://discord.gg/P8DCDMFfvZ"));
            console.sendMessage(TextHandler.format(" "));
            console.sendMessage(TextHandler.format("&7============================================================================"));

            startGiveTask();
            luckyBlockTimerLimit.scheduleReset();
        } else {
            getLogger().severe("You must use ntdluckyBlock Plugin !!!");
        }
    }

    @Override
    public void onDisable() {
        console.sendMessage(TextHandler.colorize("&7 __    __  ________  _______           __        _______  ________"));
        console.sendMessage(TextHandler.colorize("&7 |  \\  |  \\|        \\|       \\         |  \\      |       \\|\\"));
        console.sendMessage(TextHandler.colorize("&7 | $$\\ | $$ \\$$$$$$$$| $$$$$$$\\      | $$      | $$$$$$$    \\\\$$$$$$$$"));
        console.sendMessage(TextHandler.colorize("&7 | $$$\\| $$   | $$   | $$  | $$ ______ | $$      | $$__/ $$    | $$  "));
        console.sendMessage(TextHandler.colorize("&7 | $$$$\\ $$   | $$   | $$  | $$|      \\|$$      | $$    $$     | $$ "));
        console.sendMessage(TextHandler.colorize("&7 | $$\\$$ $$   | $$   | $$  | $$ \\$$$$$$|$$      | $$$$$$$\\     | $$"));
        console.sendMessage(TextHandler.colorize("&7 | $$ \\$$$$   | $$   | $$__/ $$        | $$_____ | $$__/ $$    | $$"));
        console.sendMessage(TextHandler.colorize("&7 | $$ \\$$$$   | $$   | $$__/ $$        | $$_____ | $$__/ $$    | $$"));
        console.sendMessage(TextHandler.colorize("&7 | $$ \\$$$$   | $$   | $$__/ $$        | $$_____ | $$__/ $$    | $$"));
        console.sendMessage(TextHandler.colorize("&7  \\$$   \\$$    \\$$     \\$$$$$$$         \\$$$$$$$$ \\$$$$$$$     | $$"));

        console.sendMessage(TextHandler.format("&7==========================================================================="));
        console.sendMessage(TextHandler.format(" "));
        console.sendMessage(TextHandler.format("&7( &entd-LuckyBlockTimer &8| &cDisabled &7)"));
        console.sendMessage(TextHandler.format(" "));
        console.sendMessage(TextHandler.format("&4&lPlease report any issue here&8: &fhttps://discord.gg/P8DCDMFfvZ"));
        console.sendMessage(TextHandler.format(" "));
        console.sendMessage(TextHandler.format("&7==========================================================================="));

        if (giveTask != null) {
            giveTask.cancel();
        }
        if (playerDataManager != null) {
            playerDataManager.close();
        }
    }
    private void rh() {
        new UpdateChecker(this, 119846).getLatestVersion(version -> {
            String currentVersion = this.getDescription().getVersion();
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                Logger.log(Logger.LogLevel.SUCCESS, "&eNo updates available.");
                Logger.log(Logger.LogLevel.SUCCESS, "&aThe plugin is up to date. Version: &3" + currentVersion);
            } else {
                Logger.log(Logger.LogLevel.WARNING, "&aThere's new update visit https://www.spigotmc.org/resources/ntd-luckyblock-timer.119846/");
                Logger.log(Logger.LogLevel.WARNING, "&eYou are currently running version: " + currentVersion);
            }
        });
        leaderboardManager = new LeaderBoardManager();


        settingsConfig = new SettingsConfig("settings.yml", getDataFolder().getPath());
        playerDataManager = new PlayerDataManager(this);

        luckyBlockTimerLimit = new LuckyBlockTimerLimit();

        loadAllowedColors();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolder(leaderboardManager).register();
        } else {
            Logger.log(Logger.LogLevel.WARNING, "PlaceholderAPI not found! Custom placeholders will not work.");
        }
        commandManagerimpl =new CommandManagerimpl();
        (this.inventoryManager = new InventoryManager(this)).init();
        allowedColorsGUI = new AllowedColorsGUI();
        ListenerHandler.registerlisteners(new LBTGUI(),
                new ResetInterval(),
                new LBTEvent(leaderboardManager),
                new AllowedColorsGUI());

    }

    public void startGiveTask() {
        if (giveTask != null) {
            giveTask.cancel();
        }

        boolean perPlayerTimer = settingsConfig.getConfig().getBoolean("per-player-timer");
        boolean perColorTimer = settingsConfig.getConfig().getBoolean("per-color-timer");
        int defaultGiveTimer = settingsConfig.getConfig().getInt("givetimer") * 20;

        ConfigurationSection colorTimersSection = settingsConfig.getConfig().getConfigurationSection("allowed-colors");

        if (perPlayerTimer) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                    handlePlayerLuckyBlockAsync(player);
                }, 0L, defaultGiveTimer);
            }
        }
        else if (perColorTimer && colorTimersSection != null) {
            for (String color : colorTimersSection.getKeys(false)) {
                int colorGiveTimer = colorTimersSection.getInt(color) * 20;

                Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        handlePlayerLuckyBlockAsync(player, color);
                    }
                }, 0L, colorGiveTimer);
            }
        } else if (perColorTimer && perPlayerTimer) {
            Logger.log(Logger.LogLevel.ERROR,"&cYou must only choose whether perplayertimer or percolortimer not both. Selecting to normal timer.");
            giveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    handlePlayerLuckyBlockAsync(player, null);
                }
            }, 0L, defaultGiveTimer);
        }else{
            giveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    handlePlayerLuckyBlockAsync(player, null);
                }
            }, 0L, defaultGiveTimer);
        }
    }

    private void handlePlayerLuckyBlockAsync(Player player, String color) {
        Bukkit.getScheduler().runTask(this, () -> {
            LuckyBlockTimerLimit timerLimit = getLuckyBlockTimerLimit();
            try {
                timerLimit.giveLuckyBlock(player, color);
            } catch (LuckyBlockNotLoadedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handlePlayerLuckyBlockAsync(Player player) {
        UUID playerUUID = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            int dailyLimit = luckyBlockTimerLimit.getPlayerDailyLimit(player);
            int currentCount = playerDataManager.getLuckyBlockCount(playerUUID);
            boolean hasReachedLimit = playerDataManager.getReachedLimit(playerUUID);

            Bukkit.getScheduler().runTask(this, () -> {
                if (dailyLimit != -1 && currentCount >= dailyLimit) {
                    if (!hasReachedLimit) {
                        player.sendMessage(TextHandler.format(settingsConfig.getConfig().getString("reached_limit_message")
                                .replace("{limit}", String.valueOf(dailyLimit))));
                    }
                    return;
                }
                playerDataManager.setReachedLimit(playerUUID, true);
                try {
                    luckyBlockTimerLimit.giveLuckyBlock(player);
                } catch (LuckyBlockNotLoadedException e) {
                    throw new RuntimeException(e);
                }
                if (hasReachedLimit) {
                    playerDataManager.setReachedLimit(playerUUID, false);
                }
            });
        });
    }

    public void loadAllowedColors() {
        ConfigurationSection section = settingsConfig.getConfig().getConfigurationSection("allowed-colors");

        if (section == null || section.getKeys(false).isEmpty()) {
            Logger.log(Logger.LogLevel.ERROR, "&4No colors found in settings.yml, defaulting to basic colors.");
            section = settingsConfig.getConfig().createSection("allowed-colors");

            Map<String, Integer> defaultColors = Map.of(
                    "Red", 5,
                    "Blue", 7,
                    "Green", 3,
                    "Yellow", 10
            );

            for (Map.Entry<String, Integer> entry : defaultColors.entrySet()) {
                section.set(entry.getKey(), entry.getValue());
            }
            settingsConfig.save();
        }

        Logger.log(Logger.LogLevel.INFO, "Loaded Allowed Colors:");
    }

    public String getRandomAllowedColor() {
        ConfigurationSection section = settingsConfig.getConfig().getConfigurationSection("allowed-colors");

        if (section == null || section.getKeys(false).isEmpty()) {
            getLogger().warning("Allowed colors list is empty or not loaded!");
            return "Yellow";
        }

        List<String> colors = new ArrayList<>(section.getKeys(false));
        return colors.get(ThreadLocalRandom.current().nextInt(colors.size()));
    }
    public void rc(){
        commandManagerimpl.registerAll(this,
                new LBTCommand());
    }
}

