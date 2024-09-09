package mc.owls.valley.net.feathercore.api.core;

import org.bukkit.event.Listener;

public interface IFeatherListener extends Listener {
    public void onCreate(final IFeatherCoreProvider provider);
}
