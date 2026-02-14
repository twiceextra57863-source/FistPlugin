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
import java.util.List;

public abstract class BaseAbility implements Ability {
    
    protected final FistPlugin plugin;
    
    public BaseAbility(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    protected Player getTargetPlayer(Player player, double range) {
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        Player closest = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (entity instanceof Player && entity != player) {
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance < closestDistance && player.hasLineOfSight(entity)) {
                    closestDistance = distance;
                    closest = (Player) entity;
                }
            }
        }
        
        return closest;
    }
    
    protected LivingEntity getTargetEntity(Player player, double range) {
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        LivingEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && entity != player) {
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance < closestDistance && player.hasLineOfSight(entity)) {
                    closestDistance = distance;
                    closest = (LivingEntity) entity;
                }
            }
        }
        
        return closest;
    }
    
    protected void spawnLineParticles(Location start, Location end, Particle particle, double spacing) {
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        
        for (double d = 0; d < distance; d += spacing) {
            Location point = start.clone().add(direction.clone().multiply(d));
            start.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    protected void spawnCircleParticles(Location center, Particle particle, double radius, int points) {
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Location point = center.clone().add(x, 0, z);
            center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    protected void playSound(Location loc, Sound sound, float volume, float pitch) {
        loc.getWorld().playSound(loc, sound, volume, pitch);
    }
          }
