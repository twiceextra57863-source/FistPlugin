package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class BeastFist extends BaseAbility {
    
    public BeastFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Right click - Growing damage projectile
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
                            
                            double damage = 2.0 + (time * 2.0); // 2,4,6,8,10 damage
                            target.damage(damage, player);
                            
                            target.getWorld().spawnParticle(Particle.EXPLOSION, 
                                target.getLocation().add(0, 1, 0), 20, 0.5, 1.0, 0.5, 0.1);
                            
                            target.getWorld().playSound(target.getLocation(), 
                                Sound.ENTITY_RAVAGER_ROAR, 0.5f, 0.5f + (time * 0.2f));
                            
                            time++;
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                }
            }
        }, plugin);
        
        player.sendMessage("§c🐾 Growing damage applied!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        // Crouch + right click - Shrink for 10 seconds
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 2));
        
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 200) {
                    cancel();
                    return;
                }
                
                player.getWorld().spawnParticle(Particle.ENTITY_EFFECT, 
                    player.getLocation().add(0, 0.5, 0), 5, 0.2, 0.2, 0.2, 0);
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0L, 5L);
        
        player.sendMessage("§c🐁 You shrunk for 10 seconds!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Beast Fist";
    }
    
    @Override
    public String getDescription() {
        return "§cSize matters - grow and shrink at will";
    }
}
