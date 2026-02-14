package com.fistplugin.gui;

import com.fistplugin.FistPlugin;
import com.fistplugin.data.FistType;
import com.fistplugin.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FistSelectorGUI implements Listener {
    
    private final FistPlugin plugin;
    private final Map<UUID, Long> lastSelectionTime = new HashMap<>();
    
    // GUI Constants
    private final String GUI_TITLE = "§8⚡ Select Your Fist ⚡";
    private final int GUI_SIZE = 54;
    
    public FistSelectorGUI(FistPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open the fist selector GUI for a player
     */
    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
        
        // Fill background
        fillBackground(inv);
        
        // Add all fist icons
        addFistIcons(inv);
        
        // Add current fist display
        addCurrentFistDisplay(inv, player);
        
        // Add stats button
        addStatsButton(inv);
        
        // Add info button
        addInfoButton(inv);
        
        // Add close button
        addCloseButton(inv);
        
        player.openInventory(inv);
    }
    
    /**
     * Fill inventory background with glass panes
     */
    private void fillBackground(Inventory inv) {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        // Fill all slots with glass first
        for (int i = 0; i < GUI_SIZE; i++) {
            inv.setItem(i, glass);
        }
        
        // Add decorative borders
        ItemStack borderGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = borderGlass.getItemMeta();
        borderMeta.setDisplayName(" ");
        borderGlass.setItemMeta(borderMeta);
        
        // Top row
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, borderGlass);
        }
        
        // Bottom row
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, borderGlass);
        }
        
        // Side borders
        inv.setItem(9, borderGlass);
        inv.setItem(17, borderGlass);
        inv.setItem(18, borderGlass);
        inv.setItem(26, borderGlass);
        inv.setItem(27, borderGlass);
        inv.setItem(35, borderGlass);
        inv.setItem(36, borderGlass);
        inv.setItem(44, borderGlass);
    }
    
    /**
     * Add all fist icons to the GUI
     */
    private void addFistIcons(Inventory inv) {
        FistType[] fists = FistType.values();
        
        // Slot positions for fists (3 rows of 7)
        int[][] slotPositions = {
            {19, 20, 21, 22, 23, 24, 25},  // Row 1
            {28, 29, 30, 31, 32, 33, 34},  // Row 2
            {37, 38, 39, 40, 41, 42, 43}   // Row 3
        };
        
        int fistIndex = 0;
        for (int row = 0; row < slotPositions.length && fistIndex < fists.length; row++) {
            for (int col = 0; col < slotPositions[row].length && fistIndex < fists.length; col++) {
                int slot = slotPositions[row][col];
                FistType fist = fists[fistIndex];
                inv.setItem(slot, createFistIcon(fist));
                fistIndex++;
            }
        }
    }
    
    /**
     * Create an icon for a fist type
     */
    private ItemStack createFistIcon(FistType fist) {
        Material material = getFistMaterial(fist);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(fist.getDisplayName());
        
        List<String> lore = new ArrayList<>();
        lore.add("§7" + fist.getLore());
        lore.add("");
        lore.add("§e§lABILITIES:");
        lore.add("§e• Right Click: §7" + getRightClickDescription(fist));
        lore.add("§e• Crouch + Right Click: §7" + getCrouchClickDescription(fist));
        lore.add("");
        lore.add("§7Cooldowns:");
        lore.add("§8  Right: §e" + fist.getRightClickCooldown() + "s  §8Crouch: §e" + fist.getCrouchClickCooldown() + "s");
        lore.add("");
        lore.add("§aClick to select this fist!");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * Get right click ability description
     */
    private String getRightClickDescription(FistType fist) {
        switch(fist) {
            case ORB: return "Launch explosive fireball";
            case BLOSSOM: return "Launch freezing projectile";
            case BEAST: return "Growing damage over time";
            case WATER: return "Fly for 6 seconds";
            case REALITY: return "Raise terrain 5x5 area";
            case COSMIC: return "Spin target for 2 seconds";
            case WOLF: return "Dash forward and slash";
            case BOMB: return "Ghost bomb chases target";
            case VOID: return "Pull target towards you";
            case PHANTOM: return "Phase through reality (2s)";
            default: return "Unknown ability";
        }
    }
    
    /**
     * Get crouch click ability description
     */
    private String getCrouchClickDescription(FistType fist) {
        switch(fist) {
            case ORB: return "Create boxing arena (15s)";
            case BLOSSOM: return "Freeze and poison target (10s)";
            case BEAST: return "Shrink yourself (10s)";
            case WATER: return "Summon tsunami (6s)";
            case REALITY: return "Summon meteor shower (4 meteors)";
            case COSMIC: return "Hook target, left click to launch";
            case WOLF: return "Summon wolf clones (10s)";
            case BOMB: return "Laser destruction (13s)";
            case VOID: return "Void nova explosion";
            case PHANTOM: return "Possess target (7s)";
            default: return "Unknown ability";
        }
    }
    
    /**
     * Get material for fist icon
     */
    private Material getFistMaterial(FistType fist) {
        switch(fist) {
            case ORB: return Material.FIRE_CHARGE;
            case BLOSSOM: return Material.CHERRY_SAPLING;
            case BEAST: return Material.BEEF;
            case WATER: return Material.WATER_BUCKET;
            case REALITY: return Material.END_STONE;
            case COSMIC: return Material.ENDER_PEARL;
            case WOLF: return Material.BONE;
            case BOMB: return Material.TNT;
            case VOID: return Material.OBSIDIAN;
            case PHANTOM: return Material.PHANTOM_MEMBRANE;
            default: return Material.PAPER;
        }
    }
    
    /**
     * Add current fist display
     */
    private void addCurrentFistDisplay(Inventory inv, Player player) {
        ItemStack item;
        
        if (plugin.getFistManager().hasFist(player)) {
            FistType current = plugin.getFistManager().getPlayerFist(player);
            item = createFistIcon(current);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a§l✔ CURRENT FIST: " + current.getDisplayName());
            
            List<String> lore = new ArrayList<>(meta.getLore());
            lore.add(0, "§8§m+----------------------+");
            lore.add("§eThis is your active fist!");
            lore.add("§8§m+----------------------+");
            meta.setLore(lore);
            item.setItemMeta(meta);
        } else {
            item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§c§l❌ NO FIST EQUIPPED");
            meta.setLore(Arrays.asList(
                "§8§m+----------------------+",
                "§7You don't have a fist yet!",
                "§7Select one from above to",
                "§7begin your journey!",
                "§8§m+----------------------+"
            ));
            item.setItemMeta(meta);
        }
        
        inv.setItem(13, item);
    }
    
    /**
     * Add stats button
     */
    private void addStatsButton(Inventory inv) {
        ItemStack statsButton = new ItemStack(Material.NETHER_STAR);
        ItemMeta statsMeta = statsButton.getItemMeta();
        statsMeta.setDisplayName("§b📊 Your Stats");
        statsMeta.setLore(Arrays.asList(
            "§8§m+----------------------+",
            "§7Click to view your",
            "§7fist statistics and",
            "§7leaderboard rankings!",
            "§8§m+----------------------+",
            "§e✦ Kills, Deaths, K/D",
            "§e✦ Abilities Used",
            "§e✦ Play Time",
            "§8§m+----------------------+"
        ));
        statsButton.setItemMeta(statsMeta);
        inv.setItem(49, statsButton);
    }
    
    /**
     * Add info button
     */
    private void addInfoButton(Inventory inv) {
        ItemStack infoButton = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoButton.getItemMeta();
        infoMeta.setDisplayName("§eℹ️ Fist Information");
        infoMeta.setLore(Arrays.asList(
            "§8§m+----------------------+",
            "§7About FistPlugin:",
            "§7Version: §e" + plugin.getPluginVersion(),
            "§7Author: §e" + plugin.getPluginAuthor(),
            "§8§m+----------------------+",
            "§e• 10 Unique Fists",
            "§e• 2 Abilities per Fist",
            "§e• Custom Particles & Sounds",
            "§e• Stats Tracking",
            "§8§m+----------------------+"
        ));
        infoButton.setItemMeta(infoMeta);
        inv.setItem(51, infoButton);
    }
    
    /**
     * Add close button
     */
    private void addCloseButton(Inventory inv) {
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName("§c✖ Close Menu");
        closeMeta.setLore(Arrays.asList(
            "§8§m+----------------------+",
            "§7Click to close the menu",
            "§8§m+----------------------+"
        ));
        closeButton.setItemMeta(closeMeta);
        inv.setItem(53, closeButton);
    }
    
    /**
     * Handle inventory clicks
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        String displayName = clicked.getItemMeta().getDisplayName();
        
        // Handle close button
        if (displayName.contains("Close")) {
            player.closeInventory();
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            return;
        }
        
        // Handle stats button
        if (displayName.contains("Your Stats")) {
            player.closeInventory();
            StatsGUI statsGUI = new StatsGUI(plugin);
            statsGUI.openStatsGUI(player);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            return;
        }
        
        // Handle info button
        if (displayName.contains("Fist Information")) {
            player.sendMessage("§8§m+----------------------------+");
            player.sendMessage("§6§lFistPlugin Information");
            player.sendMessage("§8§m+----------------------------+");
            player.sendMessage("§eVersion: §f" + plugin.getPluginVersion());
            player.sendMessage("§eAuthor: §f" + plugin.getPluginAuthor());
            player.sendMessage("§eFists: §f10 Unique Fists");
            player.sendMessage("§eAbilities: §f20 Total Abilities");
            player.sendMessage("§8§m+----------------------------+");
            player.sendMessage("§7Use §e/fist help §7for commands");
            player.sendMessage("§8§m+----------------------------+");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            return;
        }
        
        // Handle fist selection
        for (FistType fist : FistType.values()) {
            if (displayName.contains(fist.getDisplayName())) {
                selectFist(player, fist);
                break;
            }
        }
    }
    
    /**
     * Handle fist selection
     */
    private void selectFist(Player player, FistType fist) {
        // Check cooldown for fist change (5 minutes)
        if (lastSelectionTime.containsKey(player.getUniqueId())) {
            long lastSelect = lastSelectionTime.get(player.getUniqueId());
            long timeSince = System.currentTimeMillis() - lastSelect;
            long cooldownTime = plugin.getConfig().getInt("fist-switch-cooldown", 300) * 1000L;
            
            if (timeSince < cooldownTime && lastSelect != 0) {
                long remaining = (cooldownTime - timeSince) / 1000;
                int minutes = (int) (remaining / 60);
                int seconds = (int) (remaining % 60);
                
                player.sendMessage("§c⏳ You can change fists again in " + minutes + "m " + seconds + "s!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
        }
        
        // Check if they already have this fist
        if (plugin.getFistManager().hasFist(player)) {
            FistType current = plugin.getFistManager().getPlayerFist(player);
            if (current == fist) {
                player.sendMessage("§cYou already have this fist equipped!");
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
        }
        
        // Set new fist
        plugin.getFistManager().setPlayerFist(player, fist);
        lastSelectionTime.put(player.getUniqueId(), System.currentTimeMillis());
        
        // Success message
        player.sendMessage("§a§l✦ You selected " + fist.getDisplayName() + "§a§l!");
        player.sendMessage("§7" + fist.getLore());
        
        // Play effects
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.getWorld().spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING, 
            player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.5);
        
        // Close inventory
        player.closeInventory();
    }
                                          }
