package com.dt.lBT.manager;

import com.dt.lBT.Main;
import com.dt.lBT.utils.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;


public class LuckyBlockTimerLimit {
    private final PlayerDataManager playerDataManager = Main.getInstance().getPlayerDataManager();
    boolean limitsEnabled = Main.getInstance().getSettingsConfig().getConfig().getBoolean("limits-enabled", true);
    private final boolean perPlayerTimer = Main.getInstance().getSettingsConfig().getConfig().getBoolean("per-player-timer", false);


    private LocalDateTime nextResetTime;
    private long resetIntervalTicks;
    private int resetTaskId = -1;


    public void scheduleReset() {
        if (!limitsEnabled) {
            Logger.log(Logger.LogLevel.INFO, "Reset interval is disabled in the configuration. Player lucky block counts will not be reset.");
            return;
        }

        if (resetTaskId != -1) {
            Bukkit.getScheduler().cancelTask(resetTaskId);
        }

        resetIntervalTicks = getResetIntervalFromConfig();
        nextResetTime = LocalDateTime.now().plusSeconds(resetIntervalTicks / 20);

        if (perPlayerTimer) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                schedulePlayerReset(player);
            }
        } else {
            resetTaskId = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
                nextResetTime = LocalDateTime.now().plusSeconds(resetIntervalTicks / 20);
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    playerDataManager.resetAllCounts();
                    Logger.log(Logger.LogLevel.INFO, "Lucky block counts have been reset for all players!");
                }
            }, resetIntervalTicks, resetIntervalTicks).getTaskId();
        }

        Logger.log(Logger.LogLevel.INFO, "Next reset is scheduled in " + getFormattedTimeUntilNextReset());
    }
    private void schedulePlayerReset(Player player) {
        UUID playerUUID = player.getUniqueId();
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (player.isOnline()) {
                nextResetTime = LocalDateTime.now().plusSeconds(resetIntervalTicks / 20);
                playerDataManager.resetPlayerCount(playerUUID);
                Logger.log(Logger.LogLevel.INFO, "Lucky block counts have been reset for player " + player.getName() + "!");
            }
        }, resetIntervalTicks, resetIntervalTicks);
    }

    private long getResetIntervalFromConfig() {
        int timeValue = Main.getInstance().getSettingsConfig().getConfig().getInt("reset-interval.time-value", 30);
        String timeUnit = Main.getInstance().getSettingsConfig().getConfig().getString("reset-interval.time-unit", "seconds").toLowerCase();

        return switch (timeUnit) {
            case "minutes" -> timeValue * 60L * 20L;
            case "hours" -> timeValue * 60L * 60L * 20L;
            case "days" -> timeValue * 24L * 60L * 60L * 20L;
            default -> timeValue * 20L;
        };
    }

    public Duration getTimeUntilNextReset() {
        if (nextResetTime == null) {
            Logger.log(Logger.LogLevel.WARNING, "Next reset time has not been initialized yet.");
            return Duration.ZERO;
        }
        return Duration.between(LocalDateTime.now(), nextResetTime);
    }


    public void giveLuckyBlock(Player player) {
        if (limitsEnabled) {
            String randomColor = Main.getInstance().getRandomAllowedColor();
//            String command = Main.getInstance().getSettingsConfig().getConfig().getString("command")
//                    .replace("{player}",player.getName())
//                    .replace("{random}",randomColor);
            String command = "lb give " + player.getName() + " " + randomColor.toLowerCase() + " -silent";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);

            playerDataManager.setLuckyBlockCount(player.getUniqueId(), playerDataManager.getLuckyBlockCount(player.getUniqueId()) + 1);
        }else{
            String randomColor = Main.getInstance().getRandomAllowedColor();
            String command = "lb give " + player.getName() + " " + randomColor.toLowerCase() + " -silent";
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public int getPlayerDailyLimit(Player player) {
        if (player.hasPermission("lb.unlimited")) {
            return -1;
        }

        ConfigurationSection limitsSection = Main.getInstance().getSettingsConfig().getConfig().getConfigurationSection("limits");

        if (limitsSection != null) {
            for (String key : limitsSection.getKeys(false)) {
                String permission = limitsSection.getString(key + ".permission");
                int dailyLimit = limitsSection.getInt(key + ".dailyLimit");

                if (permission != null && player.hasPermission(permission)) {
                    return dailyLimit;
                }
            }
        }

        return 5;
    }

    public String getFormattedTimeUntilNextReset() {
        Duration timeLeft = getTimeUntilNextReset();
        long days = timeLeft.toDays();
        long hours = timeLeft.toHours() % 24;
        long minutes = timeLeft.toMinutes() % 60;
        long seconds = timeLeft.getSeconds() % 60;

        StringBuilder formattedTime = new StringBuilder();
        if (days > 0) formattedTime.append(days).append("d ");
        if (hours > 0) formattedTime.append(hours).append("h ");
        if (minutes > 0) formattedTime.append(minutes).append("m ");
        if (seconds > 0) formattedTime.append(seconds).append("s");

        return formattedTime.toString().trim();
    }
}
