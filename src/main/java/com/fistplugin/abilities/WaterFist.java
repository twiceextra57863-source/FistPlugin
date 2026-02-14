package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WaterFist extends BaseAbility {
    
    public WaterFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);
        
        // Water jet particles
        new BukkitRunnable() {
            int seconds = 0;
            
            @Override
            public void run() {
                if (seconds >= 6) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    cancel();
                    return;
                }
                
                Location loc = player.getLocation();
                
                // Water column under player
                for (double y = 0; y < 3; y += 0.5) {
                    Location waterLoc = loc.clone().add(0, -y, 0);
                    player.getWorld().spawnParticle(Particle.FALLING_WATER, waterLoc, 5, 0.3, 0.1, 0.3, 0);
                }
                
                // Spiral around player
                for (int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i + seconds * 20);
                    double x = Math.sin(rad) * 1.5;
                    double z = Math.cos(rad) * 1.5;
                    
                    Location spiral = loc.clone().add(x, 1, z);
                    player.getWorld().spawnParticle(Particle.FALLING_WATER, spiral, 2, 0.1, 0.1, 0.1, 0);
                }
                
                seconds++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        player.sendMessage("§b💧 You can now fly for 6 seconds!");
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        Location start = player.getLocation();
        Vector direction = player.getLocation().getDirection().normalize().multiply(0.5);
        direction.setY(0.2);
        
        new BukkitRunnable() {
            int ticks = 0;
            Location current = start.clone();
            
            @Override
            public void run() {
                if (ticks >= 60) { // 6 seconds
                    cancel();
                    return;
                }
                
                // Create tsunami wall
                for (int i = -4; i <= 4; i++) {
                    for (int h = 0; h < 6; h++) {
                        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize().multiply(i);
                        Location waterLoc = current.clone().add(perpendicular).add(0, h, 0);
                        
                        // Water particles
                        player.getWorld().spawnParticle(Particle.FALLING_WATER, waterLoc, 10, 0.5, 0.5, 0.5, 0);
                        
                        // Push entities
                        for (Entity entity : player.getWorld().getNearbyEntities(waterLoc, 2, 2, 2)) {
                            if (entity instanceof LivingEntity && entity != player) {
                                Vector push = direction.clone().multiply(3).setY(1.2);
                                entity.setVelocity(push);
                            }
                        }
                        
                        // Temporary water blocks (visual only)
                        if (h == 0 && waterLoc.getBlock().getType() == Material.AIR) {
                            player.sendBlockChange(waterLoc, Material.WATER.createBlockData());
                        }
                    }
                }
                
                current.add(direction);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
        
        // Restore blocks after tsunami
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Location current = start.clone();
            for (int i = 0; i < 60; i++) {
                for (int x = -4; x <= 4; x++) {
                    for (int h = 0; h < 6; h++) {
                        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize().multiply(x);
                        Location waterLoc = current.clone().add(perpendicular).add(0, h, 0);
                        
                        if (waterLoc.getBlock().getType() == Material.AIR) {
                            player.sendBlockChange(waterLoc, Material.AIR.createBlockData());
                        }
                    }
                }
                current.add(direction);
            }
        }, 120L); // 6 seconds later
        
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUCKET_EMPTY, 2.0f, 0.5f);
        player.sendMessage("§b🌊 Tsunami summoned!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Water Fist";
    }
    
    @Override
    public String getDescription() {
        return "Ride the waves and summon tsunamis";
    }
}
