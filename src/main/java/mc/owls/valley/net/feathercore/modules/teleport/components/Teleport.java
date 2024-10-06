/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Teleport.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Module responsible for managing teleports
 */

package mc.owls.valley.net.feathercore.modules.teleport.components;

import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

public class Teleport extends FeatherModule {

    public Teleport(final String name, final Supplier<IConfigFile> configSupplier) {
        super(name, configSupplier);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
    }

    @Override
    protected void onModuleDisable() {
    }

    public void teleport(final Player who, final Player to) {
        who.teleport(to);
    }

    public void teleport(final Player who, final double x, final double y, final double z, final World world) {
        final var whoLocation = who.getLocation();
        who.teleport(new Location(world, x, y, z, whoLocation.getYaw(), whoLocation.getPitch()));
    }

    public void teleport(final Player who, final double x, final double y, final double z) {
        final var whoLocation = who.getLocation();
        who.teleport(new Location(whoLocation.getWorld(), x, y, z, whoLocation.getYaw(), whoLocation.getPitch()));
    }

}
