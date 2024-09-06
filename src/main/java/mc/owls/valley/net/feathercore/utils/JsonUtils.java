package mc.owls.valley.net.feathercore.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import mc.owls.valley.net.feathercore.core.FeatherCore;
import mc.owls.valley.net.feathercore.modules.manager.exceptions.ModuleSetupException;

public class JsonUtils {
    public static JSONObject loadJSON(@NotNull final FeatherCore plugin, @NotNull final String fileName)
            throws ModuleSetupException {
        JSONObject jsonObject = null;
        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream != null) {
                final JSONParser parser = new JSONParser();
                jsonObject = (JSONObject) parser.parse(new InputStreamReader(inputStream));
            } else {
                throw new ModuleSetupException(fileName
                        + " not found in plugin resources. That's weird O.o, please contact the developer");
            }
        } catch (IOException | ParseException e) {
            throw new ModuleSetupException(
                    "Error on parsing json resource -> cause: " + StringUtils.exceptionToStr(e));
        }
        if (jsonObject == null) {
            throw new ModuleSetupException("Failed to read json resource: " + fileName);
        }
        return jsonObject;
    }

    public static void assertEntryNotNull(final Object object, @NotNull final String tag, @NotNull final String file)
            throws ModuleSetupException {
        if (object == null) {
            throw new ModuleSetupException(
                    tag + " is missing from " + file);
        }
    }
}
