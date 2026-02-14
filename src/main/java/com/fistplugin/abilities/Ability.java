package com.fistplugin.abilities;

import org.bukkit.entity.Player;

/**
 * Main interface for all fist abilities
 * Each fist must implement this interface
 */
public interface Ability {
    
    /**
     * Execute right click ability
     * @param player The player using the ability
     * @return true if ability was successfully executed
     */
    boolean onRightClick(Player player);
    
    /**
     * Execute crouch + right click ability
     * @param player The player using the ability
     * @return true if ability was successfully executed
     */
    boolean onCrouchRightClick(Player player);
    
    /**
     * Get the display name of the ability/fist
     * @return Display name with color codes
     */
    String getName();
    
    /**
     * Get the description of the ability/fist
     * @return Description text
     */
    String getDescription();
    
    /**
     * Get the cooldown for right click ability in seconds
     * @return Cooldown in seconds
     */
    default int getRightClickCooldown() {
        return 5; // Default 5 seconds
    }
    
    /**
     * Get the cooldown for crouch right click ability in seconds
     * @return Cooldown in seconds
     */
    default int getCrouchClickCooldown() {
        return 30; // Default 30 seconds
    }
    
    /**
     * Called when ability is successfully executed
     * @param player The player who used the ability
     */
    default void onAbilityUse(Player player) {
        // Track ability usage
        // Can be overridden by specific abilities
    }
    
    /**
     * Check if player can use this ability
     * @param player The player to check
     * @return true if player can use ability
     */
    default boolean canUse(Player player) {
        return player.hasPermission("fist.use." + getName().toLowerCase().replace(" ", ""));
    }
    
    /**
     * Get the unique identifier for this ability
     * @return Unique ID string
     */
    default String getIdentifier() {
        return getName().toLowerCase().replace(" ", "_");
    }
}
