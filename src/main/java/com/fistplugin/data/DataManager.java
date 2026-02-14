package com.fistplugin.data;

import com.fistplugin.FistPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataManager {
    
    private final FistPlugin plugin;
    private final File dataFolder;
    
    public DataManager(FistPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }
    
    public void savePlayerData(PlayerData data) {
        File file = new File(dataFolder, data.getUuid().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        if (data.getFistType() != null) {
            config.set("fist", data.getFistType().name());
        }
        config.set("last-fist-change", data.getLastFistChange());
        config.set("kills", data.getKills());
        config.set("deaths", data.getDeaths());
        config.set("abilities-used", data.getAbilitiesUsed());
        config.set("play-time", data.getPlayTime());
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save player data for " + data.getUuid());
        }
    }
    
    public PlayerData loadPlayerData(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        
        if (!file.exists()) {
            return new PlayerData(uuid);
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        PlayerData data = new PlayerData(uuid);
        
        String fistName = config.getString("fist");
        if (fistName != null) {
            try {
                data.setFistType(FistType.valueOf(fistName));
            } catch (IllegalArgumentException e) {
                // Invalid fist type, ignore
            }
        }
        
        data.setLastFistChange(config.getLong("last-fist-change", 0));
        data.setKills(config.getInt("kills", 0));
        data.setDeaths(config.getInt("deaths", 0));
        data.setAbilitiesUsed(config.getInt("abilities-used", 0));
        data.setPlayTime(config.getLong("play-time", 0));
        
        return data;
    }
    
    public void loadAllPlayerData() {
        // Load data for online players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerData data = loadPlayerData(player.getUniqueId());
            plugin.getFistManager().loadPlayerData(player.getUniqueId(), data);
        }
    }
    
    public void saveAllPlayerData() {
        // Save data for all online players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerData data = plugin.getFistManager().getPlayerData(player);
            if (data != null) {
                savePlayerData(data);
            }
        }
    }
}
