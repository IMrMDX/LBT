package com.dt.lBT.manager;



import com.dt.lBT.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class ListenerHandler {
    public static void registerlisteners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
            System.out.println("ntd-LBT registred listener "+listener.getClass().getName());
        }
    }
}
