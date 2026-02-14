package com.fistplugin.commands;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import com.fistplugin.data.PlayerData;
import com.fistplugin.gui.FistSelectorGUI;
import com.fistplugin.gui.StatsGUI;
import com.fistplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
            case "?":
                sendHelp(sender);
                break;
                
            case "menu":
            case "gui":
            case "selector":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    FistSelectorGUI gui = new FistSelectorGUI(plugin);
                    gui.openGUI(player);
                } else {
                    sender.sendMessage("§cOnly players can use this command!");
                }
                break;
                
            case "stats":
                handleStatsCommand(sender, args);
                break;
                
            case "info":
                handleInfoCommand(sender, args);
                break;
                
            case "list":
                listFists(sender);
                break;
                
            case "top":
            case "leaderboard":
                handleLeaderboardCommand(sender, args);
                break;
                
            case "cooldowns":
                if (sender instanceof Player) {
                    showCooldowns((Player) sender);
                } else {
                    sender.sendMessage("§cOnly players can use this command!");
                }
                break;
                
            case "toggle":
                if (sender instanceof Player) {
                    toggleFist((Player) sender);
                } else {
                    sender.sendMessage("§cOnly players can use this command!");
                }
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    /**
     * Send help message
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§6§lFistPlugin Commands");
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§e/fist help §7- Show this help");
        sender.sendMessage("§e/fist menu §7- Open fist selector GUI");
        sender.sendMessage("§e/fist stats [player] §7- View stats");
        sender.sendMessage("§e/fist info [player] §7- Show fist info");
        sender.sendMessage("§e/fist list §7- List all fists");
        sender.sendMessage("§e/fist top §7- View leaderboard");
        sender.sendMessage("§e/fist cooldowns §7- Check your cooldowns");
        sender.sendMessage("§e/fist toggle §7- Toggle fist on/off");
        sender.sendMessage("§8§m+----------------------------+");
        
        if (sender.hasPermission("fist.admin")) {
            sender.sendMessage("§c§lAdmin Commands:");
            sender.sendMessage("§c/fistadmin give <player> <fist> §7- Give fist");
            sender.sendMessage("§c/fistadmin remove <player> §7- Remove fist");
            sender.sendMessage("§c/fistadmin reload §7- Reload plugin");
            sender.sendMessage("§c/fistadmin reset <player> §7- Reset stats");
            sender.sendMessage("§8§m+----------------------------+");
        }
    }
    
    /**
     * Handle stats command
     */
    private void handleStatsCommand(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            // Check if player has permission to view others' stats
            if (!sender.hasPermission("fist.admin") && !sender.getName().equals(args[1])) {
                sender.sendMessage("§cYou don't have permission to view others' stats!");
                return;
            }
            
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                if (sender instanceof Player) {
                    StatsGUI statsGUI = new StatsGUI(plugin);
                    statsGUI.openStatsGUI((Player) sender, target);
                } else {
                    // Console view
                    showStatsConsole(sender, target);
                }
            } else {
                sender.sendMessage("§cPlayer not found!");
            }
        } else if (sender instanceof Player) {
            StatsGUI statsGUI = new StatsGUI(plugin);
            statsGUI.openStatsGUI((Player) sender, (Player) sender);
        } else {
            sender.sendMessage("§cUsage: /fist stats <player>");
        }
    }
    
    /**
     * Show stats in console
     */
    private void showStatsConsole(CommandSender sender, Player target) {
        PlayerData data = plugin.getFistManager().getPlayerData(target);
        FistType fist = plugin.getFistManager().getPlayerFist(target);
        
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§6§l" + target.getName() + "'s Stats");
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§eFist: §7" + (fist != null ? fist.getDisplayName() : "None"));
        sender.sendMessage("§eKills: §7" + data.getKills());
        sender.sendMessage("§eDeaths: §7" + data.getDeaths());
        sender.sendMessage("§eK/D: §7" + getKDRatio(data));
        sender.sendMessage("§eAbilities Used: §7" + data.getAbilitiesUsed());
        sender.sendMessage("§8§m+----------------------------+");
    }
    
    /**
     * Handle info command
     */
    private void handleInfoCommand(CommandSender sender, String[] args) {
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
    }
    
    /**
     * Show fist info
     */
    private void showInfo(CommandSender sender, Player target) {
        if (plugin.getFistManager().hasFist(target)) {
            FistType fist = plugin.getFistManager().getPlayerFist(target);
            
            sender.sendMessage("§8§m+----------------------------+");
            sender.sendMessage("§6" + target.getName() + "'s Fist: " + fist.getDisplayName());
            sender.sendMessage("§7" + fist.getLore());
            sender.sendMessage("§8§m+----------------------------+");
            sender.sendMessage("§eRight-click: §7" + getRightClickDescription(fist));
            sender.sendMessage("§eCooldown: §7" + fist.getRightClickCooldown() + "s");
            sender.sendMessage("§8§m+----------------------------+");
            sender.sendMessage("§eCrouch+Right-click: §7" + getCrouchClickDescription(fist));
            sender.sendMessage("§eCooldown: §7" + fist.getCrouchClickCooldown() + "s");
            sender.sendMessage("§8§m+----------------------------+");
        } else {
            sender.sendMessage("§c" + target.getName() + " doesn't have a fist!");
        }
    }
    
    /**
     * List all fists
     */
    private void listFists(CommandSender sender) {
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§6§lAvailable Fists");
        sender.sendMessage("§8§m+----------------------------+");
        
        for (FistType fist : FistType.values()) {
            sender.sendMessage(fist.getDisplayName() + " §7- " + fist.getLore());
        }
        
        sender.sendMessage("§8§m+----------------------------+");
        sender.sendMessage("§eUse §6/fist menu §eto select a fist!");
        sender.sendMessage("§8§m+----------------------------+");
    }
    
    /**
     * Handle leaderboard command
     */
    private void handleLeaderboardCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return;
        }
        
        Player player = (Player) sender;
        StatsGUI statsGUI = new StatsGUI(plugin);
        
        if (args.length >= 2) {
            // Try to find fist-specific leaderboard
            for (FistType fist : FistType.values()) {
                if (args[1].equalsIgnoreCase(fist.name())) {
                    statsGUI.openFistLeaderboard(player, fist);
                    return;
                }
            }
        }
        
        // Default to global leaderboard
        statsGUI.openGlobalLeaderboard(player);
    }
    
    /**
     * Show player's cooldowns
     */
    private void showCooldowns(Player player) {
        FistType fist = plugin.getFistManager().getPlayerFist(player);
        
        if (fist == null) {
            player.sendMessage("§cYou don't have a fist!");
            return;
        }
        
        player.sendMessage("§8§m+----------------------------+");
        player.sendMessage("§6§lYour Cooldowns");
        player.sendMessage("§8§m+----------------------------+");
        
        // Right click cooldown
        int rightRemaining = plugin.getCooldownManager().getRemainingCooldown(player, fist.name() + "_RIGHT");
        if (rightRemaining > 0) {
            player.sendMessage("§eRight-click: §c" + rightRemaining + "s remaining");
        } else {
            player.sendMessage("§eRight-click: §a✔ Ready");
        }
        
        // Crouch click cooldown
        int crouchRemaining = plugin.getCooldownManager().getRemainingCooldown(player, fist.name() + "_CROUCH");
        if (crouchRemaining > 0) {
            player.sendMessage("§eCrouch+Right-click: §c" + crouchRemaining + "s remaining");
        } else {
            player.sendMessage("§eCrouch+Right-click: §a✔ Ready");
        }
        
        player.sendMessage("§8§m+----------------------------+");
    }
    
    /**
     * Toggle fist on/off
     */
    private void toggleFist(Player player) {
        if (!plugin.getFistManager().hasFist(player)) {
            player.sendMessage("§cYou don't have a fist to toggle!");
            return;
        }
        
        // You can implement fist toggle functionality here
        // For example, temporarily disable fist abilities
        player.sendMessage("§aFist toggled!");
    }
    
    /**
     * Get K/D ratio
     */
    private String getKDRatio(PlayerData data) {
        int kills = data.getKills();
        int deaths = data.getDeaths();
        
        if (deaths == 0) {
            return kills > 0 ? "Perfect" : "0.0";
        }
        
        double ratio = (double) kills / deaths;
        return String.format("%.2f", ratio);
    }
    
    /**
     * Get right click ability description
     */
    private String getRightClickDescription(FistType fist) {
        switch(fist) {
            case ORB: return "Launch explosive fireball";
            case BLOSSOM: return "Launch freezing projectile";
            case BEAST: return "Growing damage over time";
            case WATER: return "Fly for 6 seconds";
            case REALITY: return "Raise terrain 5x5 area";
            case COSMIC: return "Spin target for 2 seconds";
            case WOLF: return "Dash forward and slash";
            case BOMB: return "Ghost bomb chases target";
            case VOID: return "Pull target towards you";
            case PHANTOM: return "Phase through reality (2s)";
            default: return "Unknown ability";
        }
    }
    
    /**
     * Get crouch click ability description
     */
    private String getCrouchClickDescription(FistType fist) {
        switch(fist) {
            case ORB: return "Create boxing arena (15s)";
            case BLOSSOM: return "Freeze and poison target (10s)";
            case BEAST: return "Shrink yourself (10s)";
            case WATER: return "Summon tsunami (6s)";
            case REALITY: return "Summon meteor shower (4 meteors)";
            case COSMIC: return "Hook target, left click to launch";
            case WOLF: return "Summon wolf clones (10s)";
            case BOMB: return "Laser destruction (13s)";
            case VOID: return "Void nova explosion";
            case PHANTOM: return "Possess target (7s)";
            default: return "Unknown ability";
        }
    }
}
