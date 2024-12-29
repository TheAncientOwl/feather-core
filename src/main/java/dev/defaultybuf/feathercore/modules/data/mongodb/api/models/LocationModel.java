/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Location.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description Location data model
 */

package dev.defaultybuf.feathercore.modules.data.mongodb.api.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;

@Entity(value = "locations", useDiscriminator = false)
public class LocationModel {

    @Property("pos_x") public double x;

    @Property("pos_y") public double y;

    @Property("pos_z") public double z;

    @Property("world_name") public String world;

    public LocationModel() {}

    public LocationModel(org.bukkit.Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld().getName();
    }

}
