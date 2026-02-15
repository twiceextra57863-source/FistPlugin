package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OrbFist extends BaseAbility {
    
    public OrbFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setVelocity(player.getLocation().getDirection().multiply(2.0));
        fireball.setYield(3.0f);
        fireball.setIsIncendiary(false);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fireball.isDead() || !fireball.isValid()) {
                    cancel();
                    return;
                }
                
                Location loc = fireball.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 3, 0.1, 0.1, 0.1, 0.02);
                // Fixed: SMOKE_NORMAL -> SMOKE
                loc.getWorld().spawnParticle(Particle.SMOKE, loc, 2, 0.1, 0.1, 0.1, 0.01);
                
                loc.getWorld().getNearbyEntities(loc, 2, 2, 2).stream()
                    .filter(e -> e instanceof LivingEntity && e != player)
                    .findFirst()
                    .ifPresent(e -> {
                        fireball.remove();
                        loc.getWorld().createExplosion(loc, 3.0f, false, true);
                    });
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Implementation
        player.sendMessage("§6🥊 Boxing arena creation - Coming soon!");
        return true;
    }
    
    @Override
    public String getName() {
        return "Orb Fist";
    }
    
    @Override
    public String getDescription() {
        return "Master of explosive orbs";
    }
}
