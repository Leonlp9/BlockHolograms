package de.flam.untitled;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Holo extends JavaPlugin {

    public static Holo plugin;

    Command command = new Command();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getCommand("holo").setExecutor(command);
        command.start();

        int pluginId = 18700; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        getLogger().info("Metrics enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Holo getPlugin() {
        return plugin;
    }
}
