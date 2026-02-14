package com.fistplugin.commands;

import org.bukkit.command.*;
import java.util.ArrayList;
import java.util.List;

public class FistTabCompleter implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("help");
            completions.add("menu");
            completions.add("info");
            completions.add("stats");
            completions.add("list");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("stats"))) {
            // Add online player names
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        
        // Filter based on current input
        List<String> result = new ArrayList<>();
        String input = args[args.length - 1].toLowerCase();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(input)) {
                result.add(completion);
            }
        }
        
        return result;
    }
}
