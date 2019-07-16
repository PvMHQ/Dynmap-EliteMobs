package io.github.alexgraz.dynmapelitemobs;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DynmapEliteMobs extends JavaPlugin {

    private static Plugin pluginInstance = null;

    @Override
    public void onEnable() {
        pluginInstance = this;
        getServer().getPluginManager().registerEvents(new EliteMobListener(), this);
    }

    public static Plugin getInstance() {
        return pluginInstance;
    }
}