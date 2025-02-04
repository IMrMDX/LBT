package com.dt.lBT.api;


import com.dt.lBT.Main;
import com.dt.lBT.utils.command.Command;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;


public class CommandManagerimpl implements CommandManager {

    @Override
    public void registerAll(Plugin provider, Command<?>... commands) {
        for (Command<?> command : commands) {
            // Register command executor
            Main.getInstance().getCommand(command.getCommandInfo().name()).setExecutor(command);

            // Register tab completer if available
            if (command instanceof org.bukkit.command.TabCompleter) {
                Main.getInstance().getCommand(command.getCommandInfo().name()).setTabCompleter((org.bukkit.command.TabCompleter) command);
            }

            System.out.println(command.getCommandInfo().name());
        }
        Main.getInstance().getLogger().log(Level.FINER, "Commands registered");
    }

    @Override
    public void register(Plugin provider, Command<?> command) {
        // Register command executor
        Main.getInstance().getCommand(command.getCommandInfo().name()).setExecutor(command);

        // Register tab completer if available
        if (command instanceof org.bukkit.command.TabCompleter) {
            Main.getInstance().getCommand(command.getCommandInfo().name()).setTabCompleter((org.bukkit.command.TabCompleter) command);
        }
    }
}
