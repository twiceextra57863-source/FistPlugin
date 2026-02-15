package com.fistplugin.gui;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;

public class StatsGUI {
    
    private final FistPlugin plugin;
    
    public StatsGUI(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void openStatsGUI(Player player, UUID targetUUID) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8⚡ Player Stats ⚡");
        
        // Fill background
        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(" ");
        bg.setItemMeta(bgMeta);
        
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, bg);
        }
        
        Player target = Bukkit.getPlayer(targetUUID);
        PlayerData data = plugin.getFistManager().getPlayerData(target != null ? target : targetUUID);
        
        if (target != null) {
            // Player head
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(target);
            headMeta.setDisplayName("§6§l" + target.getName());
            head.setItemMeta(headMeta);
            inv.setItem(13, head);
        }
        
        // Stats display
        inv.setItem(20, createStatItem(Material.DIAMOND_SWORD, "§eKills", 
            Arrays.asList("§7Total: §f" + data.getKills())));
        
        inv.setItem(22, createStatItem(Material.REDSTONE, "§eDeaths", 
            Arrays.asList("§7Total: §f" + data.getDeaths())));
        
        double kd = data.getDeaths() > 0 ? 
            (double) data.getKills() / data.getDeaths() : data.getKills();
        
        inv.setItem(24, createStatItem(Material.NETHER_STAR, "§eK/D Ratio", 
            Arrays.asList("§7Ratio: §f" + String.format("%.2f", kd))));
        
        inv.setItem(29, createStatItem(Material.BLAZE_POWDER, "§eAbilities Used", 
            Arrays.asList("§7Total: §f" + data.getAbilitiesUsed())));
        
        long hours = data.getPlayTime() / 3600000;
        long minutes = (data.getPlayTime() % 3600000) / 60000;
        
        inv.setItem(33, createStatItem(Material.CLOCK, "§ePlay Time", 
            Arrays.asList("§7Hours: §f" + hours, "§7Minutes: §f" + minutes)));
        
        if (data.getFistType() != null) {
            inv.setItem(31, createStatItem(Material.END_CRYSTAL, "§eCurrent Fist", 
                Arrays.asList(data.getFistType().getDisplayName(), "§7" + data.getFistType().getLore())));
        }
        
        player.openInventory(inv);
    }
    
    private ItemStack createStatItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
