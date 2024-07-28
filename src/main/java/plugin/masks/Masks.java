package plugin.masks;

import org.bukkit.plugin.java.JavaPlugin;

public final class Masks extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        getCommand("mask").setExecutor(new MaskCommand(this));
        getCommand("masks").setExecutor(new MaskCommand(this));
        getServer().getPluginManager().registerEvents(new GUIManager(this), this);

        // debug
        //getLogger().info("Loaded configuration: " + getConfig().saveToString());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
