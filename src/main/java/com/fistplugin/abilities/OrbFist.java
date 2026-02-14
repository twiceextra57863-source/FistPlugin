package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrbFist extends BaseAbility {
    
    private final Map<UUID, Location> boxingArenas = new HashMap<>();
    
    public OrbFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Launch fireball
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setVelocity(player.getLocation().getDirection().multiply(2.0));
        fireball.setYield(3.0f);
        fireball.setIsIncendiary(false);
        
        // Track projectile
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fireball.isDead() || !fireball.isValid()) {
                    cancel();
                    return;
                }
                
                // Particle trail
                Location loc = fireball.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 3, 0.1, 0.1, 0.1, 0.02);
                loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 2, 0.1, 0.1, 0.1, 0.01);
                
                // Check for nearby entities to trigger explosion early
                loc.getWorld().getNearbyEntities(loc, 2, 2, 2).stream()
                    .filter(e -> e instanceof LivingEntity && e != player)
                    .findFirst()
                    .ifPresent(e -> {
                        fireball.remove();
                        loc.getWorld().createExplosion(loc, 3.0f, false, true);
                    });
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        // Stats tracking
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        Player target = getTargetPlayer(player, 50);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        Location center = player.getTargetBlock(null, 50).getLocation().add(0, 2, 0);
        Location playerLoc = player.getLocation();
        Location targetLoc = target.getLocation();
        
        // Teleport to arena
        player.teleport(center.clone().add(4, 0, 0));
        target.teleport(center.clone().add(-4, 0, 0));
        
        // Apply effects
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 300, 0));
        
        // Store arena location
        boxingArenas.put(player.getUniqueId(), center);
        boxingArenas.put(target.getUniqueId(), center);
        
        // Create arena barrier
        new BukkitRunnable() {
            int timeLeft = 15;
            
            @Override
            public void run() {
                if (timeLeft <= 0) {
                    // Remove arena
                    boxingArenas.remove(player.getUniqueId());
                    boxingArenas.remove(target.getUniqueId());
                    
                    // Teleport back
                    if (player.isOnline() && !player.isDead()) {
                        player.teleport(playerLoc);
                    }
                    if (target.isOnline() && !target.isDead()) {
                        target.teleport(targetLoc);
                    }
                    
                    player.sendMessage("§6Boxing arena disappeared!");
                    if (target.isOnline()) {
                        target.sendMessage("§6Boxing arena disappeared!");
                    }
                    
                    cancel();
                    return;
                }
                
                // Spawn barrier particles
                for (int i = 0; i < 360; i += 15) {
                    double rad = Math.toRadians(i);
                    double x = Math.sin(rad) * 6;
                    double z = Math.cos(rad) * 6;
                    
                    for (double y = 0; y < 5; y += 0.5) {
                        Location particleLoc = center.clone().add(x, y, z);
                        center.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                    }
                }
                
                // Check if players try to escape
                if (player.getLocation().distance(center) > 6) {
                    player.teleport(center.clone().add(3, 0, 0));
                }
                if (target.getLocation().distance(center) > 6) {
                    target.teleport(center.clone().add(-3, 0, 0));
                }
                
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        // Cinematic particles
        new BukkitRunnable() {
            double angle = 0;
            
            @Override
            public void run() {
                if (!boxingArenas.containsKey(player.getUniqueId())) {
                    cancel();
                    return;
                }
                
                angle += 0.2;
                double y = Math.sin(angle) * 2 + 2;
                
                for (int i = 0; i < 4; i++) {
                    double a = angle + (i * Math.PI / 2);
                    double x = Math.sin(a) * 5;
                    double z = Math.cos(a) * 5;
                    
                    Location particleLoc = center.clone().add(x, y, z);
                    center.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 2, 0.1, 0.1, 0.1, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        // Stats tracking
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        
        return true;
    }
    
    private void spawnOrbParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 20) {
                    cancel();
                    return;
                }
                
                // Orb particles around hand
                for (int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i + ticks * 10);
                    double x = Math.sin(rad) * 1.5;
                    double z = Math.cos(rad) * 1.5;
                    
                    Location handLoc = loc.clone().add(x, 0.5, z);
                    player.getWorld().spawnParticle(Particle.FLAME, handLoc, 1, 0, 0, 0, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    @Override
    public String getName() {
        return "Orb Fist";
    }
    
    @Override
    public String getDescription() {
        return "Master of explosive orbs and boxing arenas";
    }
}
