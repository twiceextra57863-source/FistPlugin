package com.fistplugin.managers;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import com.fistplugin.abilities.*;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class AbilityManager {
    
    private final FistPlugin plugin;
    private final Map<FistType, Ability> abilities;
    
    public AbilityManager(FistPlugin plugin) {
        this.plugin = plugin;
        this.abilities = new HashMap<>();
        
        // Register all abilities
        registerAbilities();
    }
    
    private void registerAbilities() {
        abilities.put(FistType.ORB, new OrbFist(plugin));
        abilities.put(FistType.BLOSSOM, new BlossomFist(plugin));
        abilities.put(FistType.BEAST, new BeastFist(plugin));
        abilities.put(FistType.WATER, new WaterFist(plugin));
        abilities.put(FistType.REALITY, new RealityFist(plugin));
        abilities.put(FistType.COSMIC, new CosmicFist(plugin));
        abilities.put(FistType.WOLF, new WolfFist(plugin));
        abilities.put(FistType.BOMB, new BombFist(plugin));
        abilities.put(FistType.VOID, new VoidFist(plugin));
        abilities.put(FistType.PHANTOM, new PhantomFist(plugin));
    }
    
    public boolean executeRightClick(Player player, FistType fist) {
        Ability ability = abilities.get(fist);
        if (ability != null) {
            return ability.onRightClick(player);
        }
        return false;
    }
    
    public boolean executeCrouchRightClick(Player player, FistType fist) {
        Ability ability = abilities.get(fist);
        if (ability != null) {
            return ability.onCrouchRightClick(player);
        }
        return false;
    }
    
    public interface Ability {
        boolean onRightClick(Player player);
        boolean onCrouchRightClick(Player player);
        String getName();
        String getDescription();
    }
}
