package com.fistplugin.listeners;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class FistListener implements Listener {
    
    private final FistPlugin plugin;
    
    public FistListener(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;
        
        Player player = event.getPlayer();
        FistType fist = plugin.getFistManager().getPlayerFist(player);
        
        if (fist == null) return;
        
        boolean isCrouching = player.isSneaking();
        
        if (isCrouching) {
            // Check cooldown for crouch ability
            String cooldownKey = fist.name() + "_CROUCH";
            if (plugin.getCooldownManager().isOnCooldown(player, cooldownKey)) {
                int remaining = plugin.getCooldownManager().getRemainingCooldown(player, cooldownKey);
                player.sendMessage("§c⏳ Crouch ability on cooldown! " + remaining + "s remaining");
                return;
            }
            
            // Execute crouch ability
            boolean success = plugin.getAbilityManager().executeCrouchRightClick(player, fist);
            
            if (success) {
                // Set cooldown
                plugin.getCooldownManager().setCooldown(player, cooldownKey, fist.getCrouchClickCooldown());
                
                // Play sound and particles
                player.getWorld().playSound(player.getLocation(), fist.getSound(), 1.0f, 1.2f);
                plugin.getParticleManager().spawnFistParticles(player, fist);
            }
        } else {
            // Check cooldown for right click ability
            String cooldownKey = fist.name() + "_RIGHT";
            if (plugin.getCooldownManager().isOnCooldown(player, cooldownKey)) {
                int remaining = plugin.getCooldownManager().getRemainingCooldown(player, cooldownKey);
                player.sendMessage("§c⏳ Ability on cooldown! " + remaining + "s remaining");
                return;
            }
            
            // Execute right click ability
            boolean success = plugin.getAbilityManager().executeRightClick(player, fist);
            
            if (success) {
                // Set cooldown
                plugin.getCooldownManager().setCooldown(player, cooldownKey, fist.getRightClickCooldown());
                
                // Play sound and particles
                player.getWorld().playSound(player.getLocation(), fist.getSound(), 1.0f, 1.0f);
                plugin.getParticleManager().spawnFistParticles(player, fist);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Save player data
        plugin.getDataManager().savePlayerData(plugin.getFistManager().getPlayerData(player));
        
        // Unload from memory
        plugin.getFistManager().unloadPlayerData(player.getUniqueId());
        plugin.getCooldownManager().clearCooldowns(player);
    }
}
