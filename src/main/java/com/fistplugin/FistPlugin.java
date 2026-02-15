package com.fistplugin;

import com.fistplugin.managers.*;
import com.fistplugin.listeners.*;
import com.fistplugin.commands.*;
import com.fistplugin.data.DataManager;
import com.fistplugin.gui.SpinMenuGUI;
import com.fistplugin.gui.FistSelectorGUI;
import com.fistplugin.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class FistPlugin extends JavaPlugin {
    
    private static FistPlugin instance;
    private FistManager fistManager;
    private CooldownManager cooldownManager;
    private ParticleManager particleManager;
    private AbilityManager abilityManager;
    private DataManager dataManager;
    private MessageUtils messageUtils;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default configs
        saveDefaultConfig();
        saveResource("messages.yml", false);
        
        // Initialize managers
        initializeManagers();
        
        // Register listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        // Load all player data
        dataManager.loadAllPlayerData();
        
        getLogger().info("§a✅ FistPlugin v" + getDescription().getVersion() + " enabled!");
        getLogger().info("§e👊 10 Unique Fists loaded successfully!");
    }
    
    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveAllPlayerData();
        }
        getLogger().info("§c❌ FistPlugin disabled!");
    }
    
    private void initializeManagers() {
        fistManager = new FistManager(this);
        cooldownManager = new CooldownManager(this);
        particleManager = new ParticleManager(this);
        abilityManager = new AbilityManager(this);
        dataManager = new DataManager(this);
        messageUtils = new MessageUtils(this);
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new FistListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new ResourcePackListener(this), this);
        getServer().getPluginManager().registerEvents(new SpinMenuGUI(this), this);
        getServer().getPluginManager().registerEvents(new FistSelectorGUI(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this); // ADD THIS
    }
    
    private void registerCommands() {
        getCommand("fist").setExecutor(new FistCommand(this));
        getCommand("fist").setTabCompleter(new FistTabCompleter());
        getCommand("fistadmin").setExecutor(new AdminCommand(this));
    }
    
    public static FistPlugin getInstance() {
        return instance;
    }
    
    public FistManager getFistManager() { return fistManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public ParticleManager getParticleManager() { return particleManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public DataManager getDataManager() { return dataManager; }
    public MessageUtils getMessageUtils() { return messageUtils; }
}
