package me.honkling.fiercesnowballs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class FierceSnowballs extends JavaPlugin {
    public static FierceSnowballs instance;

    @Override
    public void onEnable() {
        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new DamageListener(), this);
        instance = this;

        saveDefaultConfig();

        getLogger().info("Snowballs are lookin' fierce.");
    }
}
