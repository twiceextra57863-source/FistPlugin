package com.fistplugin.managers;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleManager {
    
    private final FistPlugin plugin;
    
    public ParticleManager(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void spawnIdleParticles(Player player, FistType fist) {
        Location loc = player.getLocation().add(0, 1, 0);
        
        switch(fist) {
            case ORB:
                spawnOrbIdleParticles(loc);
                break;
            case BLOSSOM:
                spawnBlossomIdleParticles(loc);
                break;
            case BEAST:
                spawnBeastIdleParticles(loc);
                break;
            case WATER:
                spawnWaterIdleParticles(loc);
                break;
            case REALITY:
                spawnRealityIdleParticles(loc);
                break;
            case COSMIC:
                spawnCosmicIdleParticles(loc);
                break;
            case WOLF:
                spawnWolfIdleParticles(loc);
                break;
            case BOMB:
                spawnBombIdleParticles(loc);
                break;
            case VOID:
                spawnVoidIdleParticles(loc);
                break;
            case PHANTOM:
                spawnPhantomIdleParticles(loc);
                break;
        }
    }
    
    // ORB - Rotating fire rings with soul flames
    private void spawnOrbIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 200.0;
        
        // Outer ring - rotating
        for (int i = 0; i < 8; i++) {
            double angle = time + (i * Math.PI / 4);
            double x = Math.sin(angle) * 1.8;
            double z = Math.cos(angle) * 1.8;
            double y = Math.sin(angle * 2) * 0.3;
            
            loc.getWorld().spawnParticle(Particle.FLAME, 
                loc.clone().add(x, 1.2 + y, z), 1, 0, 0, 0, 0);
        }
        
        // Inner ring - opposite direction
        for (int i = 0; i < 4; i++) {
            double angle = -time + (i * Math.PI / 2);
            double x = Math.sin(angle) * 0.8;
            double z = Math.cos(angle) * 0.8;
            
            loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, 
                loc.clone().add(x, 1.5, z), 1, 0, 0, 0, 0);
        }
        
        // Floating orbs
        for (int i = 0; i < 3; i++) {
            double offset = time + i * 2;
            double y = Math.sin(offset) * 0.5 + 1.2;
            loc.getWorld().spawnParticle(Particle.END_ROD, 
                loc.clone().add(0, y, 0), 1, 0.1, 0.1, 0.1, 0);
        }
    }
    
    // BLOSSOM - Floating flower petals and sparkles
    private void spawnBlossomIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 300.0;
        
        // Spiral of petals
        for (int i = 0; i < 6; i++) {
            double angle = time + (i * Math.PI / 3);
            double radius = 1.2 + Math.sin(angle * 2) * 0.3;
            double x = Math.sin(angle) * radius;
            double z = Math.cos(angle) * radius;
            double y = Math.cos(angle * 1.5) * 0.5 + 1.3;
            
            loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, 
                loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
        }
        
        // Falling petals
        for (int i = 0; i < 4; i++) {
            double offset = (time * 10 + i * 10) % 20;
            double x = Math.sin(offset) * 1.5;
            double z = Math.cos(offset) * 1.5;
            
            loc.getWorld().spawnParticle(Particle.CHERRY_LEAVES, 
                loc.clone().add(x, 2.5 - offset/10, z), 1, 0, 0, 0, 0);
        }
        
        // Sparkles
        for (int i = 0; i < 3; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetZ = (Math.random() - 0.5) * 2;
            loc.getWorld().spawnParticle(Particle.END_ROD, 
                loc.clone().add(offsetX, 1 + Math.random(), offsetZ), 1, 0, 0, 0, 0);
        }
    }
    
    // BEAST - Pulsing angry particles with grow/shrink effect
    private void spawnBeastIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 150.0;
        double pulse = Math.abs(Math.sin(time)) * 0.5 + 0.8;
        
        // Pulsing ring
        for (int i = 0; i < 6; i++) {
            double angle = time * 2 + (i * Math.PI / 3);
            double radius = 1.2 * pulse;
            double x = Math.sin(angle) * radius;
            double z = Math.cos(angle) * radius;
            
            loc.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, 
                loc.clone().add(x, 1.2, z), 1, 0, 0, 0, 0);
        }
        
        // Claw marks
        for (int i = 0; i < 3; i++) {
            double angle = time + (i * Math.PI * 2 / 3);
            double x = Math.sin(angle) * 1.8;
            double z = Math.cos(angle) * 1.8;
            
            for (int j = 0; j < 3; j++) {
                double offset = j * 0.3;
                loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, 
                    loc.clone().add(x + offset, 1, z + offset), 1, 0, 0, 0, 0);
            }
        }
    }
    
    // WATER - Flowing water streams and droplets
    private void spawnWaterIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 150.0;
        
        // Spiral water stream
        for (int i = 0; i < 8; i++) {
            double angle = time + (i * Math.PI / 4);
            double radius = 1.5;
            double x = Math.sin(angle) * radius;
            double z = Math.cos(angle) * radius;
            double y = Math.sin(angle * 3) * 0.5 + 1.2;
            
            loc.getWorld().spawnParticle(Particle.FALLING_WATER, 
                loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
        }
        
        // Water droplets
        for (int i = 0; i < 5; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetZ = (Math.random() - 0.5) * 2;
            
            loc.getWorld().spawnParticle(Particle.DRIP_WATER, 
                loc.clone().add(offsetX, 2, offsetZ), 1, 0, 0, 0, 0);
        }
        
        // Splash ring
        for (int i = 0; i < 360; i += 45) {
            double rad = Math.toRadians(i + time * 50);
            double x = Math.sin(rad) * 1.2;
            double z = Math.cos(rad) * 1.2;
            
            loc.getWorld().spawnParticle(Particle.SPLASH, 
                loc.clone().add(x, 0.8, z), 1, 0, 0, 0, 0);
        }
    }
    
    // REALITY - Floating stone blocks and dust
    private void spawnRealityIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 250.0;
        
        // Floating blocks
        for (int i = 0; i < 4; i++) {
            double angle = time + (i * Math.PI / 2);
            double x = Math.sin(angle) * 1.5;
            double z = Math.cos(angle) * 1.5;
            double y = Math.cos(angle * 2) * 0.5 + 1.3;
            
            loc.getWorld().spawnParticle(Particle.BLOCK, 
                loc.clone().add(x, y, z), 1, 0, 0, 0, 0, 
                org.bukkit.Material.STONE.createBlockData());
        }
        
        // Dust clouds
        for (int i = 0; i < 3; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetZ = (Math.random() - 0.5) * 2;
            
            loc.getWorld().spawnParticle(Particle.BLOCK, 
                loc.clone().add(offsetX, 0.8, offsetZ), 1, 0.1, 0.1, 0.1, 0, 
                org.bukkit.Material.DIRT.createBlockData());
        }
        
        // Cracking effect
        for (int i = 0; i < 360; i += 60) {
            double rad = Math.toRadians(i + time * 30);
            double x = Math.sin(rad) * 1.8;
            double z = Math.cos(rad) * 1.8;
            
            loc.getWorld().spawnParticle(Particle.BLOCK, 
                loc.clone().add(x, 1, z), 1, 0, 0, 0, 0, 
                org.bukkit.Material.COBBLESTONE.createBlockData());
        }
    }
    
    // COSMIC - Galaxy spiral with portal effects
    private void spawnCosmicIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 100.0;
        
        // Main galaxy spiral
        for (int i = 0; i < 12; i++) {
            double progress = (time + i * 0.5) % (Math.PI * 2);
            double radius = 1.2 + Math.sin(progress * 2) * 0.4;
            double x = Math.sin(progress) * radius;
            double z = Math.cos(progress) * radius;
            double y = Math.cos(progress * 3) * 0.6 + 1.2;
            
            loc.getWorld().spawnParticle(Particle.PORTAL, 
                loc.clone().add(x, y, z), 1, 0, 0, 0, 0.05);
        }
        
        // Second spiral (opposite direction)
        for (int i = 0; i < 8; i++) {
            double progress = -time + (i * Math.PI / 4);
            double x = Math.sin(progress) * 1.5;
            double z = Math.cos(progress) * 1.5;
            
            loc.getWorld().spawnParticle(Particle.END_ROD, 
                loc.clone().add(x, 1.3, z), 1, 0, 0, 0, 0);
        }
        
        // Stars
        for (int i = 0; i < 5; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetY = Math.random() * 2;
            double offsetZ = (Math.random() - 0.5) * 2;
            
            loc.getWorld().spawnParticle(Particle.END_ROD, 
                loc.clone().add(offsetX, offsetY, offsetZ), 1, 0, 0, 0, 0);
        }
    }
    
    // WOLF - Claw marks and pack spirits
    private void spawnWolfIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 150.0;
        
        // Pouncing effect
        for (int i = 0; i < 3; i++) {
            double angle = time + (i * Math.PI * 2 / 3);
            double x = Math.sin(angle) * 1.8;
            double z = Math.cos(angle) * 1.8;
            
            loc.getWorld().spawnParticle(Particle.CRIT, 
                loc.clone().add(x, 1, z), 2, 0.1, 0.1, 0.1, 0.2);
        }
        
        // Wolf spirits
        for (int i = 0; i < 2; i++) {
            double offset = time + i * 5;
            double x = Math.sin(offset) * 1.5;
            double z = Math.cos(offset) * 1.5;
            
            loc.getWorld().spawnParticle(Particle.SOUL, 
                loc.clone().add(x, 1.3, z), 1, 0, 0, 0, 0);
        }
        
        // Claw swipe particles
        for (int i = 0; i < 4; i++) {
            double angle = time * 2 + (i * Math.PI / 2);
            double x = Math.sin(angle) * 1.2;
            double z = Math.cos(angle) * 1.2;
            
            loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, 
                loc.clone().add(x, 1.1, z), 1, 0, 0, 0, 0);
        }
    }
    
    // BOMB - Smoke rings and sparks
    private void spawnBombIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 200.0;
        
        // Smoke rings
        for (int i = 0; i < 3; i++) {
            double radius = 1.0 + Math.sin(time + i) * 0.3;
            
            for (int j = 0; j < 8; j++) {
                double angle = (j * Math.PI / 4) + time;
                double x = Math.sin(angle) * radius;
                double z = Math.cos(angle) * radius;
                
                loc.getWorld().spawnParticle(Particle.SMOKE, 
                    loc.clone().add(x, 1.2 + i * 0.3, z), 1, 0, 0, 0, 0.01);
            }
        }
        
        // Sparks
        for (int i = 0; i < 4; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetZ = (Math.random() - 0.5) * 2;
            
            loc.getWorld().spawnParticle(Particle.DUST, 
                loc.clone().add(offsetX, 1 + Math.random(), offsetZ), 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.ORANGE, 1));
        }
        
        // Fuse effect
        double y = Math.sin(time * 5) * 0.2 + 1.8;
        loc.getWorld().spawnParticle(Particle.FLAME, 
            loc.clone().add(0, y, 0), 1, 0.1, 0.1, 0.1, 0);
    }
    
    // VOID - Black hole effect with pulling particles
    private void spawnVoidIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 150.0;
        
        // Black hole spiral
        for (int i = 0; i < 16; i++) {
            double progress = (time + i * 0.4) % (Math.PI * 2);
            double radius = 1.8 - progress * 0.1;
            double x = Math.sin(progress * 3) * radius;
            double z = Math.cos(progress * 3) * radius;
            double y = Math.cos(progress) * 0.8 + 1.2;
            
            loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, 
                loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
        }
        
        // Pull effect
        for (int i = 0; i < 6; i++) {
            double angle = (i * Math.PI / 3) + time;
            double x = Math.sin(angle) * 2;
            double z = Math.cos(angle) * 2;
            
            for (double d = 0; d < 2; d += 0.2) {
                double progress = d / 2;
                double px = x * (1 - progress);
                double pz = z * (1 - progress);
                
                loc.getWorld().spawnParticle(Particle.PORTAL, 
                    loc.clone().add(px, 1 + d * 0.5, pz), 1, 0, 0, 0, 0.05);
            }
        }
        
        // Void essence
        for (int i = 0; i < 3; i++) {
            double offsetX = (Math.random() - 0.5) * 1.5;
            double offsetZ = (Math.random() - 0.5) * 1.5;
            
            loc.getWorld().spawnParticle(Particle.SOUL, 
                loc.clone().add(offsetX, 1.2, offsetZ), 1, 0, 0, 0, 0);
        }
    }
    
    // PHANTOM - Ghostly wisps and translucent effects
    private void spawnPhantomIdleParticles(Location loc) {
        double time = System.currentTimeMillis() / 180.0;
        
        // Ghostly wisps
        for (int i = 0; i < 6; i++) {
            double angle = time + (i * Math.PI / 3);
            double x = Math.sin(angle) * 1.5;
            double z = Math.cos(angle) * 1.5;
            double y = Math.sin(angle * 2) * 0.6 + 1.2;
            
            loc.getWorld().spawnParticle(Particle.INSTANT_EFFECT, 
                loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
        }
        
        // Fading trails
        for (int i = 0; i < 4; i++) {
            double offset = time * 5 + i * 5;
            double x = Math.sin(offset) * 1.8;
            double z = Math.cos(offset) * 1.8;
            
            loc.getWorld().spawnParticle(Particle.DUST, 
                loc.clone().add(x, 1.3, z), 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.WHITE, 0.8f));
        }
        
        // Ghost shapes
        for (int i = 0; i < 3; i++) {
            double y = 1.0 + Math.sin(time * 3 + i) * 0.5;
            
            for (int j = 0; j < 4; j++) {
                double angle = (j * Math.PI / 2) + time;
                double x = Math.sin(angle) * 0.8;
                double z = Math.cos(angle) * 0.8;
                
                loc.getWorld().spawnParticle(Particle.SOUL, 
                    loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
            }
        }
    }
    
    // For ability activation (temporary particles)
    public void spawnFistParticles(Player player, FistType fist) {
        Location loc = player.getLocation().add(0, 1, 0);
        
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 30 || !player.isOnline()) { // 1.5 seconds
                    cancel();
                    return;
                }
                
                // Burst of particles
                switch(fist) {
                    case ORB:
                        for (int i = 0; i < 360; i += 20) {
                            double rad = Math.toRadians(i + ticks * 10);
                            double x = Math.sin(rad) * 2;
                            double z = Math.cos(rad) * 2;
                            loc.getWorld().spawnParticle(Particle.FLAME, 
                                loc.clone().add(x, 1, z), 1, 0, 0, 0, 0);
                        }
                        break;
                        
                    case BLOSSOM:
                        for (int i = 0; i < 20; i++) {
                            double offsetX = (Math.random() - 0.5) * 3;
                            double offsetZ = (Math.random() - 0.5) * 3;
                            loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, 
                                loc.clone().add(offsetX, Math.random() * 2, offsetZ), 1, 0, 0, 0, 0);
                        }
                        break;
                        
                    // Add other cases...
                    default:
                        break;
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
                }
