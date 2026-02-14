package com.fistplugin.commands;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import com.fistplugin.gui.FistSelectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class FistCommand implements CommandExecutor {
    
    private final FistPlugin plugin;
    
    public FistCommand(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "help":
                sendHelp(sender);
                break;
                
            case "menu":
            case "gui":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    FistSelectorGUI.openGUI(player);
                } else {
                    sender.sendMessage("§cOnly players can use this command!");
                }
                break;
                
            case "info":
                if (args.length >= 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        showInfo(sender, target);
                    } else {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else if (sender instanceof Player) {
                    showInfo(sender, (Player) sender);
                } else {
                    sender.sendMessage("§cUsage: /fist info <player>");
                }
                break;
                
            case "stats":
                if (args.length >= 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        showStats(sender, target);
                    } else {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else if (sender instanceof Player) {
                    showStats(sender, (Player) sender);
                } else {
                    sender.sendMessage("§cUsage: /fist stats <player>");
                }
                break;
                
            case "list":
                listFists(sender);
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§6§lFistPlugin Commands");
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§e/fist help §7- Show this help");
        sender.sendMessage("§e/fist menu §7- Open fist selector");
        sender.sendMessage("§e/fist info [player] §7- Show fist info");
        sender.sendMessage("§e/fist stats [player] §7- Show player stats");
        sender.sendMessage("§e/fist list §7- List all fists");
        sender.sendMessage("§8§m+----------------------------+");
    }
    
    private void showInfo(CommandSender sender, Player target) {
        if (plugin.getFistManager().hasFist(target)) {
            FistType fist = plugin.getFistManager().getPlayerFist(target);
            sender.sendMessage("§8§m+----------------------------+");
            sender.sendMessage("§6" + target.getName() + "'s Fist: " + fist.getDisplayName());
            sender.sendMessage("§7" + fist.getLore());
            sender.sendMessage("§eRight-click: §7" + fist.getRightClickCooldown() + "s cooldown");
            sender.sendMessage("§eCrouch+Right-click: §7" + fist.getCrouchClickCooldown() + "s cooldown");
            sender.sendMessage("§8§m+----------------------------+");
        } else {
            sender.sendMessage("§c" + target.getName() + " doesn't have a fist!");
        }
    }
    
    private void showStats(CommandSender sender, Player target) {
        var data = plugin.getFistManager().getPlayerData(target);
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§6§l" + target.getName() + "'s Stats");
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§eKills: §7" + data.getKills());
        sender.sendMessage("§eDeaths: §7" + data.getDeaths());
        sender.sendMessage("§eK/D Ratio: §7" + String.format("%.2f", 
            data.getDeaths() > 0 ? (double) data.getKills() / data.getDeaths() : data.getKills()));
        sender.sendMessage("§eAbilities Used: §7" + data.getAbilitiesUsed());
        sender.sendMessage("§8§m+----------------------------+");
    }
    
    private void listFists(CommandSender sender) {
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§6§lAvailable Fists");
        sender.sendMessage("§8§m+----------------------------+");
        
        for (FistType fist : FistType.values()) {
            sender.sendMessage(fist.getDisplayName() + " §7- " + fist.getLore());
        }
        
        sender.sendMessage("§8§m+----------------------------+");
    }
}
