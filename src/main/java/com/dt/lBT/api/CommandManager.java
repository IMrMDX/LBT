package com.dt.guildGate.api;

import com.dt.guildGate.utils.command.Command;
import org.bukkit.plugin.Plugin;

/**
 * The interface Module manager.
 *
 * @author <a href="https://github.com/ripwindows">ripwindows</a>
 */
public interface CommandManager {

    void registerAll(Plugin provider, Command<?>... commands);

    void register(Plugin provider, Command<?> command);

}
