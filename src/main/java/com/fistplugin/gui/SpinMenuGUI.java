package com.fistplugin.gui;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Random;

public class SpinMenuGUI implements Listener {
    
    private static FistPlugin plugin;
    private final Random random = new Random(); // Made non-static
    
    public SpinMenuGUI(FistPlugin plugin) {
        SpinMenuGUI.plugin = plugin;
    }
    
    public static void openSpinMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8⚡ §6§lCASINO SPIN §8⚡");
        
        // Decorative border
        ItemStack goldBorder = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta goldMeta = goldBorder.getItemMeta();
        goldMeta.setDisplayName("§6✧");
        goldBorder.setItemMeta(goldMeta);
        
        ItemStack diamondBorder = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta diamondMeta = diamondBorder.getItemMeta();
        diamondMeta.setDisplayName("§b✧");
        diamondBorder.setItemMeta(diamondMeta);
        
        // Fill with fancy pattern
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                if (i % 2 == 0) {
                    inv.setItem(i, goldBorder.clone());
                } else {
                    inv.setItem(i, diamondBorder.clone());
                }
            } else {
                ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta glassMeta = glass.getItemMeta();
                glassMeta.setDisplayName(" ");
                glass.setItemMeta(glassMeta);
                inv.setItem(i, glass);
            }
        }
        
        // Center display
        ItemStack center = new ItemStack(Material.END_CRYSTAL);
        ItemMeta centerMeta = center.getItemMeta();
        centerMeta.setDisplayName("§6§l✨ SPIN TO WIN ✨");
        centerMeta.setLore(Arrays.asList(
            "§7Click to start the",
            "§7magical spin wheel!"
        ));
        center.setItemMeta(centerMeta);
        inv.setItem(22, center);
        
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }
    
    public void startSpinAnimation(Player player) { // Made non-static
        Inventory inv = Bukkit.createInventory(null, 54, "§8⚡ §6§lSPINNING... §8⚡");
        
        // All fists icons for spinning
        FistType[] fists = FistType.values();
        
        // Create fancy spinning display
        for (int i = 0; i < 54; i++) {
            if (i >= 18 && i <= 35 && i % 9 != 0 && i % 9 != 8) {
                // Random fist for spinning slots
                FistType randomFist = fists[random.nextInt(fists.length)];
                inv.setItem(i, createFistIcon(randomFist, true));
            } else {
                ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta glassMeta = glass.getItemMeta();
                glassMeta.setDisplayName(" ");
                glass.setItemMeta(glassMeta);
                inv.setItem(i, glass);
            }
        }
        
        player.openInventory(inv);
        
        // Spin animation - 3 seconds
        new BukkitRunnable() {
            int ticks = 0;
            int spinSpeed = 2;
            
            @Override
            public void run() {
                if (ticks >= 60) { // 3 seconds
                    // Stop spinning and show result
                    showSpinResult(player, fists);
                    cancel();
                    return;
                }
                
                // Update spinning slots
                for (int i = 19; i <= 34; i++) {
                    if (i % 9 != 0 && i % 9 != 8) {
                        FistType randomFist = fists[random.nextInt(fists.length)];
                        inv.setItem(i, createFistIcon(randomFist, true));
                    }
                }
                
                // Spin sound
                if (ticks % 4 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1.0f + (ticks / 60.0f));
                }
                
                ticks += spinSpeed;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    private void showSpinResult(Player player, FistType[] fists) { // Added fists parameter
        Inventory inv = Bukkit.createInventory(null, 54, "§8⚡ §6§lYOUR PRIZE §8⚡");
        
        // Decorative border
        ItemStack goldBorder = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta goldMeta = goldBorder.getItemMeta();
        goldMeta.setDisplayName("§6✧");
        goldBorder.setItemMeta(goldMeta);
        
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, goldBorder.clone());
            } else {
                ItemStack glass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
                ItemMeta glassMeta = glass.getItemMeta();
                glassMeta.setDisplayName(" ");
                glass.setItemMeta(glassMeta);
                inv.setItem(i, glass);
            }
        }
        
        // Get random fist
        FistType result = fists[random.nextInt(fists.length)];
        
        // Set result in center
        inv.setItem(22, createFistIcon(result, false));
        
        // Add celebration items
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta fireMeta = firework.getItemMeta();
        fireMeta.setDisplayName("§a§lCONGRATULATIONS!");
        fireMeta.setLore(Arrays.asList(
            "§7You received:",
            result.getDisplayName(),
            "§7" + result.getLore()
        ));
        firework.setItemMeta(fireMeta);
        inv.setItem(31, firework);
        
        player.openInventory(inv);
        
        // Give fist to player
        plugin.getFistManager().setPlayerFist(player, result);
        
        // Epic effects
        player.getWorld().playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 50, 1, 1, 1, 0.1);
        
        player.sendMessage(" ");
        player.sendMessage("§8§m✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧");
        player.sendMessage("§6§l    ✨ CONGRATULATIONS! ✨");
        player.sendMessage("§8§m✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧");
        player.sendMessage("§a    You received: " + result.getDisplayName());
        player.sendMessage("§7    " + result.getLore());
        player.sendMessage("§8§m✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧✦✧");
        player.sendMessage(" ");
        
        // Close after 5 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTaskLater(plugin, 100L);
    }
    
    private static ItemStack createFistIcon(FistType fist, boolean small) {
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
        
        if (small) {
            meta.setDisplayName("§f" + fist.getDisplayName());
        } else {
            meta.setDisplayName(fist.getDisplayName());
            meta.setLore(Arrays.asList(
                "§7" + fist.getLore(),
                "",
                "§eRight-click: §fSummon projectile",
                "§eCrouch+Right-click: §fSpecial ability"
            ));
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        
        if (title.contains("CASINO SPIN") || title.contains("SPINNING") || title.contains("YOUR PRIZE")) {
            event.setCancelled(true);
            
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                
                // Start spin on center click
                if (title.contains("CASINO SPIN") && event.getRawSlot() == 22) {
                    startSpinAnimation(player);
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("CASINO SPIN") || title.contains("SPINNING") || title.contains("YOUR PRIZE")) {
            event.setCancelled(true);
        }
    }
}
