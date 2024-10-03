package com.dt.lBT;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextHandler {
    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }


    public static String colorize(String string) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        for (Matcher matcher = pattern.matcher(string); matcher.find(); matcher = pattern.matcher(string)) {
            String color = string.substring(matcher.start(), matcher.end());
            string = string.replace(color, "" + net.md_5.bungee.api.ChatColor.of(color));
        }
        string = net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

    public static TextComponent colorizetextcomponent(String string) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        for (Matcher matcher = pattern.matcher(string); matcher.find(); matcher = pattern.matcher(string)) {
            String color = string.substring(matcher.start(), matcher.end());
            string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color) + ""); // You're missing this replacing
        }
        string = ChatColor.translateAlternateColorCodes('&', string);

        return new TextComponent(TextComponent.fromLegacyText(string));
    }

    public static List<String> colorize(List<String> strings) {
        return strings.stream()
                .map(TextHandler::colorize)
                .collect(Collectors.toList());
    }
}
