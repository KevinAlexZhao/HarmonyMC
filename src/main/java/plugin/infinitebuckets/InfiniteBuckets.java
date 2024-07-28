package plugin.infinitebuckets;

import org.bukkit.plugin.java.JavaPlugin;

public final class InfiniteBuckets extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        ItemManager.init();
        getCommand("infwater").setExecutor(new CommandManager());
        getCommand("inflava").setExecutor(new CommandManager());
        getServer().getPluginManager().registerEvents(new CustomItemListener(), this);
        getLogger().info("InfiniteBuckets plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("InfiniteBuckets plugin has been disabled!");
    }
}
