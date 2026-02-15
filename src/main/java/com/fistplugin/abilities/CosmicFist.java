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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmicFist extends BaseAbility {
    
    private final Map<UUID, UUID> hookedTargets = new HashMap<>();
    
    public CosmicFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click - Spin target
        final LivingEntity target = getTargetEntity(player, 30);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        new BukkitRunnable() {
            int rotations = 0;
            
            @Override
            public void run() {
                if (rotations >= 40 || target.isDead()) { // 2 seconds
                    cancel();
                    return;
                }
                
                Location loc = target.getLocation();
                loc.setYaw(loc.getYaw() + 18);
                target.teleport(loc);
                
                // Cosmic particles
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
        player.sendMessage("§3🌀 Target spinning!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click - Hook and drag
        final LivingEntity target = getTargetEntity(player, 40);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        Location start = player.getEyeLocation();
        Location end = target.getLocation().add(0, 1, 0);
        
        // Hook particles
        spawnLineParticles(start, end, Particle.END_ROD, 0.3);
        
        // Store hooked target
        hookedTargets.put(player.getUniqueId(), target.getUniqueId());
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1.0f, 0.5f);
        player.sendMessage("§3🪝 Hook shot! Left click to launch");
        
        // Remove after 5 seconds
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            hookedTargets.remove(player.getUniqueId());
        }, 100L);
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    public void onLeftClick(Player player) {
        UUID targetId = hookedTargets.get(player.getUniqueId());
        if (targetId == null) return;
        
        Entity target = plugin.getServer().getEntity(targetId);
        if (target instanceof LivingEntity && !target.isDead()) {
            Vector direction = player.getLocation().getDirection().multiply(3);
            target.setVelocity(direction);
            
            ((LivingEntity) target).damage(5.0, player);
            
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.0f);
            target.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
            
            hookedTargets.remove(player.getUniqueId());
        }
    }
    
    @Override
    public String getName() {
        return "Cosmic Fist";
    }
    
    @Override
    public String getDescription() {
        return "§3Control the gravity of your enemies";
    }
}
