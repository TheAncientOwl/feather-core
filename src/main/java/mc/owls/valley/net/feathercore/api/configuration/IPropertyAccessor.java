package mc.owls.valley.net.feathercore.api.configuration;

import java.util.List;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface IPropertyAccessor {
    public String getString(@NotNull final String path);

    public boolean getBoolean(@NotNull final String path);

    public int getInt(@NotNull final String path);

    public double getDouble(@NotNull final String path);

    public IConfigSection getConfigurationSection(@NotNull final String path);

    public List<String> getStringList(@NotNull final String path);
}
