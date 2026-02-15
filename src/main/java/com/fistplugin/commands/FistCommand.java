package com.fistplugin.commands;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import com.fistplugin.data.PlayerData;
import com.fistplugin.gui.FistSelectorGUI;
import com.fistplugin.gui.StatsGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class FistCommand implements CommandExecutor {
    
    private final FistPlugin plugin;
    private final StatsGUI statsGUI;
    
    public FistCommand(FistPlugin plugin) {
        this.plugin = plugin;
        this.statsGUI = new StatsGUI(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "help":
                sendHelp(player);
                break;
                
            case "menu":
                FistSelectorGUI.openGUI(player);
                break;
                
            case "info":
                if (args.length >= 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        showInfo(player, target);
                    } else {
                        player.sendMessage("§cPlayer not found!");
                    }
                } else {
                    showInfo(player, player);
                }
                break;
                
            case "stats":
                if (args.length >= 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        // Fixed: Pass Player object instead of UUID
                        statsGUI.openStatsGUI(player, target.getUniqueId());
                    } else {
                        player.sendMessage("§cPlayer not found!");
                    }
                } else {
                    statsGUI.openStatsGUI(player, player.getUniqueId());
                }
                break;
                
            case "list":
                listFists(player);
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§8§m+----------------------------+");
        player.sendMessage("§6§lFistPlugin Commands");
        player.sendMessage("§8§m+----------------------------+");
        player.sendMessage("§e/fist help §7- Show this help");
        player.sendMessage("§e/fist menu §7- Open fist selector");
        player.sendMessage("§e/fist info [player] §7- Show fist info");
        player.sendMessage("§e/fist stats [player] §7- Show player stats");
        player.sendMessage("§e/fist list §7- List all fists");
        player.sendMessage("§8§m+----------------------------+");
    }
    
    private void showInfo(Player player, Player target) {
        if (plugin.getFistManager().hasFist(target)) {
            FistType fist = plugin.getFistManager().getPlayerFist(target);
            player.sendMessage("§8§m+----------------------------+");
            player.sendMessage("§6" + target.getName() + "'s Fist: " + fist.getDisplayName());
            player.sendMessage("§7" + fist.getLore());
            player.sendMessage("§8§m+----------------------------+");
        } else {
            player.sendMessage("§c" + target.getName() + " doesn't have a fist!");
        }
    }
    
    private void listFists(Player player) {
        player.sendMessage("§8§m+----------------------------+");
        player.sendMessage("§6§lAvailable Fists");
        player.sendMessage("§8§m+----------------------------+");
        
        for (FistType fist : FistType.values()) {
            player.sendMessage(fist.getDisplayName() + " §7- " + fist.getLore());
        }
        
        player.sendMessage("§8§m+----------------------------+");
    }
}
