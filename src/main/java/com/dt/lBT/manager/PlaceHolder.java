package com.dt.lBT.manager;

import com.dt.lBT.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlaceHolder extends PlaceholderExpansion {

    private final LeaderBoardManager leaderboardManager;

    public PlaceHolder(LeaderBoardManager leaderboardManager) {
        this.leaderboardManager = leaderboardManager;
    }

    @Override
    public String getIdentifier() {
        return "lbt";
    }

    @Override
    public String getAuthor() {
        return Main.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.equalsIgnoreCase("time")) {
            LuckyBlockTimerLimit timerLimit = Main.getInstance().getLuckyBlockTimerLimit();
            if (timerLimit == null) {
                return "No reset scheduled";
            }

            if (timerLimit.getTimeUntilNextReset().isZero()) {
                return "No reset scheduled";
            }
            if (params.equalsIgnoreCase("count")) {
                return String.valueOf(leaderboardManager.getLuckyBlockCount(player.getUniqueId()));
            }


            return timerLimit.getFormattedTimeUntilNextReset();
        }
        if (params.startsWith("top_")) {
            try {
                int rank = Integer.parseInt(params.split("_")[1]);
                Map<UUID, Integer> topPlayers = leaderboardManager.getTopPlayers(rank);
                UUID uuid = (UUID) topPlayers.keySet().toArray()[rank - 1];
                OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(uuid);
                return topPlayer.getName() + ": " + topPlayers.get(uuid);
            } catch (Exception e) {
                return "N/A";
            }
        }

        return null;
    }


}
