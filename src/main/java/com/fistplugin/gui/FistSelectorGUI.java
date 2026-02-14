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
    
    private static FistPlugin plugin;
    
    public FistSelectorGUI(FistPlugin plugin) {
        FistSelectorGUI.plugin = plugin;
    }
    
    public static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8⚡ Select Your Fist ⚡");
        
        // Fill background
        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(" ");
        bg.setItemMeta(bgMeta);
        
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, bg);
        }
        
        // Add fist icons
        FistType[] fists = FistType.values();
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 28, 29, 30, 31, 32, 33, 34};
        
        for (int i = 0; i < fists.length && i < slots.length; i++) {
            inv.setItem(slots[i], createFistIcon(fists[i]));
        }
        
        // Current fist display
        if (plugin.getFistManager().hasFist(player)) {
            FistType current = plugin.getFistManager().getPlayerFist(player);
            ItemStack currentItem = createFistIcon(current);
            ItemMeta meta = currentItem.getItemMeta();
            meta.setDisplayName("§a§lCurrent: " + current.getDisplayName());
            currentItem.setItemMeta(meta);
            inv.setItem(49, currentItem);
        }
        
        player.openInventory(inv);
    }
    
    private static ItemStack createFistIcon(FistType fist) {
        Material material = Material.PAPER;
        switch (fist) {
            case ORB: material = Material.FIRE_CHARGE; break;
            case BLOSSOM: material = Material.CHERRY_SAPLING; break;
            case BEAST: material = Material.BEEF; break;
            case WATER: material = Material.WATER_BUCKET; break;
            case REALITY: material = Material.END_STONE; break;
            case COSMIC: material = Material.ENDER_PEARL; break;
            case WOLF: material = Material.BONE; break;
            case BOMB: material = Material.TNT; break;
            case VOID: material = Material.OBSIDIAN; break;
            case PHANTOM: material = Material.PHANTOM_MEMBRANE; break;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(fist.getDisplayName());
        meta.setLore(Arrays.asList(
            "§7" + fist.getLore(),
            "",
            "§eClick to select this fist!"
        ));
        item.setItemMeta(meta);
        
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§8⚡ Select Your Fist ⚡")) return;
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        // Check if clicked on a fist
        for (FistType fist : FistType.values()) {
            if (clicked.getItemMeta().getDisplayName().equals(fist.getDisplayName())) {
                // Check cooldown for fist change (5 minutes)
                long lastChange = plugin.getFistManager().getPlayerData(player).getLastFistChange();
                long timeSince = System.currentTimeMillis() - lastChange;
                
                if (timeSince < 300000 && lastChange != 0) { // 5 minutes
                    int minutesLeft = (int) ((300000 - timeSince) / 60000);
                    int secondsLeft = (int) ((300000 - timeSince) / 1000) % 60;
                    player.sendMessage("§cYou can change fists again in " + minutesLeft + "m " + secondsLeft + "s!");
                    player.closeInventory();
                    return;
                }
                
                // Change fist
                plugin.getFistManager().setPlayerFist(player, fist);
                player.sendMessage("§a✅ You selected " + fist.getDisplayName());
                player.closeInventory();
                break;
            }
        }
    }
}
