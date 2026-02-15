package com.fistplugin.utils;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class MessageUtils {
    
    private final FistPlugin plugin;
    private FileConfiguration messages;
    
    public MessageUtils(FistPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }
    
    public void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public String getMessage(String path) {
        String message = messages.getString(path);
        if (message == null) return "§cMessage not found: " + path;
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public String getMessage(String path, Placeholder... placeholders) {
        String message = getMessage(path);
        for (Placeholder p : placeholders) {
            message = message.replace(p.key, p.value);
        }
        return message;
    }
    
    public List<String> getMessageList(String path) {
        List<String> list = messages.getStringList(path);
        list.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line));
        return list;
    }
    
    public void sendMessage(Player player, String path) {
        player.sendMessage(getMessage("prefix") + getMessage(path));
    }
    
    public void sendMessage(Player player, String path, Placeholder... placeholders) {
        player.sendMessage(getMessage("prefix") + getMessage(path, placeholders));
    }
    
    public void sendMessageList(Player player, String path) {
        for (String line : getMessageList(path)) {
            player.sendMessage(line);
        }
    }
    
    public void broadcast(String path) {
        String message = getMessage("prefix") + getMessage(path);
        plugin.getServer().broadcastMessage(message);
    }
    
    public static class Placeholder {
        private final String key;
        private final String value;
        
        public Placeholder(String key, String value) {
            this.key = "%" + key + "%";
            this.value = value;
        }
        
        public static Placeholder of(String key, String value) {
            return new Placeholder(key, value);
        }
        
        public static Placeholder player(Player player) {
            return new Placeholder("player", player.getName());
        }
        
        public static Placeholder fist(FistType fist) {
            return new Placeholder("fist", fist != null ? fist.getDisplayName() : "None");
        }
        
        public static Placeholder number(String key, int number) {
            return new Placeholder(key, String.valueOf(number));
        }
        
        public static Placeholder time(String key, int seconds) {
            return new Placeholder(key, seconds + "s");
        }
    }
}
