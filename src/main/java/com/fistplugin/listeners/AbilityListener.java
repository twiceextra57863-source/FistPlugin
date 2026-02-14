package com.fistplugin.listeners;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.projectiles.ProjectileSource;

public class AbilityListener implements Listener {
    
    private final FistPlugin plugin;
    
    public AbilityListener(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        
        if (!(shooter instanceof Player)) return;
        
        Player player = (Player) shooter;
        FistType fist = plugin.getFistManager().getPlayerFist(player);
        
        if (fist == null) return;
        
        // Handle different projectile hits based on fist type
        if (projectile instanceof Fireball && fist == FistType.ORB) {
            // Orb fist explosion
            event.getEntity().getWorld().createExplosion(
                event.getEntity().getLocation(), 3.0f, false, true);
        }
        
        if (projectile instanceof Snowball && fist == FistType.BLOSSOM) {
            // Blossom fist freeze effect
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getHitEntity();
                target.setFreezeTicks(80); // 4 seconds freeze
            }
        }
        
        if (projectile instanceof Egg && fist == FistType.BEAST) {
            // Beast fist damage
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getHitEntity();
                target.damage(6.0, player);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        
        Player player = (Player) event.getDamager();
        FistType fist = plugin.getFistManager().getPlayerFist(player);
        
        if (fist == null) return;
        
        // Handle cosmic fist left click launch
        if (fist == FistType.COSMIC && player.isSneaking()) {
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getEntity();
                target.setVelocity(player.getLocation().getDirection().multiply(3));
                target.damage(4.0, player);
            }
        }
    }
}
