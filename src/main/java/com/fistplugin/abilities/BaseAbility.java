package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

public abstract class BaseAbility implements Ability {
    
    protected final FistPlugin plugin;
    
    public BaseAbility(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    // IMPROVED: Better target detection with ray tracing
    protected Player getTargetPlayer(Player player, double range) {
        // Try ray tracing first for precise targeting
        RayTraceResult result = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getLocation().getDirection(),
            range,
            1.0,
            e -> e instanceof Player && e != player
        );
        
        if (result != null && result.getHitEntity() instanceof Player) {
            return (Player) result.getHitEntity();
        }
        
        // Fallback to nearest entity if ray trace fails
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        Player closest = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (entity instanceof Player && entity != player) {
                // Check if player has line of sight
                if (!player.hasLineOfSight(entity)) continue;
                
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closest = (Player) entity;
                }
            }
        }
        
        return closest;
    }
    
    // IMPROVED: Better entity detection with ray tracing
    protected LivingEntity getTargetEntity(Player player, double range) {
        // Try ray tracing first
        RayTraceResult result = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getLocation().getDirection(),
            range,
            1.0,
            e -> e instanceof LivingEntity && e != player && !(e instanceof Player)
        );
        
        if (result != null && result.getHitEntity() instanceof LivingEntity) {
            return (LivingEntity) result.getHitEntity();
        }
        
        // Also check for players
        result = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getLocation().getDirection(),
            range,
            1.0,
            e -> e instanceof Player && e != player
        );
        
        if (result != null && result.getHitEntity() instanceof Player) {
            return (Player) result.getHitEntity();
        }
        
        return null;
    }
    
    // Get entities in a cone (for area effects)
    protected List<Entity> getEntitiesInCone(Player player, double range, double angle) {
        Vector playerDir = player.getLocation().getDirection().normalize();
        double cosAngle = Math.cos(Math.toRadians(angle / 2));
        
        return player.getNearbyEntities(range, range, range).stream()
            .filter(e -> e instanceof LivingEntity && e != player)
            .filter(e -> {
                Vector toEntity = e.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
                return playerDir.dot(toEntity) > cosAngle;
            })
            .toList();
    }
    
    protected void spawnLineParticles(Location start, Location end, Particle particle, double spacing) {
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        
        for (double d = 0; d < distance; d += spacing) {
            Location point = start.clone().add(direction.clone().multiply(d));
            start.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    protected void playSound(Location loc, Sound sound, float volume, float pitch) {
        if (loc.getWorld() != null) {
            loc.getWorld().playSound(loc, sound, volume, pitch);
        }
    }
}
