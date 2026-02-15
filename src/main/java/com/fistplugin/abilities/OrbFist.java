package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrbFist extends BaseAbility {
    
    private final Map<UUID, Location> boxingArenas = new HashMap<>();
    private final Map<UUID, Location> originalLocations = new HashMap<>();
    
    public OrbFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click ability - Fire projectile
        final Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setVelocity(player.getLocation().getDirection().multiply(2.0));
        fireball.setYield(3.0f);
        fireball.setIsIncendiary(false);
        
        // Track projectile
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fireball.isDead() || !fireball.isValid()) {
                    cancel();
                    return;
                }
                
                Location loc = fireball.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 5, 0.2, 0.2, 0.2, 0.02);
                loc.getWorld().spawnParticle(Particle.SMOKE, loc, 3, 0.2, 0.2, 0.2, 0.01);
                
                // Check for nearby entities
                loc.getWorld().getNearbyEntities(loc, 2, 2, 2).stream()
                    .filter(e -> e instanceof LivingEntity && e != player)
                    .findFirst()
                    .ifPresent(e -> {
                        fireball.remove();
                        loc.getWorld().createExplosion(loc, 3.0f, false, true);
                    });
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
        player.sendMessage("§6⚡ Orb launched!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click ability - Boxing arena
        final Player target = getTargetPlayer(player, 50);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        // Create arena at target location
        final Location arenaCenter = target.getLocation().clone();
        
        // Store original locations
        final Location playerOriginalLoc = player.getLocation().clone();
        final Location targetOriginalLoc = target.getLocation().clone();
        
        originalLocations.put(player.getUniqueId(), playerOriginalLoc);
        originalLocations.put(target.getUniqueId(), targetOriginalLoc);
        
        // Calculate arena positions (10x10 area)
        final Location playerArenaPos = arenaCenter.clone().add(5, 0, 0);
        final Location targetArenaPos = arenaCenter.clone().add(-5, 0, 0);
        
        // Teleport to arena
        player.teleport(playerArenaPos);
        target.teleport(targetArenaPos);
        
        // Apply effects
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 300, 0));
        
        // Store arena location
        boxingArenas.put(player.getUniqueId(), arenaCenter);
        boxingArenas.put(target.getUniqueId(), arenaCenter);
        
        // Create visual arena
        createBoxingArena(arenaCenter, player.getWorld());
        
        player.sendMessage("§6🥊 Boxing arena created! Fight for 15 seconds!");
        target.sendMessage("§6🥊 You've been pulled into a boxing arena by " + player.getName() + "!");
        
        // Return after 15 seconds
        new BukkitRunnable() {
            int timeLeft = 15;
            
            @Override
            public void run() {
                if (timeLeft <= 0) {
                    // Remove arena
                    boxingArenas.remove(player.getUniqueId());
                    boxingArenas.remove(target.getUniqueId());
                    
                    // Teleport back
                    if (player.isOnline() && !player.isDead()) {
                        player.teleport(originalLocations.get(player.getUniqueId()));
                    }
                    if (target.isOnline() && !target.isDead()) {
                        target.teleport(originalLocations.get(target.getUniqueId()));
                    }
                    
                    originalLocations.remove(player.getUniqueId());
                    originalLocations.remove(target.getUniqueId());
                    
                    player.sendMessage("§6Boxing arena disappeared!");
                    if (target.isOnline()) {
                        target.sendMessage("§6Boxing arena disappeared!");
                    }
                    
                    cancel();
                    return;
                }
                
                // Keep players in arena
                if (player.getLocation().distance(arenaCenter) > 8) {
                    player.teleport(playerArenaPos);
                }
                if (target.getLocation().distance(arenaCenter) > 8) {
                    target.teleport(targetArenaPos);
                }
                
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    private void createBoxingArena(Location center, World world) {
        // Create visual barrier particles for 15 seconds
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 300) { // 15 seconds
                    cancel();
                    return;
                }
                
                // Create circular barrier
                for (int i = 0; i < 360; i += 10) {
                    double rad = Math.toRadians(i);
                    double x = Math.sin(rad) * 7;
                    double z = Math.cos(rad) * 7;
                    
                    // Vertical pillars
                    for (double y = 0; y < 5; y += 0.5) {
                        Location particleLoc = center.clone().add(x, y, z);
                        world.spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                    }
                    
                    // Top and bottom rings
                    Location topLoc = center.clone().add(x, 5, z);
                    Location bottomLoc = center.clone().add(x, 0, z);
                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, topLoc, 1, 0, 0, 0, 0);
                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, bottomLoc, 1, 0, 0, 0, 0);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    @Override
    public String getName() {
        return "Orb Fist";
    }
    
    @Override
    public String getDescription() {
        return "§6Master of explosive orbs and boxing arenas";
    }
}
