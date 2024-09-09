package mc.owls.valley.net.feathercore.core.common;

import java.lang.reflect.Field;

import org.bukkit.configuration.file.FileConfiguration;

import mc.owls.valley.net.feathercore.api.common.StringUtils;
import mc.owls.valley.net.feathercore.api.common.YamlUtils;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.core.FeatherCore;

public class Literals {
    public static void setup(final FeatherCore plugin) throws FeatherSetupException {
        final FileConfiguration pluginConfig = YamlUtils.loadYaml(plugin, FeatherCore.PLUGIN_YML);
        final var literals = pluginConfig.getMapList("feathercore.literals");

        for (final var literal : literals) {
            final String fieldName = (String) literal.get("field");
            final String literalName = (String) literal.get("literal");

            try {
                final Class<?> clazz = plugin.getClass();
                final Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(plugin, literalName);
            } catch (NoSuchFieldException | IllegalAccessException | SecurityException e) {
                throw new FeatherSetupException("Could not setup config connection {" + fieldName + " -> " + literalName
                        + "}\nReason: " + StringUtils.exceptionToStr(e));
            }
        }
    }
}
