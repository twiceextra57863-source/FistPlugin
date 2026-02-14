package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class WolfFist extends BaseAbility {
    
    public WolfFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        Vector direction = player.getLocation().getDirection();
        player.setVelocity(direction.multiply(1.5));
        
        // Slash effect
        for (int i = 0; i < 360; i += 30) {
            double rad = Math.toRadians(i);
            double x = Math.sin(rad) * 2;
            double z = Math.cos(rad) * 2;
            
            Location slashLoc = player.getLocation().clone().add(x, 1, z);
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, slashLoc, 1, 0, 0, 0, 0);
        }
        
        // Find and freeze target
        LivingEntity target = getTargetEntity(player, 10);
        if (target != null) {
            target.setFreezeTicks(40); // 2 seconds freeze
            target.setVelocity(direction.multiply(0.5));
            target.damage(6.0, player);
            
            // Wolf attack particles
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.2);
        }
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 1.0f, 1.0f);
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        List<ArmorStand> clones = new ArrayList<>();
        
        // Spawn 3 clones
        for (int i = 0; i < 3; i++) {
            double angle = (2 * Math.PI / 3) * i;
            double x = Math.sin(angle) * 2;
            double z = Math.cos(angle) * 2;
            
            Location cloneLoc = player.getLocation().clone().add(x, 0, z);
            ArmorStand clone = player.getWorld().spawn(cloneLoc, ArmorStand.class);
            clone.setVisible(false);
            clone.setGravity(false);
            clone.setSmall(false);
            clone.setBasePlate(false);
            clone.setArms(true);
            clone.setItemInHand(player.getInventory().getItemInMainHand());
            clone.setCustomName("Wolf Clone");
            clone.setCustomNameVisible(false);
            
            clones.add(clone);
            
            // Spawn particle effect
            clone.getWorld().spawnParticle(Particle.CLOUD, cloneLoc, 20, 0.5, 0.5, 0.5, 0);
        }
        
        // Clone AI
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 200 || clones.stream().allMatch(Entity::isDead)) { // 10 seconds
                    clones.forEach(Entity::remove);
                    cancel();
                    return;
                }
                
                for (ArmorStand clone : clones) {
                    if (clone.isDead()) continue;
                    
                    // Find nearest entity to attack
                    for (Entity entity : clone.getNearbyEntities(8, 8, 8)) {
                        if (entity instanceof LivingEntity && entity != player && !(entity instanceof ArmorStand)) {
                            // Move towards target
                            Vector direction = entity.getLocation().toVector()
                                .subtract(clone.getLocation().toVector()).normalize().multiply(0.3);
                            clone.setVelocity(direction);
                            
                            // Attack if close
                            if (clone.getLocation().distance(entity.getLocation()) < 2) {
                                ((LivingEntity) entity).damage(4.0, player);
                                
                                // Attack effect
                                entity.getWorld().spawnParticle(Particle.CRIT, 
                                    entity.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.1);
                                
                                // Jump back
                                clone.setVelocity(clone.getLocation().getDirection().multiply(-0.5));
                            }
                            
                            break;
                        }
                    }
                    
                    // Follow player if no target
                    if (clone.getNearbyEntities(8, 8, 8).stream()
                        .noneMatch(e -> e instanceof LivingEntity && e != player && !(e instanceof ArmorStand))) {
                        Vector toPlayer = player.getLocation().toVector()
                            .subtract(clone.getLocation().toVector()).normalize().multiply(0.2);
                        clone.setVelocity(toPlayer);
                    }
                    
                    // Particles
                    clone.getWorld().spawnParticle(Particle.CLOUD, clone.getLocation().add(0, 1, 0), 1, 0.1, 0.1, 0.1, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1.0f, 1.0f);
        player.sendMessage("§7🐕‍🦺 Wolf clones summoned!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Wolf Fist";
    }
    
    @Override
    public String getDescription() {
        return "Hunt with your pack";
    }
}
