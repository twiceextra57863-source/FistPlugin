package com.fistplugin.utils;

import com.fistplugin.FistPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtils {
    
    private static FistPlugin plugin;
    private static YamlConfiguration messages;
    private static final Map<String, String> messageCache = new HashMap<>();
    
    public static void init(FistPlugin instance) {
        plugin = instance;
        reloadMessages();
    }
    
    /**
     * Reload messages from file
     */
    public static void reloadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        messageCache.clear();
    }
    
    /**
     * Get formatted message
     */
    public static String getMessage(String path, String... placeholders) {
        String cached = messageCache.get(path);
        String message;
        
        if (cached != null) {
            message = cached;
        } else {
            message = messages.getString(path);
            if (message == null) {
                message = "&cMessage not found: " + path;
            } else {
                message = ChatColor.translateAlternateColorCodes('&', message);
                messageCache.put(path, message);
            }
        }
        
        // Replace placeholders
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
            }
        }
        
        return message;
    }
    
    /**
     * Send message to player with prefix
     */
    public static void sendMessage(Player player, String path, String... placeholders) {
        String prefix = getMessage("prefix");
        String message = getMessage(path, placeholders);
        player.sendMessage(prefix + message);
    }
    
    /**
     * Send message without prefix
     */
    public static void sendRawMessage(Player player, String path, String... placeholders) {
        player.sendMessage(getMessage(path, placeholders));
    }
    
    /**
     * Send action bar message
     */
    public static void sendActionBar(Player player, String path, String... placeholders) {
        String message = getMessage(path, placeholders);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
    
    /**
     * Send title message
     */
    public static void sendTitle(Player player, String titlePath, String subtitlePath, 
                                 int fadeIn, int stay, int fadeOut, String... placeholders) {
        String title = getMessage(titlePath, placeholders);
        String subtitle = getMessage(subtitlePath, placeholders);
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
    
    /**
     * Get list of messages
     */
    public static List<String> getMessageList(String path, String... placeholders) {
        List<String> messagesList = messages.getStringList(path);
        
        // Replace placeholders
        for (int i = 0; i < messagesList.size(); i++) {
            String msg = messagesList.get(i);
            for (int j = 0; j < placeholders.length; j += 2) {
                if (j + 1 < placeholders.length) {
                    msg = msg.replace("%" + placeholders[j] + "%", placeholders[j + 1]);
                }
            }
            messagesList.set(i, ChatColor.translateAlternateColorCodes('&', msg));
        }
        
        return messagesList;
    }
    
    /**
     * Send multiple messages
     */
    public static void sendMessageList(Player player, String path, String... placeholders) {
        List<String> messagesList = getMessageList(path, placeholders);
        for (String msg : messagesList) {
            player.sendMessage(msg);
        }
    }
    
    /**
     * Broadcast message to all players
     */
    public static void broadcast(String path, String... placeholders) {
        String prefix = getMessage("prefix");
        String message = getMessage(path, placeholders);
        plugin.getServer().broadcastMessage(prefix + message);
    }
    
    /**
     * Format cooldown time
     */
    public static String formatCooldown(int seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            int minutes = seconds / 60;
            int secs = seconds % 60;
            return minutes + "m " + secs + "s";
        } else {
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            return hours + "h " + minutes + "m";
        }
    }
    
    /**
     * Create progress bar
     */
    public static String createProgressBar(int current, int max, int length, String filled, String empty) {
        StringBuilder bar = new StringBuilder();
        double percent = (double) current / max;
        int filledLength = (int) (length * percent);
        
        for (int i = 0; i < length; i++) {
            if (i < filledLength) {
                bar.append(filled);
            } else {
                bar.append(empty);
            }
        }
        
        return bar.toString();
    }
    
    /**
     * Format number with commas
     */
    public static String formatNumber(int number) {
        return String.format("%,d", number);
    }
    
    /**
     * Get ability description
     */
    public static String getAbilityDescription(FistType fist, boolean isCrouch) {
        String base = fist.getDisplayName() + "§7 - " + fist.getLore();
        String ability;
        
        if (isCrouch) {
            switch(fist) {
                case ORB:
                    ability = "\n§eCrouch+Right-click: §7Create boxing arena (15s)";
                    break;
                case BLOSSOM:
                    ability = "\n§eCrouch+Right-click: §7Freeze and poison target (10s)";
                    break;
                case BEAST:
                    ability = "\n§eCrouch+Right-click: §7Shrink yourself (10s)";
                    break;
                case WATER:
                    ability = "\n§eCrouch+Right-click: §7Summon tsunami (6s)";
                    break;
                case REALITY:
                    ability = "\n§eCrouch+Right-click: §7Summon meteor shower (4 meteors)";
                    break;
                case COSMIC:
                    ability = "\n§eCrouch+Right-click: §7Hook target, left click to launch";
                    break;
                case WOLF:
                    ability = "\n§eCrouch+Right-click: §7Summon wolf clones (10s)";
                    break;
                case BOMB:
                    ability = "\n§eCrouch+Right-click: §7Laser destruction (13s)";
                    break;
                case VOID:
                    ability = "\n§eCrouch+Right-click: §7Void nova explosion";
                    break;
                case PHANTOM:
                    ability = "\n§eCrouch+Right-click: §7Possess target (7s)";
                    break;
                default:
                    ability = "";
            }
        } else {
            switch(fist) {
                case ORB:
                    ability = "\n§eRight-click: §7Launch explosive fireball";
                    break;
                case BLOSSOM:
                    ability = "\n§eRight-click: §7Launch freezing projectile";
                    break;
                case BEAST:
                    ability = "\n§eRight-click: §7Growing damage over time";
                    break;
                case WATER:
                    ability = "\n§eRight-click: §6Fly for 6 seconds";
                    break;
                case REALITY:
                    ability = "\n§eRight-click: §7Raise terrain 5x5 area";
                    break;
                case COSMIC:
                    ability = "\n§eRight-click: §7Spin target for 2 seconds";
                    break;
                case WOLF:
                    ability = "\n§eRight-click: §7Dash forward and slash";
                    break;
                case BOMB:
                    ability = "\n§eRight-click: §7Ghost bomb chases target";
                    break;
                case VOID:
                    ability = "\n§eRight-click: §7Pull target towards you";
                    break;
                case PHANTOM:
                    ability = "\n§eRight-click: §7Phase through reality (2s)";
                    break;
                default:
                    ability = "";
            }
        }
        
        return base + ability;
    }
    
    /**
     * Format time
     */
    public static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
    
    /**
     * Center text
     */
    public static String centerText(String text) {
        int width = 320;
        int messageLength = text.length();
        int spaces = (width - messageLength) / 2;
        
        StringBuilder centered = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            centered.append(" ");
        }
        centered.append(text);
        
        return centered.toString();
    }
              }
