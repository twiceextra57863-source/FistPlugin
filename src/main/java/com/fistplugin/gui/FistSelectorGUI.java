package com.fistplugin.gui;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FistSelectorGUI implements Listener {
    
    private static FistPlugin plugin;
    private final Map<UUID, Long> lastClickTime = new HashMap<>();
    
    public FistSelectorGUI(FistPlugin plugin) {
        FistSelectorGUI.plugin = plugin;
    }
    
    public static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8⚡ SELECT YOUR FIST ⚡");
        
        // Decorative border
        ItemStack border = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName("§5✧");
        border.setItemMeta(borderMeta);
        
        // Fill border
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border.clone());
            }
        }
        
        // Add fist icons in a nice pattern
        FistType[] fists = FistType.values();
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        
        for (int i = 0; i < fists.length && i < slots.length; i++) {
            inv.setItem(slots[i], createFistIcon(fists[i]));
        }
        
        // Admin section (only visible to ops)
        if (player.isOp() || player.hasPermission("fist.admin")) {
            ItemStack adminGive = new ItemStack(Material.COMMAND_BLOCK);
            ItemMeta adminMeta = adminGive.getItemMeta();
            adminMeta.setDisplayName("§c§lADMIN GIVE");
            adminMeta.setLore(Arrays.asList(
                "§7Click on any fist above",
                "§7to give it instantly!",
                "§c§lNO COOLDOWN"
            ));
            adminGive.setItemMeta(adminMeta);
            inv.setItem(49, adminGive);
            
            ItemStack info = new ItemStack(Material.PAPER);
            ItemMeta infoMeta = info.getItemMeta();
            infoMeta.setDisplayName("§6§lADMIN MODE ACTIVE");
            infoMeta.setLore(Arrays.asList(
                "§7You can give any fist",
                "§7by clicking on it!"
            ));
            info.setItemMeta(infoMeta);
            inv.setItem(40, info);
        }
        
        // Current fist display
        if (plugin.getFistManager().hasFist(player)) {
            FistType current = plugin.getFistManager().getPlayerFist(player);
            ItemStack currentItem = createFistIcon(current);
            ItemMeta meta = currentItem.getItemMeta();
            meta.setDisplayName("§a§lCURRENT: " + current.getDisplayName());
            meta.setLore(Arrays.asList(
                "§7" + current.getLore(),
                "",
                "§eThis is your active fist"
            ));
            currentItem.setItemMeta(meta);
            inv.setItem(4, currentItem);
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
            "§eRight-click: §f" + fist.getRightClickCooldown() + "s cooldown",
            "§eCrouch+Right-click: §f" + fist.getCrouchClickCooldown() + "s cooldown",
            "",
            "§aClick to select this fist!"
        ));
        item.setItemMeta(meta);
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§8⚡ SELECT YOUR FIST ⚡")) return;
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        // Check for admin give mode
        boolean adminMode = false;
        if (event.getInventory().getItem(49) != null && 
            event.getInventory().getItem(49).getType() == Material.COMMAND_BLOCK) {
            adminMode = true;
        }
        
        // Check if clicked on a fist
        for (FistType fist : FistType.values()) {
            if (clicked.getItemMeta().getDisplayName().equals(fist.getDisplayName())) {
                
                if (adminMode && (player.isOp() || player.hasPermission("fist.admin"))) {
                    // Admin give - no cooldown
                    plugin.getFistManager().setPlayerFist(player, fist);
                    player.sendMessage("§c§l[ADMIN] §aGave " + fist.getDisplayName() + " instantly!");
                    player.closeInventory();
                    
                    // Admin effect
                    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
                    player.getWorld().spawnParticle(org.bukkit.Particle.FLASH, player.getLocation(), 1, 0, 0, 0, 0);
                    return;
                }
                
                // Normal player - check cooldown
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
                
                // Selection effect
                player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.getWorld().spawnParticle(org.bukkit.Particle.END_ROD, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
                
                player.closeInventory();
                break;
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals("§8⚡ SELECT YOUR FIST ⚡")) {
            event.setCancelled(true);
        }
    }
                    }
