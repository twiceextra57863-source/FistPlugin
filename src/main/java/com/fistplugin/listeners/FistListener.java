package com.fistplugin.listeners;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class FistListener implements Listener {
    
    private final FistPlugin plugin;
    
    public FistListener(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only handle right clicks
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // Only handle main hand
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        FistType fist = plugin.getFistManager().getPlayerFist(player);
        
        // Agar player ke paas fist nahi hai, toh normal interaction allow karo
        if (fist == null) {
            return;
        }
        
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean isCrouching = player.isSneaking();
        
        // PEHLE CHECK: Kya ability cooldown me hai?
        String cooldownKey = isCrouching ? fist.name() + "_CROUCH" : fist.name() + "_RIGHT";
        boolean isOnCooldown = plugin.getCooldownManager().isOnCooldown(player, cooldownKey);
        
        // AGAR COOLDOWN ME HAI, toh normal interaction allow karo (event cancel mat karo)
        if (isOnCooldown) {
            // Sirf batado ki cooldown me hai, lekin interaction rokoge mat
            if (item != null && item.getType() != Material.AIR) {
                // Item use karne do - return karo bina cancel kiye
                return;
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                // Block interact karne do - return karo bina cancel kiye
                return;
            }
            // Agar haath empty hai aur cooldown me hai, toh kuch mat karo
            return;
        }
        
        // AB AGAR COOLDOWN ME NAHI HAI, tabhi ability trigger karo
        boolean shouldTriggerAbility = false;
        
        // Decide when to trigger ability
        if (isCrouching) {
            // Crouch + right click - ALWAYS trigger ability (cooldown check already done)
            shouldTriggerAbility = true;
        } else {
            // Normal right click - trigger only if hand is empty
            if (item == null || item.getType() == Material.AIR) {
                shouldTriggerAbility = true;
            }
        }
        
        // Agar ability trigger karna hai
        if (shouldTriggerAbility) {
            event.setCancelled(true); // Sirf tab cancel karo jab ability use kar rahe ho
            
            if (isCrouching) {
                // Crouch right click ability
                boolean success = plugin.getAbilityManager().executeCrouchRightClick(player, fist);
                
                if (success) {
                    int cooldown = plugin.getAbilityManager().getCrouchClickCooldown(fist);
                    plugin.getCooldownManager().setCooldown(player, cooldownKey, cooldown);
                    player.getWorld().playSound(player.getLocation(), fist.getSound(), 1.0f, 1.2f);
                    plugin.getParticleManager().spawnFistParticles(player, fist);
                }
            } else {
                // Normal right click ability
                boolean success = plugin.getAbilityManager().executeRightClick(player, fist);
                
                if (success) {
                    int cooldown = plugin.getAbilityManager().getRightClickCooldown(fist);
                    plugin.getCooldownManager().setCooldown(player, cooldownKey, cooldown);
                    player.getWorld().playSound(player.getLocation(), fist.getSound(), 1.0f, 1.0f);
                    plugin.getParticleManager().spawnFistParticles(player, fist);
                }
            }
        }
        // Agar ability trigger nahi karni, toh kuch mat karo - normal interaction hoga
    }
    
    // Check if item is something that should always be usable
    private boolean isNormalItem(Material material) {
        if (material == null) return false;
        
        // Food items
        if (material.isEdible()) return true;
        
        // Interactive blocks/items
        String name = material.name();
        return name.endsWith("_DOOR") ||
               name.endsWith("_TRAPDOOR") ||
               name.endsWith("_FENCE_GATE") ||
               name.endsWith("_BUTTON") ||
               name.endsWith("_PRESSURE_PLATE") ||
               material == Material.LEVER ||
               material == Material.CHEST ||
               material == Material.FURNACE ||
               material == Material.CRAFTING_TABLE ||
               material == Material.ENCHANTING_TABLE ||
               material == Material.ANVIL ||
               material == Material.POTION ||
               material == Material.MILK_BUCKET ||
               material == Material.HONEY_BOTTLE;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getDataManager().savePlayerData(plugin.getFistManager().getPlayerData(player));
        plugin.getFistManager().unloadPlayerData(player.getUniqueId());
        plugin.getCooldownManager().clearCooldowns(player);
    }
}
