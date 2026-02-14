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
        
        // Send welcome message
        player.sendMessage("§8§m+----------------------------+");
        player.sendMessage("§6👊 Welcome to FistPlugin! §e" + player.getName());
        player.sendMessage("§7Type §e/fist help §7for commands");
        
        if (plugin.getFistManager().hasFist(player)) {
            FistType fist = plugin.getFistManager().getPlayerFist(player);
            player.sendMessage("§aYour fist: " + fist.getDisplayName());
        } else {
            player.sendMessage("§cYou don't have a fist yet!");
        }
        
        player.sendMessage("§8§m+----------------------------+");
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
                
                // Spawn ambient particles every 10 ticks
                plugin.getParticleManager().spawnFistParticles(player, fist);
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
}
