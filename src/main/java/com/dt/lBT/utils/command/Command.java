package com.dt.lBT.utils.command;


import com.dt.lBT.utils.TextHandler;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


public abstract class Command<Sender> implements CommandExecutor, TabCompleter {
    @Getter
    private @NonNull final CommandInfo commandInfo;

    public Command() {
        commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        Objects.requireNonNull(commandInfo, "Command must have CommandInfo annotation");

    }

    @Override
    public boolean onCommand(CommandSender s, org.bukkit.command.Command command, String label, String[] args) {

        if (!commandInfo.permission().isEmpty() && !s.hasPermission(commandInfo.permission())) {
            s.sendMessage(TextHandler.format("&c&lSorry! You don't have enough permissions"));
            return true;
        }
        Sender sender = (Sender) s;
        if (check(sender,args)) execute(sender,args);
        return true;
    }

    public abstract boolean execute(Sender sender, String[] args);

    public abstract boolean check(Sender sender, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (check((Sender) sender, args)) {
            return tabComplete(sender, args); // Return suggestions if the condition is met
        }
        return Collections.emptyList(); // Return empty list to hide the command from the tab completer
    }

    public abstract List<String> tabComplete(CommandSender sender, String[] args);


}
