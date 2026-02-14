package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class BeastFist extends BaseAbility {
    
    public BeastFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        Egg egg = player.launchProjectile(Egg.class);
        egg.setVelocity(player.getLocation().getDirection().multiply(2));
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (egg.isDead() || !egg.isValid()) {
                    cancel();
                    return;
                }
                
                egg.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, egg.getLocation(), 1, 0.1, 0.1, 0.1, 0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        plugin.getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onEggHit(org.bukkit.event.entity.ProjectileHitEvent event) {
                if (event.getEntity().equals(egg) && event.getHitEntity() instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) event.getHitEntity();
                    
                    new BukkitRunnable() {
                        int time = 0;
                        
                        @Override
                        public void run() {
                            if (time >= 5 || target.isDead()) {
                                cancel();
                                return;
                            }
                            
                            double damage = 2.0 + (time * 2.0);
                            target.damage(damage, player);
                            
                            // Visual effect
                            target.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, 
                                target.getLocation().add(0, 1, 0), 20, 0.5, 1.0, 0.5, 0.1);
                            
                            // Sound
                            target.getWorld().playSound(target.getLocation(), 
                                Sound.ENTITY_RAVAGER_ROAR, 0.5f, 0.5f + (time * 0.2f));
                            
                            time++;
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                }
            }
        }, plugin);
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Visual shrink effect
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
        
        // Effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 2));
        
        // Particle effect
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 200) {
                    cancel();
                    return;
                }
                
                player.getWorld().spawnParticle(Particle.SPELL_MOB, 
                    player.getLocation().add(0, 0.5, 0), 5, 0.2, 0.2, 0.2, 0);
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0L, 5L);
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Beast Fist";
    }
    
    @Override
    public String getDescription() {
        return "Size matters - grow and shrink at will";
    }
}
