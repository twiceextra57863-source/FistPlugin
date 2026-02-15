package com.fistplugin.managers;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleManager {
    
    private final FistPlugin plugin;
    
    public ParticleManager(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void spawnFistParticles(Player player, FistType fist) {
        // Limited time particles - 3 seconds ke liye
        final Location loc = player.getLocation().add(0, 1, 0);
        
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 60 || !player.isOnline()) { // 3 seconds (60 ticks)
                    cancel();
                    return;
                }
                
                switch(fist) {
                    case ORB:
                        spawnOrbParticles(loc);
                        break;
                    case BLOSSOM:
                        spawnBlossomParticles(loc);
                        break;
                    case BEAST:
                        spawnBeastParticles(loc);
                        break;
                    case WATER:
                        spawnWaterParticles(loc);
                        break;
                    case REALITY:
                        spawnRealityParticles(loc);
                        break;
                    case COSMIC:
                        spawnCosmicParticles(loc);
                        break;
                    case WOLF:
                        spawnWolfParticles(loc);
                        break;
                    case BOMB:
                        spawnBombParticles(loc);
                        break;
                    case VOID:
                        spawnVoidParticles(loc);
                        break;
                    case PHANTOM:
                        spawnPhantomParticles(loc);
                        break;
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void spawnOrbParticles(Location loc) {
        double angle = System.currentTimeMillis() / 100.0;
        double x = Math.sin(angle) * 1.5;
        double z = Math.cos(angle) * 1.5;
        loc.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(x, 1, z), 1, 0, 0, 0, 0);
        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(x * 0.5, 0.5, z * 0.5), 1, 0, 0, 0, 0);
    }
    
    private void spawnBlossomParticles(Location loc) {
        for (int i = 0; i < 3; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetZ = (Math.random() - 0.5) * 2;
            loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, 
                loc.clone().add(offsetX, 0, offsetZ), 1, 0, 0, 0, 0);
        }
    }
    
    private void spawnBeastParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, loc, 2, 0.5, 0.5, 0.5, 0);
    }
    
    private void spawnWaterParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.FALLING_WATER, loc, 3, 0.5, 0.5, 0.5, 0);
    }
    
    private void spawnRealityParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.BLOCK, loc, 3, 0.5, 0.5, 0.5, 0, 
            org.bukkit.Material.STONE.createBlockData());
    }
    
    private void spawnCosmicParticles(Location loc) {
        double angle = System.currentTimeMillis() / 100.0;
        double x = Math.sin(angle) * 1.5;
        double z = Math.cos(angle) * 1.5;
        loc.getWorld().spawnParticle(Particle.PORTAL, loc.clone().add(x, 1, z), 1, 0, 0, 0, 0);
    }
    
    private void spawnWolfParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.CRIT, loc, 2, 0.5, 0.5, 0.5, 0.1);
    }
    
    private void spawnBombParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.SMOKE, loc, 3, 0.3, 0.3, 0.3, 0.02);
    }
    
    private void spawnVoidParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 2, 0.4, 0.4, 0.4, 0);
    }
    
    private void spawnPhantomParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.INSTANT_EFFECT, loc, 3, 0.5, 0.5, 0.5, 0);
    }
    
    // For projectile trails (1 second only)
    public void spawnProjectileTrail(Location loc, FistType fist) {
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 20) { // 1 second
                    cancel();
                    return;
                }
                
                switch(fist) {
                    case ORB:
                        loc.getWorld().spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
                        break;
                    case BLOSSOM:
                        loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 1, 0, 0, 0, 0);
                        break;
                    case BEAST:
                        loc.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, loc, 1, 0, 0, 0, 0);
                        break;
                    default:
                        break;
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
