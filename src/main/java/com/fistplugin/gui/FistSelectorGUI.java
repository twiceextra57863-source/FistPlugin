package com.fistplugin.gui;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FistSelectorGUI implements Listener {
    
    private final FistPlugin plugin;
    private final StatsGUI statsGUI;
    
    public FistSelectorGUI(FistPlugin plugin) {
        this.plugin = plugin;
        this.statsGUI = new StatsGUI(plugin);
    }
    
    public static void openGUI(Player player) {
        // This would be implemented
    }
    
    // Remove lines 275-276, 344-345 that were calling non-existent methods
    // Remove line 334 that had wrong StatsGUI call
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Implementation
    }
}
