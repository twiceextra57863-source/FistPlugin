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
                if (!player.isOnline()) { cancel(); return; }
                angle += 0.2;
                double x = Math.sin(angle) * 1.5;
                double z = Math.cos(angle) * 1.5;
                player.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(x, 1, z), 1, 0, 0, 0, 0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void spawnBlossomParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                for (int i = 0; i < 3; i++) {
                    double offsetX = (Math.random() - 0.5) * 2;
                    double offsetZ = (Math.random() - 0.5) * 2;
                    player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, 
                        loc.clone().add(offsetX, 0, offsetZ), 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
    
    private void spawnBeastParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, loc, 2, 0.5, 0.5, 0.5, 0);
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
    
    private void spawnWaterParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                player.getWorld().spawnParticle(Particle.FALLING_WATER, loc, 3, 0.5, 0.5, 0.5, 0);
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
    
    private void spawnRealityParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                player.getWorld().spawnParticle(Particle.BLOCK, loc, 3, 0.5, 0.5, 0.5, 0, 
                    org.bukkit.Material.STONE.createBlockData());
            }
        }.runTaskTimer(plugin, 0L, 8L);
    }
    
    private void spawnCosmicParticles(Player player, Location loc) {
        new BukkitRunnable() {
            double angle = 0;
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                angle += 0.1;
                double x = Math.sin(angle) * 1.5;
                double z = Math.cos(angle) * 1.5;
                player.getWorld().spawnParticle(Particle.PORTAL, loc.clone().add(x, 1, z), 1, 0, 0, 0, 0);
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    private void spawnWolfParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                player.getWorld().spawnParticle(Particle.CRIT, loc, 2, 0.5, 0.5, 0.5, 0.1);
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
    
    private void spawnBombParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                player.getWorld().spawnParticle(Particle.SMOKE, loc, 3, 0.3, 0.3, 0.3, 0.02);
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }
    
    private void spawnVoidParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 2, 0.4, 0.4, 0.4, 0);
            }
        }.runTaskTimer(plugin, 0L, 6L);
    }
    
    private void spawnPhantomParticles(Player player, Location loc) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }
                player.getWorld().spawnParticle(Particle.INSTANT_EFFECT, loc, 3, 0.5, 0.5, 0.5, 0);
            }
        }.runTaskTimer(plugin, 0L, 3L);
    }
}
