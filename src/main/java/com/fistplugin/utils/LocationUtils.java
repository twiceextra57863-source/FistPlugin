package com.fistplugin.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class LocationUtils {
    
    /**
     * Get the block the player is looking at within range
     */
    public static Block getTargetBlock(Player player, int range) {
        return player.getTargetBlock(null, range);
    }
    
    /**
     * Get all blocks in a line of sight
     */
    public static List<Block> getLineOfSightBlocks(Player player, int range) {
        List<Block> blocks = new ArrayList<>();
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize();
        
        for (int i = 0; i < range; i++) {
            loc.add(direction);
            Block block = loc.getBlock();
            if (block.getType() != Material.AIR) {
                blocks.add(block);
                if (block.getType().isSolid()) {
                    break;
                }
            }
        }
        
        return blocks;
    }
    
    /**
     * Get the entity the player is looking at
     */
    public static Entity getTargetEntity(Player player, int range) {
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        
        Entity target = null;
        double closest = range + 1;
        
        for (Entity entity : entities) {
            if (entity == player) continue;
            
            Vector toEntity = entity.getLocation().toVector().subtract(eyeLocation.toVector());
            double distance = eyeLocation.distance(entity.getLocation());
            
            if (distance > range) continue;
            
            // Check if entity is in line of sight
            Vector normalized = toEntity.clone().normalize();
            double dot = direction.dot(normalized);
            
            if (dot > 0.95 && distance < closest) {
                if (player.hasLineOfSight(entity)) {
                    closest = distance;
                    target = entity;
                }
            }
        }
        
        return target;
    }
    
    /**
     * Get a safe location (not in blocks)
     */
    public static Location getSafeLocation(Location loc) {
        World world = loc.getWorld();
        if (world == null) return loc;
        
        Block block = loc.getBlock();
        Block above = block.getRelative(BlockFace.UP);
        
        if (!block.getType().isSolid() && !above.getType().isSolid()) {
            return loc;
        }
        
        // Search for safe spot
        for (int y = 0; y < 10; y++) {
            Block check = world.getBlockAt(loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ());
            Block checkAbove = check.getRelative(BlockFace.UP);
            
            if (!check.getType().isSolid() && !checkAbove.getType().isSolid()) {
                return check.getLocation().add(0.5, 0, 0.5);
            }
        }
        
        return loc.getWorld().getHighestBlockAt(loc).getLocation().add(0.5, 1, 0.5);
    }
    
    /**
     * Get all players in a radius
     */
    public static List<Player> getNearbyPlayers(Location center, double radius) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        return players;
    }
    
    /**
     * Get all living entities in a radius excluding the source
     */
    public static List<LivingEntity> getNearbyLivingEntities(Location center, double radius, Entity exclude) {
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != exclude) {
                entities.add((LivingEntity) entity);
            }
        }
        return entities;
    }
    
    /**
     * Check if location is safe (not in blocks or lava)
     */
    public static boolean isSafeLocation(Location loc) {
        Block block = loc.getBlock();
        Block below = block.getRelative(BlockFace.DOWN);
        Block above = block.getRelative(BlockFace.UP);
        
        return !block.getType().isSolid() && 
               !above.getType().isSolid() && 
               below.getType().isSolid() &&
               below.getType() != Material.LAVA &&
               below.getType() != Material.FIRE;
    }
    
    /**
     * Get the direction vector from one location to another
     */
    public static Vector getDirection(Location from, Location to) {
        return to.toVector().subtract(from.toVector()).normalize();
    }
    
    /**
     * Calculate the distance between two locations ignoring Y axis
     */
    public static double distance2D(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    /**
     * Get a random location in a circle
     */
    public static Location getRandomCircleLocation(Location center, double radius) {
        double angle = Math.random() * 2 * Math.PI;
        double x = center.getX() + Math.cos(angle) * radius;
        double z = center.getZ() + Math.sin(angle) * radius;
        double y = center.getY();
        
        return new Location(center.getWorld(), x, y, z);
    }
    
    /**
     * Get a random location in a sphere
     */
    public static Location getRandomSphereLocation(Location center, double radius) {
        double u = Math.random();
        double v = Math.random();
        double theta = 2 * Math.PI * u;
        double phi = Math.acos(2 * v - 1);
        
        double x = center.getX() + radius * Math.sin(phi) * Math.cos(theta);
        double y = center.getY() + radius * Math.sin(phi) * Math.sin(theta);
        double z = center.getZ() + radius * Math.cos(phi);
        
        return new Location(center.getWorld(), x, y, z);
    }
    
    /**
     * Create a circle of blocks (for arena)
     */
    public static List<Location> getCircleLocations(Location center, double radius, int points) {
        List<Location> locations = new ArrayList<>();
        
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            double y = center.getY();
            
            locations.add(new Location(center.getWorld(), x, y, z));
        }
        
        return locations;
    }
    
    /**
     * Create a line of locations
     */
    public static List<Location> getLineLocations(Location start, Location end, double spacing) {
        List<Location> locations = new ArrayList<>();
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        
        for (double d = 0; d < distance; d += spacing) {
            locations.add(start.clone().add(direction.clone().multiply(d)));
        }
        
        return locations;
    }
    
    /**
     * Check if a location is within a cuboid region
     */
    public static boolean isInCuboid(Location loc, Location pos1, Location pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        return loc.getX() >= minX && loc.getX() <= maxX &&
               loc.getY() >= minY && loc.getY() <= maxY &&
               loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
    
    /**
     * Get the center of a block
     */
    public static Location getBlockCenter(Block block) {
        return block.getLocation().add(0.5, 0.5, 0.5);
    }
    
    /**
     * Get the highest block at a location
     */
    public static Location getHighestBlockAt(Location loc) {
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int y = world.getHighestBlockYAt(x, z);
        
        return new Location(world, x + 0.5, y + 1, z + 0.5);
    }
                                                }
