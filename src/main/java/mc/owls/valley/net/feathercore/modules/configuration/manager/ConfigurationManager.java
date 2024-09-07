package mc.owls.valley.net.feathercore.modules.configuration.manager;

import java.lang.reflect.Field;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.configuration.api.IConfigFile;
import mc.owls.valley.net.feathercore.modules.configuration.bukkit.BukkitConfigFile;
import mc.owls.valley.net.feathercore.modules.configuration.manager.api.IConfigurationManager;
import mc.owls.valley.net.feathercore.modules.manager.FeatherModule;
import mc.owls.valley.net.feathercore.modules.manager.ModuleEnableStatus;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;
import mc.owls.valley.net.feathercore.utils.JsonUtils;
import mc.owls.valley.net.feathercore.utils.StringUtils;

public class ConfigurationManager extends FeatherModule implements IConfigurationManager {
    private static final String MANAGER_CONFIG_FILE_NAME = "config-manager.json";

    private IConfigFile dataConfigFile = null;
    private IConfigFile economyConfigFile = null;

    public ConfigurationManager(String name) {
        super(name);
    }

    @Override
    protected ModuleEnableStatus onModuleEnable(final FeatherCore plugin) throws ModuleSetupException {
        // 1. load connections array from config file
        final JSONObject jsonConfig = JsonUtils.loadJSON(plugin, ConfigurationManager.MANAGER_CONFIG_FILE_NAME);

        final JSONArray connectionsArray = (JSONArray) jsonConfig.get("connections");
        JsonUtils.assertEntryNotNull(connectionsArray, "connections array",
                ConfigurationManager.MANAGER_CONFIG_FILE_NAME);

        // 2. parse connections array
        for (final Object connectionObj : connectionsArray) {
            final JSONObject connectionJSON = (JSONObject) connectionObj;

            final String fieldName = (String) connectionJSON.get("field");
            final String configName = (String) connectionJSON.get("config");

            JsonUtils.assertEntryNotNull(fieldName, "field property", ConfigurationManager.MANAGER_CONFIG_FILE_NAME);
            JsonUtils.assertEntryNotNull(configName, "config property", ConfigurationManager.MANAGER_CONFIG_FILE_NAME);

            try {
                final Class<?> clazz = this.getClass();
                final Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(this, new BukkitConfigFile(plugin, configName));
            } catch (NoSuchFieldException | IllegalAccessException | SecurityException e) {
                throw new ModuleSetupException("Could not setup config connection {" + fieldName + " -> " + configName
                        + "}\nReason: " + StringUtils.exceptionToStr(e));
            }
        }

        return ModuleEnableStatus.SUCCESS;
    }

    @Override
    protected void onModuleDisable() {
        this.dataConfigFile.saveConfig();
    }

    @Override
    public IConfigFile getDataConfiguration() {
        return this.dataConfigFile;
    }

    @Override
    public IConfigFile getEconomyConfigFile() {
        return this.economyConfigFile;
    }

}
