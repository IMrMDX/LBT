package com.dt.lBT.config;

import com.dt.lBT.Main;
import com.dt.lBT.utils.TextHandler;
import com.dt.lBT.utils.command.Command;
import com.dt.lBT.utils.command.CommandInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = "lbt",permission = "lbt.admin.lbtcommand")
public class LBTCommand extends Command<CommandSender> {
    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
            if (args.length == 1){
                switch (args[0].toLowerCase()){
                    case "reloadconfig":
                        Main.getInstance().getSettingsConfig().reload();
                        Main.getInstance().getSettingsConfig().save();

                        Main.getInstance().loadAllowedColors();
                        Main.getInstance().startGiveTask();

                        Main.getInstance().getLuckyBlockTimerLimit().scheduleReset();
                        Main.getInstance().getPlayerDataManager().resetAllCounts();

                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;
                            Main.getInstance().getPlayerDataManager().setReachedLimit(player.getUniqueId(), false);
                        }
                        commandSender.sendMessage(TextHandler.format("&aLBT Config Reloaded and reset time has been updated"));

                     break;
                    case "gui":
                        commandSender.sendMessage(TextHandler.format("&cHi"));
                        break;
                    default:
                        commandSender.sendMessage(TextHandler.format("&cInvalid Argument, &6Usage:/lbt <reloadconfig/gui>"));
                        break;
                }
            }
        return false;
        }

    @Override
    public boolean check(CommandSender commandSender, String[] args) {
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> match = new ArrayList<>();
        if (args.length ==1){
            match.add("reloadconfig");
            match.add("gui");
        }
        if (args.length != 1){
            return Collections.singletonList("No Argument Here");
        }
        return match;
    }

}
