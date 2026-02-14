package com.fistplugin.managers;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ParticleManager {
    
    private final FistPlugin plugin;
    
    public ParticleManager(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void spawnFistParticles(Player player, FistType fist) {
        Location loc = player.getLocation().add(0, 1, 0);
        
        switch(fist) {
            case ORB:
                spawnOrbParticles(player, loc);
                break;
            case BLOSSOM:
                spawnBlossomParticles(player, loc);
                break;
            case BEAST:
                spawnBeastParticles(player, loc);
                break;
            case WATER:
                spawnWaterParticles(player, loc);
                break;
            case REALITY:
                spawnRealityParticles(player, loc);
                break;
            case COSMIC:
                spawnCosmicParticles(player, loc);
                break;
            case WOLF:
                spawnWolfParticles(player, loc);
                break;
            case BOMB:
                spawnBombParticles(player, loc);
                break;
            case VOID:
                spawnVoidParticles(player, loc);
                break;
            case PHANTOM:
                spawnPhantomParticles(player, loc);
                break;
        }
    }
    
    private void spawnOrbParticles(Player player, Location loc) {
        new BukkitRunnable() {
            double angle = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                angle += 0.2;
                double x = Math.sin(angle) * 1.5;
                double z = Math.cos(angle) * 1.5;
                double y = Math.sin(angle * 2) * 0.5 + 1;
                
                Location particleLoc = loc.clone().add(x, y, z);
                player.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                
                // Inner particles
                for (int i = 0; i < 3; i++) {
                    double innerAngle = angle + (i * Math.PI * 2 / 3);
                    double ix = Math.sin(innerAngle) * 0.5;
                    double iz = Math.cos(innerAngle) * 0.5;
                    player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, 
                        loc.clone().add(ix, 0.5, iz), 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void spawnBlossomParticles(Player player, Location loc) {
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                // Flower petals
                for (int i = 0; i < 5; i++) {
                    double offsetX = (Math.random() - 0.5) * 2;
                    double offsetY = Math.random() * 2;
                    double offsetZ = (Math.random() - 0.5) * 2;
                    
                    player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, 
                        loc.clone().add(offsetX, offsetY, offsetZ), 1, 0, 0, 0, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
    
    private void spawnBeastParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                // Beast aura
                player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, 
                    loc.clone().add(0, 0.5, 0), 2, 0.5, 0.5, 0.5, 0);
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
    
    private void spawnWaterParticles(Player player, Location loc) {
        new BukkitRunnable() {
            double angle = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                angle += 0.3;
                
                // Water spiral
                for (int i = 0; i < 3; i++) {
                    double spiralAngle = angle + (i * Math.PI * 2 / 3);
                    double x = Math.sin(spiralAngle) * 1.2;
                    double z = Math.cos(spiralAngle) * 1.2;
                    double y = Math.sin(spiralAngle * 2) * 0.5 + 1;
                    
                    player.getWorld().spawnParticle(Particle.FALLING_WATER, 
                        loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void spawnRealityParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                // Block break particles
                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, 
                    loc.clone().add(0, 0.5, 0), 3, 0.5, 0.5, 0.5, 0, 
                    org.bukkit.Material.STONE.createBlockData());
            }
        }.runTaskTimer(plugin, 0L, 8L);
    }
    
    private void spawnCosmicParticles(Player player, Location loc) {
        new BukkitRunnable() {
            double angle = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                angle += 0.1;
                
                // Galaxy effect
                for (int i = 0; i < 8; i++) {
                    double galaxyAngle = angle + (i * Math.PI / 4);
                    double radius = 1.5 + Math.sin(angle * 2 + i) * 0.5;
                    double x = Math.sin(galaxyAngle) * radius;
                    double z = Math.cos(galaxyAngle) * radius;
                    double y = Math.cos(galaxyAngle + angle) * 0.5 + 1;
                    
                    player.getWorld().spawnParticle(Particle.PORTAL, 
                        loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    private void spawnWolfParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                // Wolf claws
                for (int i = 0; i < 3; i++) {
                    double offsetX = (Math.random() - 0.5) * 1.5;
                    double offsetZ = (Math.random() - 0.5) * 1.5;
                    
                    player.getWorld().spawnParticle(Particle.CRIT, 
                        loc.clone().add(offsetX, 0.2, offsetZ), 2, 0.1, 0.1, 0.1, 0.1);
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
    
    private void spawnBombParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                // Smoke effect
                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, 
                    loc.clone().add(0, 0.2, 0), 5, 0.3, 0.1, 0.3, 0.02);
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }
    
    private void spawnVoidParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                // Void swirl
                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, 
                    loc.clone().add(0, 0.5, 0), 3, 0.4, 0.2, 0.4, 0);
            }
        }.runTaskTimer(plugin, 0L, 6L);
    }
    
    private void spawnPhantomParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                // Ghostly effect
                player.getWorld().spawnParticle(Particle.SPELL_INSTANT, 
                    loc.clone().add(0, 0.5, 0), 4, 0.5, 0.3, 0.5, 0);
            }
        }.runTaskTimer(plugin, 0L, 3L);
    }
    
    // Trail particles for projectiles
    public void spawnProjectileTrail(Location loc, FistType fist) {
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
    }
    
    // Cinematic particles for abilities
    public void spawnCinematicParticles(Location center, FistType fist, int count) {
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 / count) * i;
            double x = Math.sin(angle) * 3;
            double z = Math.cos(angle) * 3;
            
            Location particleLoc = center.clone().add(x, 1 + Math.sin(angle * 2), z);
            
            switch(fist) {
                case ORB:
                    center.getWorld().spawnParticle(Particle.FLAME, particleLoc, 3, 0.1, 0.1, 0.1, 0);
                    break;
                case WATER:
                    center.getWorld().spawnParticle(Particle.FALLING_WATER, particleLoc, 3, 0.1, 0.1, 0.1, 0);
                    break;
                default:
                    center.getWorld().spawnParticle(fist.getParticle(), particleLoc, 3, 0.1, 0.1, 0.1, 0);
            }
        }
    }
            }
