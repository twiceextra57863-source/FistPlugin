package com.fistplugin.utils;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SoundUtils {
    
    private static FistPlugin plugin;
    
    public static void init(FistPlugin instance) {
        plugin = instance;
    }
    
    /**
     * Play ability sound based on fist type
     */
    public static void playAbilitySound(Location loc, FistType fist, boolean isCrouch) {
        switch(fist) {
            case ORB:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.8f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
                }
                break;
                
            case BLOSSOM:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.2f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_SNOWBALL_THROW, 1.0f, 1.5f);
                }
                break;
                
            case BEAST:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_RAVAGER_ROAR, 1.0f, 1.0f);
                }
                break;
                
            case WATER:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ITEM_BUCKET_EMPTY, 2.0f, 0.5f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.2f);
                }
                break;
                
            case REALITY:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
                }
                break;
                
            case COSMIC:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_FISHING_BOBBER_THROW, 1.0f, 0.5f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
                }
                break;
                
            case WOLF:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_WOLF_HOWL, 1.0f, 1.0f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_WOLF_GROWL, 1.0f, 1.0f);
                }
                break;
                
            case BOMB:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_GUARDIAN_ATTACK, 1.0f, 0.5f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_CREEPER_PRIMED, 1.0f, 0.5f);
                }
                break;
                
            case VOID:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                }
                break;
                
            case PHANTOM:
                if (isCrouch) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_PHANTOM_SWOOP, 1.0f, 0.8f);
                } else {
                    loc.getWorld().playSound(loc, Sound.ENTITY_PHANTOM_AMBIENT, 1.0f, 1.5f);
                }
                break;
        }
    }
    
    /**
     * Play sound with pitch variation
     */
    public static void playSoundVaried(Location loc, Sound sound, float volume, float basePitch, float variation) {
        float pitch = basePitch + (float)(Math.random() * variation * 2 - variation);
        loc.getWorld().playSound(loc, sound, volume, pitch);
    }
    
    /**
     * Play a sequence of sounds
     */
    public static void playSoundSequence(Location loc, Sound[] sounds, float[] delays) {
        new BukkitRunnable() {
            int index = 0;
            
            @Override
            public void run() {
                if (index >= sounds.length) {
                    cancel();
                    return;
                }
                
                loc.getWorld().playSound(loc, sounds[index], 1.0f, 1.0f);
                index++;
            }
        }.runTaskTimer(plugin, 0L, (long)(delays[index] * 20));
    }
    
    /**
     * Play cinematic sound for ability
     */
    public static void playCinematicSound(Location loc, FistType fist) {
        switch(fist) {
            case ORB:
                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.5f);
                break;
            case BLOSSOM:
                loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1.8f);
                break;
            case BEAST:
                loc.getWorld().playSound(loc, Sound.ENTITY_RAVAGER_ROAR, 0.5f, 0.8f);
                break;
            case WATER:
                loc.getWorld().playSound(loc, Sound.ITEM_TRIDENT_THUNDER, 0.5f, 1.2f);
                break;
            case REALITY:
                loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.0f);
                break;
            case COSMIC:
                loc.getWorld().playSound(loc, Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 1.5f);
                break;
            case WOLF:
                loc.getWorld().playSound(loc, Sound.ENTITY_WOLF_HOWL, 0.5f, 0.8f);
                break;
            case BOMB:
                loc.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.5f, 1.2f);
                break;
            case VOID:
                loc.getWorld().playSound(loc, Sound.AMBIENT_CAVE, 0.5f, 0.5f);
                break;
            case PHANTOM:
                loc.getWorld().playSound(loc, Sound.AMBIENT_UNDERWATER_ENTER, 0.5f, 1.5f);
                break;
        }
    }
    
    /**
     * Play UI sound
     */
    public static void playUISound(Player player, UISoundType type) {
        switch(type) {
            case CLICK:
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                break;
            case SUCCESS:
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
                break;
            case ERROR:
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                break;
            case OPEN:
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.0f);
                break;
            case CLOSE:
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.0f);
                break;
            case SELECT:
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.5f);
                break;
        }
    }
    
    public enum UISoundType {
        CLICK, SUCCESS, ERROR, OPEN, CLOSE, SELECT
    }
    
    /**
     * Play ambient sound loop
     */
    public static void playAmbientSound(Player player, FistType fist) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFistManager().hasFist(player)) {
                    cancel();
                    return;
                }
                
                switch(fist) {
                    case ORB:
                        player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.2f, 1.0f);
                        break;
                    case WATER:
                        player.playSound(player.getLocation(), Sound.BLOCK_WATER_AMBIENT, 0.2f, 1.0f);
                        break;
                    case COSMIC:
                        player.playSound(player.getLocation(), Sound.AMBIENT_UNDERWATER_LOOP, 0.2f, 1.5f);
                        break;
                    case WOLF:
                        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_BREATHE, 0.2f, 1.0f);
                        break;
                }
            }
        }.runTaskTimer(plugin, 0L, 100L); // Every 5 seconds
    }
}
