/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file ITeleport.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Teleport module interface
 */

package dev.defaultybuf.feathercore.modules.teleport.interfaces;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import dev.defaultybuf.feathercore.api.core.IConfigAccessor;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.RequestStatus;
import dev.defaultybuf.feathercore.modules.teleport.components.Teleport.RequestType;

public interface ITeleport extends IConfigAccessor {
        public void teleport(final Player who, final Player to);

        public void teleport(final Player who, final Location where);

        public void teleport(final Player who, final double x, final double y, final double z,
                        final World world);

        public void teleport(final Player who, final double x, final double y, final double z);

        /**
         * 
         * @param issuer
         * @param target
         * @param type
         * @see Teleport.RequestType
         * @see Teleport.RequestStatus
         * @return ALREADY_REQUESTED | REQUESTED
         */
        public RequestStatus request(final Player issuer, final Player target,
                        final RequestType type);

        /**
         * 
         * @param issuer
         * @param target
         * @param type
         * @see Teleport.RequestType
         * @see Teleport.RequestStatus
         * @return NO_SUCH_REQUEST | CANCELLED
         */
        public RequestStatus cancelRequest(final Player issuer, final Player target,
                        final RequestType type);

        /**
         * 
         * @param issuer
         * @param target
         * @see Teleport.RequestType
         * @see Teleport.RequestStatus
         * @return NO_SUCH_REQUEST | CANCELLED
         */
        public RequestStatus cancelRequest(final Player issuer, final Player target);

        /**
         * 
         * @param issuer
         * @param target
         * @param type
         * @see Teleport.RequestType
         * @see Teleport.RequestStatus
         * @return NO_SUCH_REQUEST | ACCEPTED
         */
        public RequestStatus acceptRequest(final Player issuer, final Player target,
                        final RequestType type);

        /**
         * 
         * @param issuer
         * @param target
         * @see Teleport.RequestType
         * @see Teleport.RequestStatus
         * @return NO_SUCH_REQUEST | ACCEPTED
         */
        public RequestStatus acceptRequest(final Player issuer, final Player target);

        /**
         * 
         * @param player
         * @return true if player is waiting for teleport, false otherwise
         */
        public boolean isWaitingForTeleport(final Player player);

        /**
         * 
         * @param player
         * @return true if player was waiting for teleport and it was cancelled, false otherwise
         */
        public boolean cancelTeleport(final Player player);
}
