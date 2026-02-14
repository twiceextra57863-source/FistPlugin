package com.fistplugin.commands;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FistTabCompleter implements TabCompleter {
    
    private final List<String> fistCommands = Arrays.asList(
        "help", "menu", "stats", "info", "list", "top", "cooldowns", "toggle"
    );
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (command.getName().equalsIgnoreCase("fist")) {
            if (args.length == 1) {
                // Complete fist commands
                for (String cmd : fistCommands) {
                    if (cmd.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(cmd);
                    }
                }
            } else if (args.length == 2) {
                // Complete player names for stats/info commands
                if (args[0].equalsIgnoreCase("stats") || 
                    args[0].equalsIgnoreCase("info") || 
                    args[0].equalsIgnoreCase("top")) {
                    
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(player.getName());
                        }
                    }
                }
            }
        }
        
        else if (command.getName().equalsIgnoreCase("fistadmin")) {
            if (args.length == 1) {
                // Complete admin commands
                List<String> adminCommands = Arrays.asList(
                    "give", "remove", "reload", "reset", "clearall"
                );
                
                for (String cmd : adminCommands) {
                    if (cmd.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(cmd);
                    }
                }
            } else if (args.length == 2) {
                // Complete player names for give/remove/reset
                if (args[0].equalsIgnoreCase("give") || 
                    args[0].equalsIgnoreCase("remove") || 
                    args[0].equalsIgnoreCase("reset")) {
                    
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(player.getName());
                        }
                    }
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                // Complete fist names for give command
                for (FistType fist : FistType.values()) {
                    String fistName = fist.name().toLowerCase();
                    if (fistName.startsWith(args[2].toLowerCase())) {
                        completions.add(fist.name());
                    }
                }
            }
        }
        
        return completions;
    }
}
