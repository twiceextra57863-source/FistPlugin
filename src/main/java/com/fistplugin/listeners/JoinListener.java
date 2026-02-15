package com.fistplugin.listeners;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import com.fistplugin.data.PlayerData;
import com.fistplugin.gui.SpinMenuGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {
    
    private final FistPlugin plugin;
    
    public JoinListener(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Load player data
        PlayerData data = plugin.getDataManager().loadPlayerData(player.getUniqueId());
        plugin.getFistManager().loadPlayerData(player.getUniqueId(), data);
        
        // Check if first join
        if (!player.hasPlayedBefore()) {
            // Show spin menu after 1 second
            new BukkitRunnable() {
                @Override
                public void run() {
                    SpinMenuGUI.openSpinMenu(player);
                }
            }.runTaskLater(plugin, 20L);
        }
        
        // Start ambient particles
        startAmbientParticles(player);
        
        // Start heart indicator display
        startHeartIndicator(player);
        
        // Send welcome message
        player.sendMessage("§8§m+----------------------------+");
        player.sendMessage("§6👊 Welcome to FistPlugin! §e" + player.getName());
        player.sendMessage("§7Type §e/fist help §7for commands");
        
        if (plugin.getFistManager().hasFist(player)) {
            FistType fist = plugin.getFistManager().getPlayerFist(player);
            
            // Get resource pack symbol or fallback
            String symbol = plugin.getConfig().getString("resource-pack.fallback-symbol", "⚡");
            
            player.sendMessage("§aYour fist: " + fist.getDisplayName() + " §7" + symbol);
        } else {
            player.sendMessage("§cYou don't have a fist yet! Use §e/fist spin");
        }
        
        player.sendMessage("§8§m+----------------------------+");
    }
    
    private void startHeartIndicator(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                
                FistType fist = plugin.getFistManager().getPlayerFist(player);
                if (fist == null) return;
                
                // Get symbol based on fist type (will be replaced by resource pack textures)
                String symbol = getFistSymbol(fist);
                
                // Send to action bar above heart (using scoreboard or custom method)
                // For now, using a simple method - you can use ProtocolLib for better placement
                player.sendPlayerListHeaderFooter("§6§lFIST: " + fist.getDisplayName() + " " + symbol, "");
            }
        }.runTaskTimer(plugin, 0L, 100L); // Update every 5 seconds
    }
    
    private String getFistSymbol(FistType fist) {
        // These symbols will be replaced by resource pack textures
        String fallback = plugin.getConfig().getString("resource-pack.fallback-symbol", "∆");
        
        switch(fist) {
            case ORB: return "§6" + fallback;
            case BLOSSOM: return "§d" + fallback;
            case BEAST: return "§c" + fallback;
            case WATER: return "§b" + fallback;
            case REALITY: return "§5" + fallback;
            case COSMIC: return "§3" + fallback;
            case WOLF: return "§7" + fallback;
            case BOMB: return "§4" + fallback;
            case VOID: return "§8" + fallback;
            case PHANTOM: return "§f" + fallback;
            default: return fallback;
        }
    }
    
    private void startAmbientParticles(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                
                FistType fist = plugin.getFistManager().getPlayerFist(player);
                if (fist == null) return;
                
                // Spawn ambient particles continuously
                plugin.getParticleManager().spawnIdleParticles(player, fist);
            }
        }.runTaskTimer(plugin, 0L, 5L); // Update every 5 ticks
    }
}
