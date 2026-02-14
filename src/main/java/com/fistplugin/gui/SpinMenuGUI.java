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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class SpinMenuGUI implements Listener {
    
    private static FistPlugin plugin;
    
    public SpinMenuGUI(FistPlugin plugin) {
        SpinMenuGUI.plugin = plugin;
    }
    
    public static void openSpinMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8⚡ Fist Spinner ⚡");
        
        // Fill with glass panes
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, glass);
        }
        
        // Center item (spinning)
        ItemStack center = new ItemStack(Material.END_CRYSTAL);
        ItemMeta centerMeta = center.getItemMeta();
        centerMeta.setDisplayName("§6§lSPINNING...");
        centerMeta.setLore(Arrays.asList("§7Getting your random fist!"));
        center.setItemMeta(centerMeta);
        inv.setItem(22, center);
        
        player.openInventory(inv);
        
        // Spin animation
        new BukkitRunnable() {
            int ticks = 0;
            int currentSlot = 19;
            
            @Override
            public void run() {
                if (ticks >= 60) { // 3 seconds
                    // Give random fist
                    FistType[] fists = FistType.values();
                    FistType randomFist = fists[(int)(Math.random() * fists.length)];
                    
                    plugin.getFistManager().setPlayerFist(player, randomFist);
                    
                    // Show result
                    ItemStack result = createFistIcon(randomFist);
                    inv.setItem(22, result);
                    
                    player.sendMessage("§a§l✦ You received: " + randomFist.getDisplayName());
                    player.sendMessage("§7" + randomFist.getLore());
                    
                    // Close after 2 seconds
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.closeInventory();
                        }
                    }.runTaskLater(plugin, 40L);
                    
                    cancel();
                    return;
                }
                
                // Clear previous
                if (ticks % 4 == 0) {
                    int prevSlot = currentSlot - 1;
                    if (prevSlot >= 19) {
                        inv.setItem(prevSlot, glass);
                    }
                }
                
                // Update spinning item
                FistType currentFist = FistType.values()[(ticks / 4) % FistType.values().length];
                ItemStack spinningItem = createFistIcon(currentFist);
                inv.setItem(currentSlot, spinningItem);
                
                // Move to next slot
                currentSlot++;
                if (currentSlot > 34) {
                    currentSlot = 19;
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
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
            "§eRight-click: §fSummon projectile",
            "§eCrouch+Right-click: §fSpecial ability"
        ));
        item.setItemMeta(meta);
        
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§8⚡ Fist Spinner ⚡")) {
            event.setCancelled(true);
        }
    }
}
