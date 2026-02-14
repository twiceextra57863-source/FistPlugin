package com.fistplugin.commands;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {
    
    private final FistPlugin plugin;
    
    public AdminCommand(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("fist.admin")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }
        
        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "give":
                if (args.length >= 3) {
                    giveFist(sender, args[1], args[2]);
                } else {
                    sender.sendMessage("§cUsage: /fistadmin give <player> <fist>");
                }
                break;
                
            case "remove":
                if (args.length >= 2) {
                    removeFist(sender, args[1]);
                } else {
                    sender.sendMessage("§cUsage: /fistadmin remove <player>");
                }
                break;
                
            case "reload":
                reloadPlugin(sender);
                break;
                
            case "clearall":
                clearAllFists(sender);
                break;
                
            case "reset":
                if (args.length >= 2) {
                    resetPlayer(sender, args[1]);
                } else {
                    sender.sendMessage("§cUsage: /fistadmin reset <player>");
                }
                break;
                
            default:
                sendAdminHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§c§lFistPlugin Admin Commands");
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§c/fistadmin give <player> <fist> §7- Give fist to player");
        sender.sendMessage("§c/fistadmin remove <player> §7- Remove player's fist");
        sender.sendMessage("§c/fistadmin reload §7- Reload plugin");
        sender.sendMessage("§c/fistadmin clearall §7- Clear all fists");
        sender.sendMessage("§c/fistadmin reset <player> §7- Reset player stats");
        sender.sendMessage("§8§m+----------------------------+");
    }
    
    private void giveFist(CommandSender sender, String playerName, String fistName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        try {
            FistType fist = FistType.valueOf(fistName.toUpperCase());
            
            // Remove old fist first
            if (plugin.getFistManager().hasFist(target)) {
                plugin.getFistManager().removeFist(target);
            }
            
            // Give new fist
            plugin.getFistManager().setPlayerFist(target, fist);
            
            sender.sendMessage("§a✅ Gave " + fist.getDisplayName() + " §ato " + target.getName());
            target.sendMessage("§aYou received " + fist.getDisplayName() + " §afrom an admin!");
            
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid fist! Use /fist list to see available fists.");
        }
    }
    
    private void removeFist(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        if (plugin.getFistManager().hasFist(target)) {
            plugin.getFistManager().removeFist(target);
            sender.sendMessage("§a✅ Removed fist from " + target.getName());
            target.sendMessage("§cYour fist has been removed by an admin!");
        } else {
            sender.sendMessage("§c" + target.getName() + " doesn't have a fist!");
        }
    }
    
    private void reloadPlugin(CommandSender sender) {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        plugin.getDataManager().loadAllPlayerData();
        sender.sendMessage("§a✅ Plugin reloaded!");
    }
    
    private void clearAllFists(CommandSender sender) {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getFistManager().hasFist(player)) {
                plugin.getFistManager().removeFist(player);
                count++;
            }
        }
        sender.sendMessage("§a✅ Cleared " + count + " fists from all online players!");
    }
    
    private void resetPlayer(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        // Reset player data
        var data = plugin.getFistManager().getPlayerData(target);
        data.setKills(0);
        data.setDeaths(0);
        data.setAbilitiesUsed(0);
        plugin.getDataManager().savePlayerData(data);
        
        sender.sendMessage("§a✅ Reset stats for " + target.getName());
        target.sendMessage("§cYour stats have been reset by an admin!");
    }
}
