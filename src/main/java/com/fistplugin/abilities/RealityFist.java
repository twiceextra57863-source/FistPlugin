package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RealityFist extends BaseAbility {
    
    public RealityFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click - Raise 5x5 area by 6 blocks
        Block targetBlock = player.getTargetBlock(null, 50);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            player.sendMessage("§cNo valid target block!");
            return false;
        }
        
        Location center = targetBlock.getLocation();
        Material blockType = targetBlock.getType();
        
        // Raise terrain
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
        
        // Damage entities
        for (Entity entity : player.getWorld().getNearbyEntities(center, 5, 5, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                ((LivingEntity) entity).damage(10.0, player);
            }
        }
        
        player.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
        player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, center.clone().add(0, 3, 0), 1);
        
        // Falling blocks particles
        for (int i = 0; i < 20; i++) {
            Location particleLoc = center.clone().add(
                (Math.random() - 0.5) * 8,
                Math.random() * 8,
                (Math.random() - 0.5) * 8
            );
            player.getWorld().spawnParticle(Particle.BLOCK, particleLoc, 5, 0.2, 0.2, 0.2, 0, 
                blockType.createBlockData());
        }
        
        player.sendMessage("§5🗻 Terrain raised!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click - Meteor shower
        Player target = getTargetPlayer(player, 50);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        Location targetLoc = target.getLocation();
        
        new BukkitRunnable() {
            int meteors = 0;
            
            @Override
            public void run() {
                if (meteors >= 4) {
                    cancel();
                    return;
                }
                
                double offsetX = (Math.random() - 0.5) * 6;
                double offsetZ = (Math.random() - 0.5) * 6;
                
                Location meteorLoc = targetLoc.clone().add(offsetX, 20, offsetZ);
                
                Material[] meteorMaterials = {
                    Material.NETHERRACK, Material.STONE, Material.OBSIDIAN,
                    Material.ANCIENT_DEBRIS, Material.NETHERITE_BLOCK
                };
                Material meteorType = meteorMaterials[(int)(Math.random() * meteorMaterials.length)];
                
                FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(
                    meteorLoc, meteorType.createBlockData());
                fallingBlock.setVelocity(new Vector(0, -1.2, 0));
                fallingBlock.setDropItem(false);
                fallingBlock.setHurtEntities(true);
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (fallingBlock.isDead() || fallingBlock.isOnGround()) {
                            cancel();
                            return;
                        }
                        
                        fallingBlock.getWorld().spawnParticle(Particle.FLAME, 
                            fallingBlock.getLocation(), 5, 0.5, 0.5, 0.5, 0.02);
                        fallingBlock.getWorld().spawnParticle(Particle.SMOKE, 
                            fallingBlock.getLocation(), 3, 0.3, 0.3, 0.3, 0.01);
                    }
                }.runTaskTimer(plugin, 0L, 1L);
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (fallingBlock.isDead() || fallingBlock.isOnGround()) {
                            fallingBlock.getWorld().createExplosion(
                                fallingBlock.getLocation(), 5.0f, false, true);
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
                
                meteors++;
            }
        }.runTaskTimer(plugin, 0L, 10L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);
        player.sendMessage("§5☄️ Meteor shower summoned!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Reality Fist";
    }
    
    @Override
    public String getDescription() {
        return "§5Manipulate the terrain itself";
    }
}
