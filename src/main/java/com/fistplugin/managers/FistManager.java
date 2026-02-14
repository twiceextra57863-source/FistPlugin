package com.fistplugin.managers;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import com.fistplugin.data.PlayerData;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FistManager {
    
    private final FistPlugin plugin;
    private final Map<UUID, FistType> playerFists;
    private final Map<UUID, PlayerData> playerDataMap;
    
    public FistManager(FistPlugin plugin) {
        this.plugin = plugin;
        this.playerFists = new HashMap<>();
        this.playerDataMap = new HashMap<>();
    }
    
    public void setPlayerFist(Player player, FistType fist) {
        playerFists.put(player.getUniqueId(), fist);
        
        // Create or update player data
        PlayerData data = playerDataMap.getOrDefault(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        data.setFistType(fist);
        data.setLastFistChange(System.currentTimeMillis());
        playerDataMap.put(player.getUniqueId(), data);
        
        // Save to disk
        plugin.getDataManager().savePlayerData(data);
    }
    
    public FistType getPlayerFist(Player player) {
        return playerFists.get(player.getUniqueId());
    }
    
    public boolean hasFist(Player player) {
        return playerFists.containsKey(player.getUniqueId());
    }
    
    public void removeFist(Player player) {
        playerFists.remove(player.getUniqueId());
        
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data != null) {
            data.setFistType(null);
            plugin.getDataManager().savePlayerData(data);
        }
    }
    
    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), 
            k -> plugin.getDataManager().loadPlayerData(player.getUniqueId()));
    }
    
    public void loadPlayerData(UUID uuid, PlayerData data) {
        playerDataMap.put(uuid, data);
        if (data.getFistType() != null) {
            playerFists.put(uuid, data.getFistType());
        }
    }
    
    public void unloadPlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
        playerFists.remove(uuid);
    }
}
