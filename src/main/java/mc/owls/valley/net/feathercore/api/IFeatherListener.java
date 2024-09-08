package mc.owls.valley.net.feathercore.api;

import org.bukkit.event.Listener;

import mc.owls.valley.net.feathercore.core.FeatherCore;

public interface IFeatherListener extends Listener {
    public void onCreate(final FeatherCore plugin);
}
