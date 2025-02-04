package com.dt.lBT.events;

import com.dt.lBT.manager.LeaderBoardManager;
import me.DenBeKKer.ntdLuckyBlock.api.events.LuckyBlockBreakEvent;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LBTEvent implements Listener {
    private final LeaderBoardManager leaderboardManager;

    public LBTEvent(LeaderBoardManager leaderboardManager) {
        this.leaderboardManager = leaderboardManager;
    }

    @EventHandler
    public void onLuckyBlockBreak(LuckyBlockBreakEvent e) {
        Player player = e.getPlayer();
        leaderboardManager.incrementLuckyBlockCount(player.getUniqueId());
    }
}
