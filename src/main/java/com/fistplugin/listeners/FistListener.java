package com.fistplugin.listeners;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class FistListener implements Listener {
    
    private final FistPlugin plugin;
    
    public FistListener(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only handle right clicks, ignore left clicks
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // Only handle main hand (off hand ko ignore karo)
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        FistType fist = plugin.getFistManager().getPlayerFist(player);
        
        if (fist == null) return;
        
        // Check if player is sneaking (crouch)
        boolean isCrouching = player.isSneaking();
        
        // Cancel event to prevent interaction with blocks/items
        event.setCancelled(true);
        
        if (isCrouching) {
            // Crouch right click ability
            String cooldownKey = fist.name() + "_CROUCH";
            int cooldown = plugin.getAbilityManager().getCrouchClickCooldown(fist);
            
            if (plugin.getCooldownManager().isOnCooldown(player, cooldownKey)) {
                int remaining = plugin.getCooldownManager().getRemainingCooldown(player, cooldownKey);
                player.sendMessage("§c⏳ Crouch ability on cooldown! " + remaining + "s remaining");
                return;
            }
            
            boolean success = plugin.getAbilityManager().executeCrouchRightClick(player, fist);
            
            if (success) {
                plugin.getCooldownManager().setCooldown(player, cooldownKey, cooldown);
                player.getWorld().playSound(player.getLocation(), fist.getSound(), 1.0f, 1.2f);
                plugin.getParticleManager().spawnFistParticles(player, fist);
            }
        } else {
            // Normal right click ability
            String cooldownKey = fist.name() + "_RIGHT";
            int cooldown = plugin.getAbilityManager().getRightClickCooldown(fist);
            
            if (plugin.getCooldownManager().isOnCooldown(player, cooldownKey)) {
                int remaining = plugin.getCooldownManager().getRemainingCooldown(player, cooldownKey);
                player.sendMessage("§c⏳ Ability on cooldown! " + remaining + "s remaining");
                return;
            }
            
            boolean success = plugin.getAbilityManager().executeRightClick(player, fist);
            
            if (success) {
                plugin.getCooldownManager().setCooldown(player, cooldownKey, cooldown);
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
