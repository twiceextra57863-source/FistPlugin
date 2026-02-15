package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class CosmicFist extends BaseAbility {
    
    private final Map<UUID, List<Location>> selectedLocations = new HashMap<>();
    private final Map<UUID, Integer> selectionStep = new HashMap<>();
    private final Map<UUID, Boolean> selectionMode = new HashMap<>();
    
    public CosmicFist(FistPlugin plugin) {
        super(plugin);
    }
    
    public boolean isInSelectionMode(Player player) {
        return selectionMode.getOrDefault(player.getUniqueId(), false);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click - Start location selection
        if (!selectionMode.getOrDefault(player.getUniqueId(), false)) {
            // Start selection mode
            selectionMode.put(player.getUniqueId(), true);
            selectionStep.put(player.getUniqueId(), 1);
            selectedLocations.put(player.getUniqueId(), new ArrayList<>());
            
            player.sendMessage("§8§m+----------------------------+");
            player.sendMessage("§6§l✨ COSMIC TELEPORTATION");
            player.sendMessage("§8§m+----------------------------+");
            player.sendMessage("§eStep 1/3: §7Look at a block and §aRIGHT-CLICK§7");
            player.sendMessage("§7Particles will show your selected spot");
            player.sendMessage("§8§m+----------------------------+");
            
            // Visual indicator for selection mode
            startSelectionParticles(player);
            
            // Auto-cancel after 60 seconds
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (selectionMode.getOrDefault(player.getUniqueId(), false)) {
                        selectionMode.remove(player.getUniqueId());
                        selectionStep.remove(player.getUniqueId());
                        selectedLocations.remove(player.getUniqueId());
                        player.sendMessage("§c❌ Teleportation selection expired!");
                    }
                }
            }.runTaskLater(plugin, 1200L); // 60 seconds
        } else {
            // Select current target block
            Block targetBlock = player.getTargetBlockExact(50);
            if (targetBlock == null || targetBlock.getType() == Material.AIR) {
                player.sendMessage("§c❌ Please look at a valid block!");
                return false;
            }
            
            int step = selectionStep.get(player.getUniqueId());
            List<Location> locations = selectedLocations.get(player.getUniqueId());
            
            Location selectedLoc = targetBlock.getLocation().add(0.5, 1, 0.5);
            locations.add(selectedLoc);
            
            // Visual feedback
            player.getWorld().spawnParticle(Particle.PORTAL, selectedLoc, 50, 0.5, 0.5, 0.5, 0.2);
            player.getWorld().spawnParticle(Particle.END_ROD, selectedLoc, 30, 0.3, 0.3, 0.3, 0.1);
            player.getWorld().playSound(selectedLoc, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f, 1.2f);
            
            // Create a temporary marker
            createLocationMarker(selectedLoc, step);
            
            if (step < 3) {
                // Next step
                selectionStep.put(player.getUniqueId(), step + 1);
                player.sendMessage("§a✅ Location " + step + " saved! (" + step + "/3)");
                player.sendMessage("§eStep " + (step + 1) + "/3: §7Select next location");
            } else {
                // All 3 locations selected - show menu
                selectionMode.remove(player.getUniqueId());
                showTeleportMenu(player, locations);
            }
        }
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    private void startSelectionParticles(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!selectionMode.getOrDefault(player.getUniqueId(), false)) {
                    cancel();
                    return;
                }
                
                // Spiral particles around player
                Location loc = player.getLocation().add(0, 1, 0);
                double time = System.currentTimeMillis() / 200.0;
                
                for (int i = 0; i < 3; i++) {
                    double angle = time + (i * 2 * Math.PI / 3);
                    double x = Math.sin(angle) * 2;
                    double z = Math.cos(angle) * 2;
                    double y = Math.sin(angle * 2) * 1;
                    
                    player.getWorld().spawnParticle(Particle.END_ROD, 
                        loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    private void createLocationMarker(Location loc, int step) {
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 100) { // 5 seconds
                    cancel();
                    return;
                }
                
                // Spiral particles
                double time = ticks / 10.0;
                double y = Math.sin(time) * 2 + 1;
                
                for (int i = 0; i < 4; i++) {
                    double angle = time + (i * Math.PI / 2);
                    double x = Math.sin(angle) * 1.5;
                    double z = Math.cos(angle) * 1.5;
                    
                    loc.getWorld().spawnParticle(Particle.END_ROD, 
                        loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
                }
                
                // Step number
                loc.getWorld().spawnParticle(Particle.FLAME, 
                    loc.clone().add(0, 2, 0), 5, 0.2, 0.2, 0.2, 0);
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void showTeleportMenu(Player player, List<Location> locations) {
        player.sendMessage(" ");
        player.sendMessage("§8§m✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧");
        player.sendMessage("§6§l✨ COSMIC TELEPORTATION ✨");
        player.sendMessage("§8§m✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧");
        player.sendMessage(" ");
        
        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i);
            String color = i == 0 ? "§e" : (i == 1 ? "§b" : "§d");
            player.sendMessage(color + "  ⭐ Location " + (i+1) + " §8- §fX: " + loc.getBlockX() + 
                " §8Y: " + loc.getBlockY() + " §8Z: " + loc.getBlockZ());
        }
        
        player.sendMessage(" ");
        player.sendMessage("§a✧ Type §e1, 2, or 3 §ato teleport!");
        player.sendMessage("§c✧ Type 'cancel' to cancel");
        player.sendMessage("§8§m✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧");
        
        // Store locations for chat response
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // This will be handled by ChatListener
        }, 1L);
    }
    
    public void handleChatResponse(Player player, String message) {
        List<Location> locations = selectedLocations.get(player.getUniqueId());
        if (locations == null || locations.isEmpty()) {
            player.sendMessage("§cNo locations selected!");
            return;
        }
        
        if (message.equalsIgnoreCase("cancel")) {
            selectedLocations.remove(player.getUniqueId());
            selectionStep.remove(player.getUniqueId());
            player.sendMessage("§c❌ Teleportation cancelled!");
            return;
        }
        
        try {
            int choice = Integer.parseInt(message);
            if (choice >= 1 && choice <= locations.size()) {
                Location targetLoc = locations.get(choice - 1);
                
                // Epic teleport effects
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.5f);
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 100, 1, 1, 1, 0.5);
                player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 1, 0, 0, 0, 0);
                
                player.teleport(targetLoc);
                
                player.getWorld().spawnParticle(Particle.PORTAL, targetLoc, 100, 1, 1, 1, 0.5);
                player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, targetLoc, 1, 0, 0, 0, 0);
                player.getWorld().playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                
                player.sendMessage("§a✅ §lTeleported to location " + choice + "!");
                
                // Clean up
                selectedLocations.remove(player.getUniqueId());
                selectionStep.remove(player.getUniqueId());
            } else {
                player.sendMessage("§c❌ Invalid choice! Please enter 1-" + locations.size());
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§c❌ Invalid input! Please enter a number or 'cancel'");
        }
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click - Pull target
        if (selectionMode.getOrDefault(player.getUniqueId(), false)) {
            // Cancel selection
            selectionMode.remove(player.getUniqueId());
            selectedLocations.remove(player.getUniqueId());
            selectionStep.remove(player.getUniqueId());
            player.sendMessage("§c❌ Teleportation cancelled!");
            return true;
        }
        
        final LivingEntity target = getTargetEntity(player, 40);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        Location start = player.getEyeLocation();
        Location end = target.getLocation().add(0, 1, 0);
        
        // Hook particles
        spawnLineParticles(start, end, Particle.END_ROD, 0.3);
        
        // Pull target
        Vector pull = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.5);
        target.setVelocity(pull);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1.0f, 0.5f);
        player.sendMessage("§3🪝 Target pulled!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Cosmic Fist";
    }
    
    @Override
    public String getDescription() {
        return "§3Select 3 locations and teleport between them";
    }
}
