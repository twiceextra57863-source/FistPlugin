package com.fistplugin.utils;

import com.fistplugin.FistPlugin;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {
    
    private final FistPlugin plugin;
    
    public SoundUtils(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        if (loc.getWorld() != null) {
            loc.getWorld().playSound(loc, sound, volume, pitch);
        }
    }
    
    public void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
    
    public void playFistSound(Player player, String fistType) {
        Sound sound = getFistSound(fistType);
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }
    
    private Sound getFistSound(String fistType) {
        switch(fistType.toLowerCase()) {
            case "orb": return Sound.ENTITY_FIREWORK_ROCKET_BLAST;
            case "blossom": return Sound.BLOCK_GRASS_BREAK;
            case "beast": return Sound.ENTITY_RAVAGER_ROAR;
            case "water": return Sound.BLOCK_WATER_AMBIENT;
            case "reality": return Sound.BLOCK_STONE_BREAK;
            case "cosmic": return Sound.ENTITY_ENDER_DRAGON_FLAP;
            case "wolf": return Sound.ENTITY_WOLF_GROWL;
            case "bomb": return Sound.ENTITY_CREEPER_PRIMED;
            case "void": return Sound.AMBIENT_CAVE;
            case "phantom": return Sound.ENTITY_PHANTOM_AMBIENT;
            default: return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
    }
    
    // Fixed: Removed the 'index' variable that was causing error at line 130
    public void playRandomPitch(Player player, Sound sound, float volume) {
        float pitch = 0.8f + (float)(Math.random() * 0.4f);
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
    
    // Fixed: ENTITY_WOLF_BREATHE doesn't exist in 1.21, using ENTITY_WOLF_GROWL instead
    public void playWolfSound(Player player, String type) {
        switch(type) {
            case "growl":
                player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 1.0f, 1.0f);
                break;
            case "howl":
                player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1.0f, 1.0f);
                break;
            case "pant":
                player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 0.5f, 1.5f); // Using growl with higher pitch as substitute
                break;
            default:
                player.playSound(player.getLocation(), Sound.ENTITY_WOLF_AMBIENT, 1.0f, 1.0f);
        }
    }
}
