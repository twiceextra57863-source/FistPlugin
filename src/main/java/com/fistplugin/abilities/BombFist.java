package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BombFist extends BaseAbility {
    
    public BombFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        LivingEntity target = getTargetEntity(player, 30);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        // Ghost effect that chases
        Location start = player.getLocation().add(0, 1, 0);
        
        new BukkitRunnable() {
            int ticks = 0;
            Location current = start.clone();
            Vector direction = target.getLocation().toVector().subtract(start.toVector()).normalize().multiply(0.5);
            
            @Override
            public void run() {
                if (ticks >= 60 || target.isDead()) { // 3 seconds max chase
                    // Spawn chicken bomb
                    spawnChickenBomb(current, player);
                    cancel();
                    return;
                }
                
                // Move towards target
                current.add(direction);
                
                // Ghost particles
                player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, current, 5, 0.2, 0.2, 0.2, 0.01);
                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, current, 3, 0.1, 0.1, 0.1, 0.01);
                
                // Check if reached target
                if (current.distance(target.getLocation().add(0, 1, 0)) < 1.5) {
                    target.damage(8.0, player);
                    spawnChickenBomb(target.getLocation(), player);
                    cancel();
                }
                
                // Update direction in case target moves
                direction = target.getLocation().toVector().add(new Vector(0, 1, 0))
                    .subtract(current.toVector()).normalize().multiply(0.5);
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 0.5f);
        player.sendMessage("§4💣 Ghost bomb chasing target!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    private void spawnChickenBomb(Location loc, Player owner) {
        Chicken chicken = loc.getWorld().spawn(loc, Chicken.class);
        chicken.setInvulnerable(true);
        chicken.setAI(false);
        chicken.setSilent(true);
        chicken.setCustomName("§cBOMB");
        
        // Countdown effect
        new BukkitRunnable() {
            int countdown = 3;
            
            @Override
            public void run() {
                if (countdown <= 0 || chicken.isDead()) {
                    // Explode
                    chicken.getWorld().createExplosion(chicken.getLocation(), 4.0f, false, true);
                    chicken.remove();
                    cancel();
                    return;
                }
                
                // Particle effect
                chicken.getWorld().spawnParticle(Particle.REDSTONE, chicken.getLocation().add(0, 1, 0), 
                    10, 0.3, 0.3, 0.3, 0, new Particle.DustOptions(Color.RED, 1));
                
                countdown--;
            }
        }.runTaskTimer(plugin, 20L, 20L); // Every second
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        Location start = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection().normalize();
        
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 130) { // 13 seconds
                    cancel();
                    return;
                }
                
                Location current = start.clone();
                
                for (int i = 0; i < 50; i++) {
                    current.add(direction);
                    
                    // Destroy blocks
                    if (!current.getBlock().getType().isAir()) {
                        current.getBlock().breakNaturally();
                        player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, current, 5, 0.2, 0.2, 0.2, 0.05);
                    }
                    
                    // Damage entities
                    for (Entity entity : player.getWorld().getNearbyEntities(current, 1.5, 1.5, 1.5)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            ((LivingEntity) entity).damage(3.0, player);
                            
                            // Knockback
                            Vector knockback = direction.clone().multiply(1.5);
                            entity.setVelocity(knockback);
                        }
                    }
                    
                    // Laser particles
                    player.getWorld().spawnParticle(Particle.REDSTONE, current, 1, 0, 0, 0, 0, 
                        new Particle.DustOptions(Color.RED, 1));
                    
                    if (!current.getBlock().getType().isAir()) {
                        break;
                    }
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GUARDIAN_ATTACK, 1.0f, 0.5f);
        player.sendMessage("§4🔥 Laser activated!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Bomb Fist";
    }
    
    @Override
    public String getDescription() {
        return "Explosive chaos and destruction";
    }
}
