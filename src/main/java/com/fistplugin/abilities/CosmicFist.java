package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class CosmicFist extends BaseAbility {
    
    private final Map<UUID, List<Location>> selectedLocations = new HashMap<>();
    private final Map<UUID, Integer> selectionStep = new HashMap<>();
    
    public CosmicFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click - Start location selection
        if (!selectionStep.containsKey(player.getUniqueId())) {
            // Start selection mode
            selectionStep.put(player.getUniqueId(), 1);
            selectedLocations.put(player.getUniqueId(), new ArrayList<>());
            
            player.sendMessage("§3✨ Cosmic Teleportation - Step 1/3");
            player.sendMessage("§7Look at a block and right-click to select first location");
            
            // Cancel after 30 seconds if not completed
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (selectionStep.containsKey(player.getUniqueId())) {
                        selectionStep.remove(player.getUniqueId());
                        selectedLocations.remove(player.getUniqueId());
                        player.sendMessage("§cTeleportation selection cancelled (timeout)");
                    }
                }
            }.runTaskLater(plugin, 600L); // 30 seconds
        } else {
            // Select current target block
            Block targetBlock = player.getTargetBlockExact(50);
            if (targetBlock == null || targetBlock.getType() == Material.AIR) {
                player.sendMessage("§cPlease look at a valid block!");
                return false;
            }
            
            int step = selectionStep.get(player.getUniqueId());
            List<Location> locations = selectedLocations.get(player.getUniqueId());
            
            Location selectedLoc = targetBlock.getLocation().add(0.5, 1, 0.5);
            locations.add(selectedLoc);
            
            // Visual feedback
            player.getWorld().spawnParticle(Particle.PORTAL, selectedLoc, 30, 0.5, 0.5, 0.5, 0.1);
            player.getWorld().playSound(selectedLoc, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            
            if (step < 3) {
                // Next step
                selectionStep.put(player.getUniqueId(), step + 1);
                player.sendMessage("§3✨ Location " + step + " selected! (" + step + "/3)");
                player.sendMessage("§7Select location " + (step + 1) + "/3");
            } else {
                // All 3 locations selected - show menu
                showTeleportMenu(player, locations);
                selectionStep.remove(player.getUniqueId());
                selectedLocations.remove(player.getUniqueId());
            }
        }
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    private void showTeleportMenu(Player player, List<Location> locations) {
        player.sendMessage("§8§m+----------------------------+");
        player.sendMessage("§6§l✨ Cosmic Teleportation");
        player.sendMessage("§8§m+----------------------------+");
        
        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i);
            player.sendMessage("§e" + (i+1) + ". §7Location " + (i+1) + 
                " §8- §fX: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());
        }
        
        player.sendMessage("§8§m+----------------------------+");
        player.sendMessage("§aClick on a number in chat to teleport!");
        player.sendMessage("§cOr type 'cancel' to cancel");
        
        // Wait for chat response
        waitForChatResponse(player, locations);
    }
    
    private void waitForChatResponse(Player player, List<Location> locations) {
        new BukkitRunnable() {
            int timeout = 200; // 10 seconds
            
            @Override
            public void run() {
                if (timeout <= 0) {
                    player.sendMessage("§cTeleportation cancelled (timeout)");
                    cancel();
                    return;
                }
                timeout--;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        // This will be handled by a separate listener
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click - Teleport to selected location immediately
        if (selectionStep.containsKey(player.getUniqueId())) {
            // Cancel selection
            selectionStep.remove(player.getUniqueId());
            selectedLocations.remove(player.getUniqueId());
            player.sendMessage("§cTeleportation cancelled!");
            return true;
        }
        
        // Original cosmic hook ability
        final LivingEntity target = getTargetEntity(player, 40);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        Location start = player.getEyeLocation();
        Location end = target.getLocation().add(0, 1, 0);
        
        // Hook particles
        spawnLineParticles(start, end, Particle.END_ROD, 0.3);
        
        // Pull target towards player
        Vector pull = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.5);
        target.setVelocity(pull);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1.0f, 0.5f);
        player.sendMessage("§3🪝 Target pulled!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    public void handleChatResponse(Player player, String message, List<Location> locations) {
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage("§cTeleportation cancelled!");
            return;
        }
        
        try {
            int choice = Integer.parseInt(message);
            if (choice >= 1 && choice <= locations.size()) {
                Location targetLoc = locations.get(choice - 1);
                
                // Teleport with effects
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 1, 1, 1, 0.5);
                
                player.teleport(targetLoc);
                
                player.getWorld().spawnParticle(Particle.PORTAL, targetLoc, 50, 1, 1, 1, 0.5);
                player.getWorld().playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                
                player.sendMessage("§a✨ Teleported to location " + choice + "!");
            } else {
                player.sendMessage("§cInvalid choice! Please enter 1-" + locations.size());
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid input! Please enter a number or 'cancel'");
        }
    }
    
    @Override
    public String getName() {
        return "Cosmic Fist";
    }
    
    @Override
    public String getDescription() {
        return "§3Teleport to 3 selected locations or pull enemies";
    }
}
