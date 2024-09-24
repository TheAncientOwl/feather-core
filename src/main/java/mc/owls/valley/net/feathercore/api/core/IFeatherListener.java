package mc.owls.valley.net.feathercore.api.core;

import org.bukkit.event.Listener;

import mc.owls.valley.net.feathercore.api.exception.ModuleNotEnabledException;

public interface IFeatherListener extends Listener {
    public void onCreate(final IFeatherCoreProvider core) throws ModuleNotEnabledException;
}
