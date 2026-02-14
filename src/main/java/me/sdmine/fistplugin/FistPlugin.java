package me.sdmine.fistplugin;

import me.sdmine.fistplugin.ability.AbilityListener;
import me.sdmine.fistplugin.command.FistCommand;
import me.sdmine.fistplugin.fist.FistManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FistPlugin extends JavaPlugin {

    private static FistPlugin instance;
    private FistManager fistManager;

    @Override
    public void onEnable() {
        instance = this;
        fistManager = new FistManager();

        getServer().getPluginManager().registerEvents(
                new AbilityListener(fistManager), this
        );

        getCommand("fist").setExecutor(new FistCommand(fistManager));

        saveDefaultConfig();
    }

    public static FistPlugin get() {
        return instance;
    }
}

