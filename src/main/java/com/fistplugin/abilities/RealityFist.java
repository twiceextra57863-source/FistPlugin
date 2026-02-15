package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RealityFist extends BaseAbility {
    
    private final Random random = new Random();
    
    public RealityFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click - Raise terrain and create destruction
        Block targetBlock = player.getTargetBlockExact(50);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            // If no target block, use location in front of player
            targetBlock = player.getLocation().add(player.getLocation().getDirection().multiply(5)).getBlock();
        }
        
        final Location center = targetBlock.getLocation();
        final Material blockType = targetBlock.getType();
        final World world = player.getWorld();
        
        // Get all blocks in 5x5 area
        List<Location> blocksToRaise = new ArrayList<>();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y <= 5; y++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    if (!blockLoc.getBlock().getType().isAir()) {
                        blocksToRaise.add(blockLoc);
                    }
                }
            }
        }
        
        // Raise blocks with animation
        new BukkitRunnable() {
            int step = 0;
            final int totalSteps = 20;
            final List<FallingBlock> fallingBlocks = new ArrayList<>();
            
            @Override
            public void run() {
                if (step >= totalSteps) {
                    // Create explosion effect
                    world.createExplosion(center.clone().add(0, 3, 0), 3.0f, false, true);
                    
                    // Damage all entities in area
                    for (Entity entity : world.getNearbyEntities(center, 8, 8, 8)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            double distance = entity.getLocation().distance(center);
                            double damage = 15.0 * (1 - (distance / 8));
                            ((LivingEntity) entity).damage(Math.max(5, damage), player);
                            
                            // Knockback
                            Vector knockback = entity.getLocation().toVector()
                                .subtract(center.toVector()).normalize().multiply(2);
                            entity.setVelocity(knockback);
                        }
                    }
                    
                    cancel();
                    return;
                }
                
                // Spawn epic particles
                for (int i = 0; i < 10; i++) {
                    double offsetX = (random.nextDouble() - 0.5) * 10;
                    double offsetY = random.nextDouble() * 8;
                    double offsetZ = (random.nextDouble() - 0.5) * 10;
                    
                    Location particleLoc = center.clone().add(offsetX, offsetY, offsetZ);
                    
                    // Reality particles - floating blocks and dust
                    world.spawnParticle(Particle.BLOCK, particleLoc, 2, 0.3, 0.3, 0.3, 0,
                        Material.STONE.createBlockData());
                    
                    world.spawnParticle(Particle.BLOCK, particleLoc, 2, 0.3, 0.3, 0.3, 0,
                        Material.DIRT.createBlockData());
                }
                
                // Ground shaking effect
                for (Player p : world.getPlayers()) {
                    if (p.getLocation().distance(center) < 15) {
                        p.playSound(p.getLocation(), Sound.BLOCK_STONE_BREAK, 0.5f, 0.5f);
                    }
                }
                
                step++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
        
        // Actually raise the terrain
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 1; y <= 6; y++) {
                    Location blockLoc = center.clone().add(x, y, z);
                    Location sourceLoc = center.clone().add(x, 0, z);
                    
                    if (!sourceLoc.getBlock().getType().isAir()) {
                        blockLoc.getBlock().setType(sourceLoc.getBlock().getType());
                    }
                }
            }
        }
        
        // Remove original blocks
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                center.clone().add(x, 0, z).getBlock().setType(Material.AIR);
            }
        }
        
        player.sendMessage("§5🗻 TERRAIN RAISED! §7(5x5 area, 6 blocks high)");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click - EPIC METEOR SHOWER
        Player target = getTargetPlayer(player, 60); // Increased range
        if (target == null) {
            player.sendMessage("§cNo target found! Look at a player.");
            return false;
        }
        
        final Location targetLoc = target.getLocation();
        final World world = player.getWorld();
        
        player.sendMessage("§5☄️ SUMMONING METEOR SHOWER on " + target.getName() + "!");
        target.sendMessage("§c§lWARNING! " + player.getName() + " summoned meteors on you!");
        
        // Epic meteor shower
        new BukkitRunnable() {
            int meteors = 0;
            final int totalMeteors = 8;
            
            @Override
            public void run() {
                if (meteors >= totalMeteors) {
                    // Final explosion
                    world.createExplosion(targetLoc, 6.0f, false, true);
                    player.sendMessage("§5☄️ Meteor shower complete!");
                    cancel();
                    return;
                }
                
                // Random offset for each meteor
                double offsetX = (random.nextDouble() - 0.5) * 12;
                double offsetZ = (random.nextDouble() - 0.5) * 12;
                
                Location meteorLoc = targetLoc.clone().add(offsetX, 25, offsetZ);
                
                // Choose meteor type
                Material[] meteorMaterials = {
                    Material.NETHERRACK, Material.STONE, Material.OBSIDIAN,
                    Material.ANCIENT_DEBRIS, Material.NETHERITE_BLOCK,
                    Material.MAGMA_BLOCK, Material.BASALT
                };
                Material meteorType = meteorMaterials[random.nextInt(meteorMaterials.length)];
                BlockData blockData = meteorType.createBlockData();
                
                // Create meteor
                FallingBlock meteor = world.spawnFallingBlock(meteorLoc, blockData);
                meteor.setVelocity(new Vector(0, -1.5, 0));
                meteor.setDropItem(false);
                meteor.setHurtEntities(true);
                meteor.setCustomName("§cMETEOR");
                
                // Meteor trail
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (meteor.isDead() || meteor.isOnGround()) {
                            // Impact explosion
                            world.createExplosion(meteor.getLocation(), 5.0f, false, true);
                            
                            // Impact particles
                            world.spawnParticle(Particle.EXPLOSION_EMITTER, meteor.getLocation(), 1);
                            world.spawnParticle(Particle.BLOCK, meteor.getLocation(), 50, 2, 2, 2, 0,
                                meteorType.createBlockData());
                            
                            cancel();
                            return;
                        }
                        
                        // Trail particles
                        Location loc = meteor.getLocation();
                        world.spawnParticle(Particle.FLAME, loc, 10, 0.5, 0.5, 0.5, 0.05);
                        world.spawnParticle(Particle.SMOKE, loc, 5, 0.3, 0.3, 0.3, 0.02);
                        world.spawnParticle(Particle.BLOCK, loc, 3, 0.2, 0.2, 0.2, 0,
                            meteorType.createBlockData());
                    }
                }.runTaskTimer(plugin, 0L, 1L);
                
                meteors++;
            }
        }.runTaskTimer(plugin, 0L, 10L); // Spawn meteor every 0.5 seconds
        
        // Cinematic camera shake for nearby players
        for (Player p : world.getPlayers()) {
            if (p.getLocation().distance(targetLoc) < 20) {
                // Screen shake effect (via teleport)
                Location shakeLoc = p.getLocation().clone();
                shakeLoc.add(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5);
                p.teleport(shakeLoc);
            }
        }
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Reality Fist";
    }
    
    @Override
    public String getDescription() {
        return "§5Manipulate reality - raise terrain and summon meteors";
    }
                    }
