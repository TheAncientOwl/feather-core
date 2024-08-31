package mc.owls.valley.net;

import org.bukkit.plugin.java.JavaPlugin;

public class FeatherCore extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Hello, PaperMC!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye, PaperMC!");
    }
}
