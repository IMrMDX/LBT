package com.dt.lBT.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeaderBoardManager {
    private final Map<UUID, Integer> luckyBlockCounts = new HashMap<>();

    public void incrementLuckyBlockCount(UUID playerUUID) {
        luckyBlockCounts.put(playerUUID, luckyBlockCounts.getOrDefault(playerUUID, 0) + 1);
    }

    public int getLuckyBlockCount(UUID playerUUID) {
        return luckyBlockCounts.getOrDefault(playerUUID, 0);
    }

    public Map<UUID, Integer> getTopPlayers(int limit) {
        return luckyBlockCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
    }
}
