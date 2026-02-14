package com.fistplugin.listeners;

import com.fistplugin.FistPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackListener implements Listener {
    
    private final FistPlugin plugin;
    
    public ResourcePackListener(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Send resource pack if enabled
        if (plugin.getConfig().getBoolean("resource-pack.enabled")) {
            String url = plugin.getConfig().getString("resource-pack.url");
            if (url != null && !url.isEmpty()) {
                player.setResourcePack(url);
            }
        }
    }
    
    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        
        switch (event.getStatus()) {
            case SUCCESSFULLY_LOADED:
                player.sendMessage("§a✅ Resource pack loaded! Fist icons enabled!");
                break;
            case DECLINED:
                player.sendMessage("§c⚠ Resource pack declined. Fists will show as §e∆");
                break;
            case FAILED_DOWNLOAD:
                player.sendMessage("§c⚠ Resource pack download failed. Fists will show as §e∆");
                break;
        }
    }
}
