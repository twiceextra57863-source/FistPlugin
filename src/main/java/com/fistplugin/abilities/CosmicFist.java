package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmicFist extends BaseAbility {
    
    private final Map<UUID, UUID> hookedTargets = new HashMap<>();
    
    public CosmicFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        LivingEntity target = getTargetEntity(player, 30);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        // Spin effect
        new BukkitRunnable() {
            int rotations = 0;
            Location originalLoc = target.getLocation().clone();
            
            @Override
            public void run() {
                if (rotations >= 40 || target.isDead()) { // 2 seconds
                    cancel();
                    return;
                }
                
                // Spin target
                Location loc = target.getLocation();
                loc.setYaw(loc.getYaw() + 18);
                target.teleport(loc);
                
                // Cosmic particles
                for (int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i + rotations * 10);
                    double x = Math.sin(rad) * 1.5;
                    double z = Math.cos(rad) * 1.5;
                    
                    Location particleLoc = target.getLocation().clone().add(x, 1, z);
                    target.getWorld().spawnParticle(Particle.PORTAL, particleLoc, 3, 0.1, 0.1, 0.1, 0.1);
                }
                
                // Pull towards center slightly
                if (target.getLocation().distance(originalLoc) > 0.5) {
                    Vector pull = originalLoc.toVector().subtract(target.getLocation().toVector()).normalize().multiply(0.1);
                    target.setVelocity(pull);
                }
                
                rotations++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        LivingEntity target = getTargetEntity(player, 40);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        // Hook effect
        Location start = player.getEyeLocation();
        Location end = target.getLocation().add(0, 1, 0);
        
        // Particle line
        spawnLineParticles(start, end, Particle.END_ROD, 0.3);
        
        // Store hooked target
        hookedTargets.put(player.getUniqueId(), target.getUniqueId());
        
        // Hook sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1.0f, 0.5f);
        
        player.sendMessage("§3🪝 Target hooked! Left click to launch!");
        
        // Remove after 5 seconds if not used
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            hookedTargets.remove(player.getUniqueId());
        }, 100L); // 5 seconds
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    public void onLeftClick(Player player) {
        UUID targetId = hookedTargets.get(player.getUniqueId());
        if (targetId == null) return;
        
        Entity target = plugin.getServer().getEntity(targetId);
        if (target instanceof LivingEntity && !target.isDead()) {
            // Launch target
            Vector direction = player.getLocation().getDirection().multiply(3);
            target.setVelocity(direction);
            
            // Damage
            ((LivingEntity) target).damage(5.0, player);
            
            // Effects
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
            target.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, target.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
            
            hookedTargets.remove(player.getUniqueId());
        }
    }
    
    @Override
    public String getName() {
        return "Cosmic Fist";
    }
    
    @Override
    public String getDescription() {
        return "Control the gravity of your enemies";
    }
}
