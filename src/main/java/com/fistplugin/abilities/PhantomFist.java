package com.fistplugin.abilities;

import com.fistplugin.FistPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PhantomFist extends BaseAbility {
    
    public PhantomFist(FistPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean onRightClick(Player player) {
        // Phase through reality
        player.setInvulnerable(true);
        player.setInvisible(true);
        
        // Phantom particles
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 40) { // 2 seconds
                    player.setInvulnerable(false);
                    player.setInvisible(false);
                    cancel();
                    return;
                }
                
                Location loc = player.getLocation().add(0, 1, 0);
                
                // Ghostly particles
                for (int i = 0; i < 3; i++) {
                    double offsetX = (Math.random() - 0.5) * 1.5;
                    double offsetY = Math.random() * 2;
                    double offsetZ = (Math.random() - 0.5) * 1.5;
                    
                    player.getWorld().spawnParticle(Particle.SPELL_INSTANT, 
                        loc.clone().add(offsetX, offsetY, offsetZ), 1, 0, 0, 0, 0);
                }
                
                // Fading effect
                player.getWorld().spawnParticle(Particle.REDSTONE, loc, 5, 0.3, 0.3, 0.3, 0,
                    new Particle.DustOptions(Color.WHITE, 1));
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 1.0f, 1.5f);
        player.sendMessage("§f👻 You phased through reality!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public boolean onCrouchRightClick(Player player) {
        LivingEntity target = getTargetEntity(player, 30);
        if (target == null) {
            player.sendMessage("§cNo target found!");
            return false;
        }
        
        // Possession effect
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 140, 1)); // 7 seconds
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0)); // 3 seconds
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1)); // 5 seconds
        
        // Possession particles
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks >= 70 || target.isDead()) { // 3.5 seconds
                    cancel();
                    return;
                }
                
                Location loc = target.getLocation().add(0, 1, 0);
                
                // Possession effect around head
                for (int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i + ticks * 10);
                    double x = Math.sin(rad) * 1.2;
                    double z = Math.cos(rad) * 1.2;
                    
                    Location particleLoc = loc.clone().add(x, 0, z);
                    target.getWorld().spawnParticle(Particle.SPELL_MOB, particleLoc, 1, 0, 0, 0, 0,
                        new Particle.DustOptions(Color.GRAY, 1));
                }
                
                // Damage over time
                if (ticks % 10 == 0) {
                    target.damage(1.0, player);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
        
        player.getWorld().playSound(target.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 1.0f, 0.8f);
        player.sendMessage("§f👁️ Target possessed!");
        
        plugin.getFistManager().getPlayerData(player).addAbilityUsed();
        return true;
    }
    
    @Override
    public String getName() {
        return "Phantom Fist";
    }
    
    @Override
    public String getDescription() {
        return "Phase through reality and possess enemies";
    }
}
