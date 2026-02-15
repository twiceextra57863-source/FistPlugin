package com.fistplugin.utils;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleUtils {
    
    private final FistPlugin plugin;
    
    public ParticleUtils(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void spawnLineParticles(Location start, Location end, Particle particle, double spacing) {
        org.bukkit.util.Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        
        for (double d = 0; d < distance; d += spacing) {
            Location point = start.clone().add(direction.clone().multiply(d));
            start.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    public void spawnCircleParticles(Location center, Particle particle, double radius, int points) {
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Location point = center.clone().add(x, 0, z);
            center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    public void spawnSpiralParticles(Location center, Particle particle, double radius, double height, int rotations) {
        int points = 100;
        for (int i = 0; i < points; i++) {
            double progress = (double) i / points;
            double angle = progress * 2 * Math.PI * rotations;
            double r = radius * progress;
            double x = Math.cos(angle) * r;
            double z = Math.sin(angle) * r;
            double y = height * progress;
            
            Location point = center.clone().add(x, y, z);
            center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    public void spawnSphereParticles(Location center, Particle particle, double radius, int density) {
        for (int i = 0; i < density; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(2 * Math.random() - 1);
            
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);
            
            Location point = center.clone().add(x, y, z);
            center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    public void spawnDustParticles(Location center, Color color, int count, double size) {
        for (int i = 0; i < count; i++) {
            center.getWorld().spawnParticle(Particle.DUST, center, 1, 0.3, 0.3, 0.3, 0, 
                new Particle.DustOptions(color, (float)size));
        }
    }
    
    public Particle getFistParticle(FistType fist) {
        switch(fist) {
            case ORB: return Particle.FLAME;
            case BLOSSOM: return Particle.HAPPY_VILLAGER;
            case BEAST: return Particle.ANGRY_VILLAGER;
            case WATER: return Particle.FALLING_WATER;
            case REALITY: return Particle.BLOCK;
            case COSMIC: return Particle.PORTAL;
            case WOLF: return Particle.CRIT;
            case BOMB: return Particle.SMOKE;
            case VOID: return Particle.DRAGON_BREATH;
            case PHANTOM: return Particle.INSTANT_EFFECT;
            default: return Particle.HAPPY_VILLAGER;
        }
    }
    
    public Color getFistColor(FistType fist) {
        switch(fist) {
            case ORB: return Color.ORANGE;
            case BLOSSOM: return Color.FUCHSIA;
            case BEAST: return Color.RED;
            case WATER: return Color.BLUE;
            case REALITY: return Color.PURPLE;
            case COSMIC: return Color.TEAL;
            case WOLF: return Color.SILVER;
            case BOMB: return Color.MAROON;
            case VOID: return Color.BLACK;
            case PHANTOM: return Color.WHITE;
            default: return Color.WHITE;
        }
    }
    
    // Fixed: All particle names updated to 1.21
    public void spawnFistParticles(Player player, FistType fist) {
        Location loc = player.getLocation().add(0, 1, 0);
        
        switch(fist) {
            case ORB:
                new BukkitRunnable() {
                    double angle = 0;
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        angle += 0.2;
                        double x = Math.sin(angle) * 1.5;
                        double z = Math.cos(angle) * 1.5;
                        player.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(x, 1, z), 1, 0, 0, 0, 0);
                    }
                }.runTaskTimer(plugin, 0L, 1L);
                break;
                
            case BLOSSOM:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 3, 0.5, 0.5, 0.5, 0);
                    }
                }.runTaskTimer(plugin, 0L, 5L);
                break;
                
            case BEAST:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, loc, 2, 0.5, 0.5, 0.5, 0);
                    }
                }.runTaskTimer(plugin, 0L, 10L);
                break;
                
            case WATER:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        player.getWorld().spawnParticle(Particle.FALLING_WATER, loc, 3, 0.5, 0.5, 0.5, 0);
                    }
                }.runTaskTimer(plugin, 0L, 5L);
                break;
                
            case REALITY:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        player.getWorld().spawnParticle(Particle.BLOCK, loc, 3, 0.5, 0.5, 0.5, 0, 
                            org.bukkit.Material.STONE.createBlockData());
                    }
                }.runTaskTimer(plugin, 0L, 8L);
                break;
                
            case COSMIC:
                new BukkitRunnable() {
                    double angle = 0;
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        angle += 0.1;
                        double x = Math.sin(angle) * 1.5;
                        double z = Math.cos(angle) * 1.5;
                        player.getWorld().spawnParticle(Particle.PORTAL, loc.clone().add(x, 1, z), 1, 0, 0, 0, 0);
                    }
                }.runTaskTimer(plugin, 0L, 2L);
                break;
                
            case WOLF:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        player.getWorld().spawnParticle(Particle.CRIT, loc, 2, 0.5, 0.5, 0.5, 0.1);
                    }
                }.runTaskTimer(plugin, 0L, 5L);
                break;
                
            case BOMB:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        player.getWorld().spawnParticle(Particle.SMOKE, loc, 3, 0.3, 0.3, 0.3, 0.02);
                    }
                }.runTaskTimer(plugin, 0L, 4L);
                break;
                
            case VOID:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 2, 0.4, 0.4, 0.4, 0);
                    }
                }.runTaskTimer(plugin, 0L, 6L);
                break;
                
            case PHANTOM:
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { cancel(); return; }
                        player.getWorld().spawnParticle(Particle.INSTANT_EFFECT, loc, 3, 0.5, 0.5, 0.5, 0);
                    }
                }.runTaskTimer(plugin, 0L, 3L);
                break;
        }
    }
}
