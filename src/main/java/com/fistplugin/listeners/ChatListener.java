package com.fistplugin.listeners;

import com.fistplugin.FistPlugin;
import com.fistplugin.abilities.CosmicFist;
import com.fistplugin.data.FistType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    
    private final FistPlugin plugin;
    
    public ChatListener(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Check if player has CosmicFist
        if (!plugin.getFistManager().hasFist(player) || 
            plugin.getFistManager().getPlayerFist(player) != FistType.COSMIC) {
            return;
        }
        
        // Get CosmicFist instance
        CosmicFist cosmicFist = (CosmicFist) plugin.getAbilityManager().getAbility(FistType.COSMIC);
        
        // Check if player is in selection mode
        if (cosmicFist.isInSelectionMode(player)) {
            event.setCancelled(true); // Cancel chat message
            
            // Process on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                cosmicFist.handleChatResponse(player, message);
            });
        }
    }
}
