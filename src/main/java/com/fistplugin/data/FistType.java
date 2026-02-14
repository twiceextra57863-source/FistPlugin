package com.fistplugin.data;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;

public enum FistType {
    
    ORB(
        "Orb Fist",
        ChatColor.GOLD,
        "§6Master of explosive orbs and boxing arenas",
        Particle.FLAME,
        Color.ORANGE,
        Sound.ENTITY_FIREWORK_ROCKET_BLAST,
        4, 120
    ),
    
    BLOSSOM(
        "Blossom Fist",
        ChatColor.LIGHT_PURPLE,
        "§dControl your enemies with nature's grasp",
        Particle.HAPPY_VILLAGER,
        Color.FUCHSIA,
        Sound.BLOCK_GRASS_BREAK,
        5, 60
    ),
    
    BEAST(
        "Beast Fist",
        ChatColor.RED,
        "§cSize matters - grow and shrink at will",
        Particle.ANGRY_VILLAGER,
        Color.RED,
        Sound.ENTITY_RAVAGER_ROAR,
        5, 30
    ),
    
    WATER(
        "Water Fist",
        ChatColor.AQUA,
        "§bRide the waves and summon tsunamis",
        Particle.FALLING_WATER,
        Color.BLUE,
        Sound.BLOCK_WATER_AMBIENT,
        20, 60
    ),
    
    REALITY(
        "Reality Fist",
        ChatColor.DARK_PURPLE,
        "§5Manipulate the terrain itself",
        Particle.BLOCK_CRACK,
        Color.PURPLE,
        Sound.BLOCK_STONE_BREAK,
        20, 60
    ),
    
    COSMIC(
        "Cosmic Fist",
        ChatColor.DARK_AQUA,
        "§3Control the gravity of your enemies",
        Particle.PORTAL,
        Color.TEAL,
        Sound.ENTITY_ENDER_DRAGON_FLAP,
        15, 45
    ),
    
    WOLF(
        "Wolf Fist",
        ChatColor.GRAY,
        "§7Hunt with your pack",
        Particle.CRIT,
        Color.SILVER,
        Sound.ENTITY_WOLF_GROWL,
        30, 120
    ),
    
    BOMB(
        "Bomb Fist",
        ChatColor.DARK_RED,
        "§4Explosive chaos and destruction",
        Particle.SMOKE_NORMAL,
        Color.MAROON,
        Sound.ENTITY_CREEPER_PRIMED,
        40, 120
    ),
    
    VOID(
        "Void Fist",
        ChatColor.DARK_GRAY,
        "§8Harness the power of nothingness",
        Particle.DRAGON_BREATH,
        Color.BLACK,
        Sound.AMBIENT_CAVE,
        25, 90
    ),
    
    PHANTOM(
        "Phantom Fist",
        ChatColor.WHITE,
        "§fPhase through reality",
        Particle.SPELL_INSTANT,
        Color.WHITE,
        Sound.ENTITY_PHANTOM_AMBIENT,
        15, 60
    );
    
    private final String displayName;
    private final ChatColor color;
    private final String lore;
    private final Particle particle;
    private final Color particleColor;
    private final Sound sound;
    private final int rightClickCooldown;
    private final int crouchClickCooldown;
    
    FistType(String displayName, ChatColor color, String lore, Particle particle, 
             Color particleColor, Sound sound, int rightClickCooldown, int crouchClickCooldown) {
        this.displayName = displayName;
        this.color = color;
        this.lore = lore;
        this.particle = particle;
        this.particleColor = particleColor;
        this.sound = sound;
        this.rightClickCooldown = rightClickCooldown;
        this.crouchClickCooldown = crouchClickCooldown;
    }
    
    public String getDisplayName() {
        return color + displayName;
    }
    
    public String getLore() {
        return lore;
    }
    
    public Particle getParticle() {
        return particle;
    }
    
    public Color getParticleColor() {
        return particleColor;
    }
    
    public Sound getSound() {
        return sound;
    }
    
    public int getRightClickCooldown() {
        return rightClickCooldown;
    }
    
    public int getCrouchClickCooldown() {
        return crouchClickCooldown;
    }
    
    public ChatColor getColor() {
        return color;
    }
}
