package com.fistplugin.utils;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ParticleUtils {
    
    private static FistPlugin plugin;
    
    public static void init(FistPlugin instance) {
        plugin = instance;
    }
    
    /**
     * Spawn particles in a line between two locations
     */
    public static void spawnLine(Location start, Location end, Particle particle, double spacing) {
        World world = start.getWorld();
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        
        for (double d = 0; d < distance; d += spacing) {
            Location point = start.clone().add(direction.clone().multiply(d));
            world.spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    /**
     * Spawn particles in a circle
     */
    public static void spawnCircle(Location center, Particle particle, double radius, int points) {
        World world = center.getWorld();
        
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            Location point = center.clone().add(x, 0, z);
            world.spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    /**
     * Spawn particles in a sphere
     */
    public static void spawnSphere(Location center, Particle particle, double radius, int density) {
        World world = center.getWorld();
        
        for (int i = 0; i < density; i++) {
            double u = Math.random();
            double v = Math.random();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);
            
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);
            
            Location point = center.clone().add(x, y, z);
            world.spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    /**
     * Spawn particles in a helix
     */
    public static void spawnHelix(Location center, Particle particle, double radius, double height, int rotations) {
        World world = center.getWorld();
        
        for (double y = 0; y < height; y += 0.2) {
            double angle = (y / height) * 2 * Math.PI * rotations;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            Location point = center.clone().add(x, y, z);
            world.spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }
    
    /**
     * Spawn particles in a tornado effect
     */
    public static void spawnTornado(Location center, Particle particle, double height, double radius) {
        new BukkitRunnable() {
            double angle = 0;
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 100) {
                    cancel();
                    return;
                }
                
                for (double y = 0; y < height; y += 0.5) {
                    double r = radius * (1 - y / height);
                    angle += 0.3;
                    
                    double x = Math.cos(angle + y) * r;
                    double z = Math.sin(angle + y) * r;
                    
                    Location point = center.clone().add(x, y, z);
                    center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * Spawn particles forming a fist icon
     */
    public static void spawnFistIcon(Location center, FistType fist) {
        World world = center.getWorld();
        
        switch(fist) {
            case ORB:
                // Circle with inner orb
                for (int i = 0; i < 360; i += 15) {
                    double rad = Math.toRadians(i);
                    double x = Math.cos(rad) * 1.5;
                    double z = Math.sin(rad) * 1.5;
                    
                    Location outer = center.clone().add(x, 0, z);
                    world.spawnParticle(Particle.FLAME, outer, 1, 0, 0, 0, 0);
                }
                world.spawnParticle(Particle.SOUL_FIRE_FLAME, center, 10, 0.3, 0.3, 0.3, 0);
                break;
                
            case BLOSSOM:
                // Flower shape
                for (int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i);
                    double x = Math.cos(rad) * 1.2;
                    double z = Math.sin(rad) * 1.2;
                    
                    for (int p = 0; p < 5; p++) {
                        double offset = p * 0.2;
                        Location petal = center.clone().add(x + offset, 0, z + offset);
                        world.spawnParticle(Particle.HAPPY_VILLAGER, petal, 1, 0, 0, 0, 0);
                    }
                }
                break;
                
            case BEAST:
                // Paw print
                int[][] paw = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}, {0, 0}};
                for (int[] pos : paw) {
                    Location pawLoc = center.clone().add(pos[0] * 0.5, 0, pos[1] * 0.5);
                    world.spawnParticle(Particle.ANGRY_VILLAGER, pawLoc, 3, 0.2, 0.2, 0.2, 0);
                }
                break;
                
            case WATER:
                // Water drop
                for (double y = 0; y < 2; y += 0.2) {
                    Location drop = center.clone().add(0, y, 0);
                    world.spawnParticle(Particle.FALLING_WATER, drop, 1, 0.1, 0.1, 0.1, 0);
                }
                spawnCircle(center, Particle.FALLING_WATER, 1.0, 20);
                break;
                
            case REALITY:
                // Cube
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 3) continue;
                            Location cubeLoc = center.clone().add(x * 0.5, y * 0.5, z * 0.5);
                            world.spawnParticle(Particle.BLOCK_CRACK, cubeLoc, 1, 0.1, 0.1, 0.1, 0,
                                org.bukkit.Material.STONE.createBlockData());
                        }
                    }
                }
                break;
                
            case COSMIC:
                // Galaxy spiral
                for (int i = 0; i < 360; i += 10) {
                    double rad = Math.toRadians(i);
                    double r = 0.5 + Math.sin(rad * 3) * 0.3;
                    double x = Math.cos(rad) * r;
                    double z = Math.sin(rad) * r;
                    
                    Location spiral = center.clone().add(x, 0.5, z);
                    world.spawnParticle(Particle.PORTAL, spiral, 2, 0.1, 0.1, 0.1, 0.1);
                }
                break;
                
            case WOLF:
                // Wolf head
                world.spawnParticle(Particle.CRIT, center.clone().add(0, 0.5, 0), 5, 0.3, 0.3, 0.3, 0.1);
                world.spawnParticle(Particle.CRIT, center.clone().add(-0.4, 0.2, 0.4), 2, 0.1, 0.1, 0.1, 0);
                world.spawnParticle(Particle.CRIT, center.clone().add(0.4, 0.2, 0.4), 2, 0.1, 0.1, 0.1, 0);
                break;
                
            case BOMB:
                // Explosion
                for (int i = 0; i < 360; i += 20) {
                    double rad = Math.toRadians(i);
                    double x = Math.cos(rad) * 1.2;
                    double z = Math.sin(rad) * 1.2;
                    
                    Location spark = center.clone().add(x, 0.5, z);
                    world.spawnParticle(Particle.SMOKE_NORMAL, spark, 2, 0.1, 0.1, 0.1, 0.02);
                }
                world.spawnParticle(Particle.REDSTONE, center, 10, 0.3, 0.3, 0.3, 0,
                    new Particle.DustOptions(org.bukkit.Color.RED, 1));
                break;
                
            case VOID:
                // Black hole
                for (int i = 0; i < 360; i += 15) {
                    double rad = Math.toRadians(i);
                    double x = Math.cos(rad) * 1.2;
                    double z = Math.sin(rad) * 1.2;
                    
                    Location ring = center.clone().add(x, 0, z);
                    world.spawnParticle(Particle.DRAGON_BREATH, ring, 1, 0, 0, 0, 0);
                }
                spawnSphere(center, Particle.PORTAL, 0.8, 20);
                break;
                
            case PHANTOM:
                // Ghost
                for (double y = 0; y < 1.5; y += 0.3) {
                    Location body = center.clone().add(0, y, 0);
                    world.spawnParticle(Particle.SPELL_INSTANT, body, 3, 0.2, 0.1, 0.2, 0);
                }
                break;
        }
    }
    
    /**
     * Create a particle trail behind an entity
     */
    public static void createTrail(Location location, FistType fist) {
        Particle particle = getFistParticle(fist);
        location.getWorld().spawnParticle(particle, location, 3, 0.1, 0.1, 0.1, 0.02);
    }
    
    /**
     * Get particle type for fist
     */
    private static Particle getFistParticle(FistType fist) {
        switch(fist) {
            case ORB: return Particle.FLAME;
            case BLOSSOM: return Particle.HAPPY_VILLAGER;
            case BEAST: return Particle.ANGRY_VILLAGER;
            case WATER: return Particle.FALLING_WATER;
            case REALITY: return Particle.BLOCK_CRACK;
            case COSMIC: return Particle.PORTAL;
            case WOLF: return Particle.CRIT;
            case BOMB: return Particle.SMOKE_NORMAL;
            case VOID: return Particle.DRAGON_BREATH;
            case PHANTOM: return Particle.SPELL_INSTANT;
            default: return Particle.VILLAGER_HAPPY;
        }
    }
    
    /**
     * Create burst effect
     */
    public static void createBurst(Location center, Particle particle, int count, double radius) {
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double pitch = Math.random() * Math.PI - Math.PI / 2;
            
            double x = Math.cos(angle) * Math.cos(pitch) * radius;
            double y = Math.sin(pitch) * radius;
            double z = Math.sin(angle) * Math.cos(pitch) * radius;
            
            Location burstLoc = center.clone().add(x, y, z);
            center.getWorld().spawnParticle(particle, burstLoc, 2, 0.1, 0.1, 0.1, 0);
        }
    }
    
    /**
     * Create shockwave effect
     */
    public static void createShockwave(Location center, Particle particle, double maxRadius) {
        new BukkitRunnable() {
            double radius = 0;
            
            @Override
            public void run() {
                if (radius >= maxRadius) {
                    cancel();
                    return;
                }
                
                spawnCircle(center, particle, radius, (int)(radius * 20));
                radius += 0.3;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
                      }
