package com.dt.guildGate.manager;



import com.dt.guildGate.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class ListenerHandler {
    public static void registerlisteners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
            System.out.println("[RedStonePvP] registred listener "+listener.getClass().getName());
        }
    }
}
