package com.fistplugin.managers;

import com.fistplugin.FistPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    
    private final FistPlugin plugin;
    private final Map<UUID, Map<String, Long>> cooldowns;
    private final Map<UUID, Map<String, BukkitRunnable>> activeBars;
    
    public CooldownManager(FistPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
        this.activeBars = new HashMap<>();
    }
    
    public void setCooldown(Player player, String ability, int seconds) {
        Map<String, Long> playerCooldowns = cooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        playerCooldowns.put(ability, System.currentTimeMillis() + (seconds * 1000L));
        cooldowns.put(player.getUniqueId(), playerCooldowns);
        
        // Cancel existing bar for this ability
        Map<String, BukkitRunnable> playerBars = activeBars.getOrDefault(player.getUniqueId(), new HashMap<>());
        if (playerBars.containsKey(ability)) {
            playerBars.get(ability).cancel();
        }
        
        // Show status bar
        BukkitRunnable barTask = createStatusBar(player, ability, seconds);
        playerBars.put(ability, barTask);
        activeBars.put(player.getUniqueId(), playerBars);
        barTask.runTaskTimer(plugin, 0L, 2L); // Update every 2 ticks for smooth animation
    }
    
    private BukkitRunnable createStatusBar(Player player, String ability, int totalSeconds) {
        return new BukkitRunnable() {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + (totalSeconds * 1000L);
            
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                
                long currentTime = System.currentTimeMillis();
                long remaining = endTime - currentTime;
                
                if (remaining <= 0) {
                    // Show ready message with resource pack symbol
                    String symbol = plugin.getConfig().getString("resource-pack.fallback-symbol", "✔");
                    player.sendActionBar("§a§l" + symbol + " §a" + ability + " READY! " + symbol);
                    
                    // Remove from active bars
                    Map<String, BukkitRunnable> playerBars = activeBars.get(player.getUniqueId());
                    if (playerBars != null) {
                        playerBars.remove(ability);
                    }
                    cancel();
                    return;
                }
                
                // Calculate progress
                int totalMillis = totalSeconds * 1000;
                int elapsed = totalMillis - (int) remaining;
                double progress = (double) elapsed / totalMillis;
                
                // Create status bar (20 characters long)
                int barLength = 20;
                int filledLength = (int) (progress * barLength);
                
                StringBuilder bar = new StringBuilder();
                
                // Filled part (█)
                for (int i = 0; i < filledLength; i++) {
                    bar.append("§a█");
                }
                
                // Empty part (▒)
                for (int i = filledLength; i < barLength; i++) {
                    bar.append("§7▒");
                }
                
                // Get resource pack symbol or fallback
                String symbol = plugin.getConfig().getString("resource-pack.fallback-symbol", "⏳");
                
                // Time text
                int secondsLeft = (int) Math.ceil(remaining / 1000.0);
                String timeText = String.format(" §e%d§7s", secondsLeft);
                
                // Send action bar
                player.sendActionBar("§6" + ability + " §8" + bar.toString() + timeText + " " + symbol);
            }
        };
    }
    
    public boolean isOnCooldown(Player player, String ability) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return false;
        
        Long cooldownTime = playerCooldowns.get(ability);
        if (cooldownTime == null) return false;
        
        return System.currentTimeMillis() < cooldownTime;
    }
    
    public int getRemainingCooldown(Player player, String ability) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return 0;
        
        Long cooldownTime = playerCooldowns.get(ability);
        if (cooldownTime == null) return 0;
        
        long remaining = cooldownTime - System.currentTimeMillis();
        return (int) Math.max(0, remaining / 1000);
    }
    
    public void clearCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
        
        // Cancel all active bars
        Map<String, BukkitRunnable> playerBars = activeBars.remove(player.getUniqueId());
        if (playerBars != null) {
            playerBars.values().forEach(BukkitRunnable::cancel);
        }
    }
    
    public void clearAllCooldowns() {
        cooldowns.clear();
        
        // Cancel all active bars
        for (Map<String, BukkitRunnable> playerBars : activeBars.values()) {
            playerBars.values().forEach(BukkitRunnable::cancel);
        }
        activeBars.clear();
    }
}
