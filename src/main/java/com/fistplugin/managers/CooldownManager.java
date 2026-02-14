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
    
    public CooldownManager(FistPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }
    
    public void setCooldown(Player player, String ability, int seconds) {
        Map<String, Long> playerCooldowns = cooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        playerCooldowns.put(ability, System.currentTimeMillis() + (seconds * 1000L));
        cooldowns.put(player.getUniqueId(), playerCooldowns);
        
        // Show cooldown in action bar
        showCooldownBar(player, ability, seconds);
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
    
    private void showCooldownBar(Player player, String ability, int totalSeconds) {
        new BukkitRunnable() {
            int secondsLeft = totalSeconds;
            
            @Override
            public void run() {
                if (secondsLeft <= 0 || !player.isOnline()) {
                    player.sendActionBar("§a✔ " + ability + " is ready!");
                    this.cancel();
                    return;
                }
                
                // Create cooldown bar
                StringBuilder bar = new StringBuilder("§c");
                int filled = (int) ((secondsLeft / (double) totalSeconds) * 20);
                
                for (int i = 0; i < 20; i++) {
                    if (i < filled) {
                        bar.append("█");
                    } else {
                        bar.append("§7░");
                    }
                }
                
                player.sendActionBar("§6" + ability + " §8" + bar.toString() + " §e" + secondsLeft + "s");
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    public void clearCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
