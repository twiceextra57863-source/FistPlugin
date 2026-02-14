package com.fistplugin;

import com.fistplugin.managers.*;
import com.fistplugin.listeners.*;
import com.fistplugin.commands.*;
import com.fistplugin.data.DataManager;
import com.fistplugin.gui.SpinMenuGUI;
import com.fistplugin.gui.FistSelectorGUI;
import com.fistplugin.gui.StatsGUI;
import com.fistplugin.utils.*;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.logging.Level;

public class FistPlugin extends JavaPlugin {
    
    private static FistPlugin instance;
    
    // Managers
    private FistManager fistManager;
    private CooldownManager cooldownManager;
    private ParticleManager particleManager;
    private AbilityManager abilityManager;
    private DataManager dataManager;
    
    // Plugin info
    private final String PLUGIN_NAME = "§6FistPlugin";
    private final String PLUGIN_VERSION = "1.0.0";
    private final String PLUGIN_AUTHOR = "YourName";
    
    @Override
    public void onEnable() {
        // Set instance
        instance = this;
        
        // Startup message
        log("§8§m+----------------------------+");
        log("§6" + PLUGIN_NAME + " §ev" + PLUGIN_VERSION);
        log("§7Author: §f" + PLUGIN_AUTHOR);
        log("§8§m+----------------------------+");
        
        // Check server version
        checkServerVersion();
        
        // Save default configs
        saveDefaultConfigs();
        
        // Initialize utils
        initializeUtils();
        
        // Initialize managers
        initializeManagers();
        
        // Register listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        // Load all player data
        loadPlayerData();
        
        // Start metrics (if enabled)
        startMetrics();
        
        // Success message
        log("§a✅ " + PLUGIN_NAME + " v" + PLUGIN_VERSION + " enabled successfully!");
        log("§e👊 10 Unique Fists loaded with " + getTotalAbilities() + " abilities!");
        log("§8§m+----------------------------+");
    }
    
    @Override
    public void onDisable() {
        // Save all player data
        if (dataManager != null) {
            dataManager.saveAllPlayerData();
            log("§7✓ Player data saved");
        }
        
        // Cancel all tasks
        Bukkit.getScheduler().cancelTasks(this);
        log("§7✓ All tasks cancelled");
        
        // Disable message
        log("§c❌ " + PLUGIN_NAME + " v" + PLUGIN_VERSION + " disabled!");
        log("§8§m+----------------------------+");
    }
    
    /**
     * Check server version compatibility
     */
    private void checkServerVersion() {
        String version = Bukkit.getBukkitVersion();
        log("§7Server Version: §f" + version);
        
        // Check if version is compatible (1.21 - 1.21.4)
        if (!version.startsWith("1.21")) {
            log("§c⚠ This plugin is designed for Paper 1.21-1.21.4!");
            log("§c⚠ You are using " + version + " - some features may not work!");
        } else {
            log("§a✓ Compatible version detected");
        }
    }
    
    /**
     * Save default configuration files
     */
    private void saveDefaultConfigs() {
        // Save config.yml
        saveDefaultConfig();
        log("§7✓ Config.yml loaded");
        
        // Save messages.yml
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
            log("§7✓ Messages.yml created");
        } else {
            log("§7✓ Messages.yml loaded");
        }
        
        // Create player data folder
        File playerDataFolder = new File(getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
            log("§7✓ Player data folder created");
        }
    }
    
    /**
     * Initialize utility classes
     */
    private void initializeUtils() {
        ParticleUtils.init(this);
        SoundUtils.init(this);
        MessageUtils.init(this);
        log("§7✓ Utils initialized");
    }
    
    /**
     * Initialize all managers
     */
    private void initializeManagers() {
        long startTime = System.currentTimeMillis();
        
        fistManager = new FistManager(this);
        cooldownManager = new CooldownManager(this);
        particleManager = new ParticleManager(this);
        abilityManager = new AbilityManager(this);
        dataManager = new DataManager(this);
        
        long timeTaken = System.currentTimeMillis() - startTime;
        log("§7✓ All managers initialized (§e" + timeTaken + "ms§7)");
    }
    
    /**
     * Register all event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new FistListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new ResourcePackListener(this), this);
        
        // Register GUI listeners
        getServer().getPluginManager().registerEvents(new SpinMenuGUI(this), this);
        getServer().getPluginManager().registerEvents(new FistSelectorGUI(this), this);
        getServer().getPluginManager().registerEvents(new StatsGUI(this), this);
        
        log("§7✓ Listeners registered");
    }
    
    /**
     * Register all commands
     */
    private void registerCommands() {
        // Register /fist command
        FistCommand fistCommand = new FistCommand(this);
        getCommand("fist").setExecutor(fistCommand);
        getCommand("fist").setTabCompleter(new FistTabCompleter());
        
        // Register /fistadmin command
        AdminCommand adminCommand = new AdminCommand(this);
        getCommand("fistadmin").setExecutor(adminCommand);
        getCommand("fistadmin").setTabCompleter(new FistTabCompleter());
        
        log("§7✓ Commands registered");
    }
    
    /**
     * Load all player data
     */
    private void loadPlayerData() {
        if (dataManager != null) {
            dataManager.loadAllPlayerData();
            log("§7✓ Player data loaded for " + Bukkit.getOnlinePlayers().size() + " online players");
        }
    }
    
    /**
     * Start bStats metrics if enabled
     */
    private void startMetrics() {
        if (getConfig().getBoolean("metrics.enabled", true)) {
            // Initialize bStats metrics here if you want to add it
            log("§7✓ Metrics enabled");
        }
    }
    
    /**
     * Get total number of abilities
     */
    private int getTotalAbilities() {
        return 20; // 10 fists * 2 abilities each
    }
    
    /**
     * Log message with plugin prefix
     */
    public void log(String message) {
        getLogger().info(message);
    }
    
    /**
     * Log warning
     */
    public void warn(String message) {
        getLogger().warning(message);
    }
    
    /**
     * Log error
     */
    public void error(String message) {
        getLogger().severe(message);
    }
    
    /**
     * Log error with exception
     */
    public void error(String message, Throwable throwable) {
        getLogger().log(Level.SEVERE, message, throwable);
    }
    
    /**
     * Reload plugin
     */
    public void reload() {
        // Reload config
        reloadConfig();
        
        // Reload messages
        MessageUtils.reloadMessages();
        
        // Reload managers
        if (cooldownManager != null) {
            cooldownManager.clearAllCooldowns();
        }
        
        // Reload player data
        if (dataManager != null) {
            dataManager.loadAllPlayerData();
        }
        
        log("§a✅ Plugin reloaded!");
    }
    
    /**
     * Get plugin instance (singleton)
     */
    public static FistPlugin getInstance() {
        return instance;
    }
    
    /**
     * Check if plugin is enabled
     */
    public boolean isEnabled() {
        return super.isEnabled() && instance != null;
    }
    
    // ========== GETTERS FOR MANAGERS ==========
    
    public FistManager getFistManager() {
        return fistManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    
    public ParticleManager getParticleManager() {
        return particleManager;
    }
    
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    // ========== GETTERS FOR PLUGIN INFO ==========
    
    public String getPluginName() {
        return PLUGIN_NAME;
    }
    
    public String getPluginVersion() {
        return PLUGIN_VERSION;
    }
    
    public String getPluginAuthor() {
        return PLUGIN_AUTHOR;
    }
    
    // ========== SCHEDULER HELPER METHODS ==========
    
    /**
     * Run task later
     */
    public void runLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(this, runnable, delay);
    }
    
    /**
     * Run task timer
     */
    public void runTimer(Runnable runnable, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period);
    }
    
    /**
     * Run task async
     */
    public void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }
    
    /**
     * Run task later async
     */
    public void runLaterAsync(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable, delay);
    }
    
    // ========== CONFIGURATION HELPER METHODS ==========
    
    /**
     * Get config value with default
     */
    public int getConfigInt(String path, int defaultValue) {
        return getConfig().getInt(path, defaultValue);
    }
    
    public double getConfigDouble(String path, double defaultValue) {
        return getConfig().getDouble(path, defaultValue);
    }
    
    public boolean getConfigBoolean(String path, boolean defaultValue) {
        return getConfig().getBoolean(path, defaultValue);
    }
    
    public String getConfigString(String path, String defaultValue) {
        return getConfig().getString(path, defaultValue);
    }
    
    // ========== DEBUG METHODS ==========
    
    private boolean debugMode = false;
    
    public void setDebugMode(boolean debug) {
        this.debugMode = debug;
        log("§eDebug mode: " + (debug ? "§aON" : "§cOFF"));
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public void debug(String message) {
        if (debugMode) {
            log("§8[§eDEBUG§8] §f" + message);
        }
    }
    
    // ========== UPDATE CHECKER ==========
    
    /**
     * Check for updates (optional)
     */
    private void checkForUpdates() {
        if (!getConfig().getBoolean("check-for-updates", true)) return;
        
        runAsync(() -> {
            try {
                // You can implement update checking here
                // For example, check against GitHub releases
                debug("Checking for updates...");
            } catch (Exception e) {
                debug("Failed to check for updates: " + e.getMessage());
            }
        });
    }
    
    // ========== METRICS ==========
    
    /**
     * Get plugin statistics
     */
    public PluginStats getStats() {
        return new PluginStats();
    }
    
    public class PluginStats {
        public int getOnlinePlayers() {
            return Bukkit.getOnlinePlayers().size();
        }
        
        public int getTotalFists() {
            return fistManager != null ? fistManager.getTotalFistCount() : 0;
        }
        
        public int getTotalAbilitiesUsed() {
            return fistManager != null ? fistManager.getTotalAbilitiesUsed() : 0;
        }
        
        public long getUptime() {
            return System.currentTimeMillis() - startTime;
        }
    }
    
    private long startTime = System.currentTimeMillis();
    
    // ========== METRICS ==========
    
    /**
     * Register plugin metrics with bStats
     */
    private void setupMetrics() {
        // You can integrate bStats here if desired
        // int pluginId = 12345; // Your plugin ID
        // Metrics metrics = new Metrics(this, pluginId);
    }
}
