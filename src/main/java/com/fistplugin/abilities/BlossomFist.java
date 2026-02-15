package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class BlossomFist extends BaseAbility {
    
    public BlossomFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click - Freeze projectile
        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(player.getLocation().getDirection().multiply(2.5));
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (snowball.isDead() || !snowball.isValid()) {
                    cancel();
                    return;
                }
                snowball.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, snowball.getLocation(), 2, 0.1, 0.1, 0.1, 0);
                snowball.getWorld().spawnParticle(Particle.END_ROD, snowball.getLocation(), 1, 0.1, 0.1, 0.1, 0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        plugin.getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onProjectileHit(org.bukkit.event.entity.ProjectileHitEvent event) {
                if (event.getEntity().equals(snowball) && event.getHitEntity() instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) event.getHitEntity();
                    
                    // Freeze for 4 seconds
                    target.setFreezeTicks(80);
                    
                    new BukkitRunnable() {
                        int ticks = 0;
                        
                        @Override
                        public void run() {
                            if (ticks >= 80 || target.isDead()) {
                                cancel();
                                return;
                            }
                            
                            target.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, 
                                target.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0);
                            target.getWorld().spawnParticle(Particle.END_ROD, 
                                target.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0);
                            ticks += 5;
                        }
                    }.runTaskTimer(plugin, 0L, 5L);
                    
                    target.getWorld().playSound(target.getLocation(), Sound.BLOCK_POWDER_SNOW_STEP, 1.0f, 0.5f);
                }
            }
        }, plugin);
        
        player.sendMessage("§d🌸 Freeze projectile launched!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click - Chain freeze with poison
        LivingEntity target = getTargetEntity(player, 30);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        Location start = player.getLocation().add(0, 1, 0);
        Location end = target.getLocation().add(0, 1, 0);
        
        // Chain particles
        spawnLineParticles(start, end, Particle.END_ROD, 0.3);
        
        // Freeze and poison for 10 seconds
        target.setFreezeTicks(200);
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
        
        // Damage over time
        new BukkitRunnable() {
            int damageTicks = 0;
            
            @Override
            public void run() {
                if (damageTicks >= 10 || target.isDead()) {
                    cancel();
                    return;
                }
                
                target.damage(2.0, player);
                target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, 
                    target.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0);
                
                // Spiral particles
                for (int i = 0; i < 360; i += 45) {
                    double rad = Math.toRadians(i + damageTicks * 20);
                    double x = Math.sin(rad) * 1.5;
                    double z = Math.cos(rad) * 1.5;
                    
                    Location spiral = target.getLocation().clone().add(x, 1 + Math.sin(rad) * 0.5, z);
                    target.getWorld().spawnParticle(Particle.ENTITY_EFFECT, spiral, 1, 0, 0, 0, 0);
                }
                
                damageTicks++;
            }
        }.runTaskTimer(plugin, 20L, 20L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f);
        player.sendMessage("§d🌿 Target frozen and poisoned for 10 seconds!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Blossom Fist";
    }
    
    @Override
    public String getDescription() {
        return "§dControl your enemies with nature's grasp";
    }
}
