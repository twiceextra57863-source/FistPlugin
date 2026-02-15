package com.fistplugin.gui;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;

public class StatsGUI implements Listener {
    
    private final FistPlugin plugin;
    
    public StatsGUI(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void openStatsGUI(Player player, UUID targetUUID) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8⚡ Player Stats ⚡");
        
        // Fill background - CAN'T BE TAKEN
        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(" ");
        bg.setItemMeta(bgMeta);
        
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, bg.clone());
        }
        
        Player target = Bukkit.getPlayer(targetUUID);
        PlayerData data = plugin.getFistManager().getPlayerData(target != null ? target : player);
        
        if (target != null) {
            // Player head
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(target);
            headMeta.setDisplayName("§6§l" + target.getName());
            head.setItemMeta(headMeta);
            inv.setItem(13, head);
        }
        
        // Stats items - CAN'T BE TAKEN
        inv.setItem(20, createStatItem(Material.DIAMOND_SWORD, "§eKills", 
            Arrays.asList("§7Total: §f" + data.getKills()), false));
        
        inv.setItem(22, createStatItem(Material.REDSTONE, "§eDeaths", 
            Arrays.asList("§7Total: §f" + data.getDeaths()), false));
        
        double kd = data.getDeaths() > 0 ? 
            (double) data.getKills() / data.getDeaths() : data.getKills();
        
        inv.setItem(24, createStatItem(Material.NETHER_STAR, "§eK/D Ratio", 
            Arrays.asList("§7Ratio: §f" + String.format("%.2f", kd)), false));
        
        inv.setItem(29, createStatItem(Material.BLAZE_POWDER, "§eAbilities Used", 
            Arrays.asList("§7Total: §f" + data.getAbilitiesUsed()), false));
        
        long hours = data.getPlayTime() / 3600000;
        long minutes = (data.getPlayTime() % 3600000) / 60000;
        
        inv.setItem(33, createStatItem(Material.CLOCK, "§ePlay Time", 
            Arrays.asList("§7Hours: §f" + hours, "§7Minutes: §f" + minutes), false));
        
        if (data.getFistType() != null) {
            inv.setItem(31, createStatItem(Material.END_CRYSTAL, "§eCurrent Fist", 
                Arrays.asList(data.getFistType().getDisplayName(), "§7" + data.getFistType().getLore()), false));
        }
        
        player.openInventory(inv);
    }
    
    private ItemStack createStatItem(Material material, String name, java.util.List<String> lore, boolean canTake) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§8⚡ Player Stats ⚡") ||
            event.getView().getTitle().equals("§8⚡ Fist Spinner ⚡") ||
            event.getView().getTitle().equals("§8⚡ Select Your Fist ⚡")) {
            event.setCancelled(true); // CANCEL ALL CLICKS - ITEMS NAHI NIKALENGE
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals("§8⚡ Player Stats ⚡") ||
            event.getView().getTitle().equals("§8⚡ Fist Spinner ⚡") ||
            event.getView().getTitle().equals("§8⚡ Select Your Fist ⚡")) {
            event.setCancelled(true); // DRAG BHI CANCEL
        }
    }
}
