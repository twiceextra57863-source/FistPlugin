package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CosmicFist extends BaseAbility {
    
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
        
        new BukkitRunnable() {
            int rotations = 0;
            
            @Override
            public void run() {
                if (rotations >= 40 || target.isDead()) {
                    cancel();
                    return;
                }
                
                Location loc = target.getLocation();
                loc.setYaw(loc.getYaw() + 18);
                target.teleport(loc);
                
                for (int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i + rotations * 10);
                    double x = Math.sin(rad) * 1.5;
                    double z = Math.cos(rad) * 1.5;
                    
                    Location particleLoc = target.getLocation().clone().add(x, 1, z);
                    target.getWorld().spawnParticle(Particle.PORTAL, particleLoc, 3, 0.1, 0.1, 0.1, 0.1);
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
        
        Location start = player.getEyeLocation();
        Location end = target.getLocation().add(0, 1, 0);
        
        spawnLineParticles(start, end, Particle.END_ROD, 0.3);
        
        // Fixed: EXPLOSION_NORMAL -> EXPLOSION
        target.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
        
        player.sendMessage("§3🪝 Target hooked!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
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
