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

public class VoidFist extends BaseAbility {
    
    public VoidFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click - Pull target
        final LivingEntity target = getTargetEntity(player, 40);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        Location playerLoc = player.getLocation();
        Location targetLoc = target.getLocation();
        
        Vector pull = playerLoc.toVector().subtract(targetLoc.toVector()).normalize().multiply(2);
        target.setVelocity(pull);
        
        spawnLineParticles(targetLoc.add(0, 1, 0), playerLoc.add(0, 1, 0), Particle.DRAGON_BREATH, 0.2);
        
        target.damage(4.0, player);
        
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 20 || target.isDead()) {
                    cancel();
                    return;
                }
                
                target.getWorld().spawnParticle(Particle.DRAGON_BREATH, 
                    target.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.01);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        player.sendMessage("§8🌑 Target pulled!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click - Void nova
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);
        
        new BukkitRunnable() {
            double radius = 0;
            
            @Override
            public void run() {
                if (radius >= 10) {
                    cancel();
                    return;
                }
                
                for (int i = 0; i < 360; i += 10) {
                    double rad = Math.toRadians(i);
                    double x = Math.sin(rad) * radius;
                    double z = Math.cos(rad) * radius;
                    
                    Location particleLoc = player.getLocation().clone().add(x, 1, z);
                    player.getWorld().spawnParticle(Particle.DRAGON_BREATH, particleLoc, 3, 0.1, 0.1, 0.1, 0);
                    player.getWorld().spawnParticle(Particle.PORTAL, particleLoc, 2, 0.1, 0.1, 0.1, 0.05);
                }
                
                for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Vector pull = player.getLocation().toVector()
                            .subtract(entity.getLocation().toVector()).normalize().multiply(1.5);
                        entity.setVelocity(pull);
                        
                        if (radius > 5) {
                            ((LivingEntity) entity).damage(2.0, player);
                        }
                    }
                }
                
                radius += 0.5;
            }
        }.runTaskTimer(plugin, 0L, 2L);
        
        player.sendMessage("§8💫 Void nova unleashed!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Void Fist";
    }
    
    @Override
    public String getDescription() {
        return "§8Harness the power of nothingness";
    }
}
