/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Teleport.java
 * @author Alexandru Delegeanu
 * @version 0.8
 * @description Module responsible for managing teleports
 */

package dev.defaultybuf.feathercore.modules.teleport.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import dev.defaultybuf.feather.toolkit.api.FeatherModule;
import dev.defaultybuf.feather.toolkit.exceptions.FeatherSetupException;
import dev.defaultybuf.feather.toolkit.util.java.Clock;
import dev.defaultybuf.feather.toolkit.util.java.JavaExt;
import dev.defaultybuf.feathercore.modules.teleport.interfaces.ITeleport;

public class Teleport extends FeatherModule implements ITeleport {
    public static enum RequestType {
        TO, HERE
    }

    public static final class TeleportRequest {
        private final Player issuer;
        private final Player target;
        private final RequestType type;
        private long time = 0;

        public TeleportRequest(final Player issuer, final Player target, final RequestType type,
                final long time) {
            this.issuer = issuer;
            this.target = target;
            this.type = type;
            this.time = time;
        }

        public void updateTime(final long newTime) {
            this.time = newTime;
        }

        public boolean isExpired(final long lifetime, final long now) {
            return this.time + lifetime < now;
        }

        public boolean is(final RequestType type) {
            return this.type.equals(type);
        }

        public boolean equals(final Player issuer, final Player target, final RequestType type) {
            return this.type.equals(type) &&
                    this.issuer.getUniqueId().equals(issuer.getUniqueId()) &&
                    this.target.getUniqueId().equals(target.getUniqueId());
        }

        public boolean equals(final Player issuer, final Player target) {
            return this.issuer.getUniqueId().equals(issuer.getUniqueId()) &&
                    this.target.getUniqueId().equals(target.getUniqueId());
        }
    }

    @SuppressWarnings("unused") private BukkitTask teleportCheckTask = null;
    private List<TeleportRequest> requests = new ArrayList<>();
    private Map<UUID, TeleportRequest> teleports = new HashMap<>();

    public Teleport(final InitData data) {
        super(data);
    }

    @Override
    protected void onModuleEnable() throws FeatherSetupException {
        this.teleportCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                getPlugin(), new TeleportChecker(this), 0,
                this.config.getTicks("request.check-interval"));
    }

    @Override
    protected void onModuleDisable() {}

    @Override
    public void teleport(final Player who, final Player to) {
        who.teleport(to);
    }

    @Override
    public void teleport(final Player who, final Location where) {
        who.teleport(where);
    }

    @Override
    public void teleport(final Player who, final double x, final double y, final double z,
            final World world) {
        final var whoLocation = who.getLocation();
        who.teleport(new Location(world, x, y, z, whoLocation.getYaw(), whoLocation.getPitch()));
    }

    @Override
    public void teleport(final Player who, final double x, final double y, final double z) {
        final var whoLocation = who.getLocation();
        who.teleport(new Location(whoLocation.getWorld(), x, y, z, whoLocation.getYaw(),
                whoLocation.getPitch()));
    }

    public static enum RequestStatus {
        ALREADY_REQUESTED, REQUESTED, NO_SUCH_REQUEST, CANCELLED, ACCEPTED
    }

    /**
     * 
     * @param issuer
     * @param target
     * @param type
     * @see Teleport.RequestType
     * @see Teleport.RequestStatus
     * @return ALREADY_REQUESTED | REQUESTED
     */
    @Override
    public RequestStatus request(final Player issuer, final Player target, final RequestType type) {
        if (JavaExt.contains(this.requests, (req) -> {
            return req.equals(issuer, target, type);
        })) {
            return RequestStatus.ALREADY_REQUESTED;
        }

        this.requests.add(new TeleportRequest(issuer, target, type, Clock.currentTimeMillis()));

        return RequestStatus.REQUESTED;
    }

    /**
     * 
     * @param issuer
     * @param target
     * @param type
     * @see Teleport.RequestType
     * @see Teleport.RequestStatus
     * @return NO_SUCH_REQUEST | CANCELLED
     */
    @Override
    public RequestStatus cancelRequest(final Player issuer, final Player target,
            final RequestType type) {
        final var index = JavaExt.findIndex(this.requests, (req) -> {
            return req.equals(issuer, target, type);
        });

        if (!index.isPresent()) {
            return RequestStatus.NO_SUCH_REQUEST;
        }

        this.requests.remove((int) index.get());

        return RequestStatus.CANCELLED;
    }

    /**
     * 
     * @param issuer
     * @param target
     * @see Teleport.RequestType
     * @see Teleport.RequestStatus
     * @return NO_SUCH_REQUEST | CANCELLED
     */
    @Override
    public RequestStatus cancelRequest(final Player issuer, final Player target) {
        final var index = JavaExt.findIndex(this.requests, (req) -> {
            return req.equals(issuer, target);
        });

        if (!index.isPresent()) {
            return RequestStatus.NO_SUCH_REQUEST;
        }

        this.requests.remove((int) index.get());

        return RequestStatus.CANCELLED;
    }

    /**
     * 
     * @param issuer
     * @param target
     * @param type
     * @see Teleport.RequestType
     * @see Teleport.RequestStatus
     * @return NO_SUCH_REQUEST | ACCEPTED
     */
    @Override
    public RequestStatus acceptRequest(final Player issuer, final Player target,
            final RequestType type) {
        final var index = JavaExt.findIndex(this.requests, (req) -> {
            return req.equals(issuer, target, type);
        });

        if (!index.isPresent()) {
            return RequestStatus.NO_SUCH_REQUEST;
        }

        final var timedRequest = this.requests.get((int) index.get());
        this.requests.remove((int) index.get());

        timedRequest.updateTime(Clock.currentTimeMillis());

        this.teleports.put(
                timedRequest.is(RequestType.TO) ? timedRequest.issuer.getUniqueId()
                        : timedRequest.target.getUniqueId(),
                timedRequest);

        return RequestStatus.ACCEPTED;
    }

    /**
     * 
     * @param issuer
     * @param target
     * @see Teleport.RequestType
     * @see Teleport.RequestStatus
     * @return NO_SUCH_REQUEST | ACCEPTED
     */
    @Override
    public RequestStatus acceptRequest(final Player issuer, final Player target) {
        final var index = JavaExt.findLastIndex(this.requests, (req) -> {
            return req.equals(issuer, target);
        });

        if (!index.isPresent()) {
            return RequestStatus.NO_SUCH_REQUEST;
        }

        final var timedRequest = this.requests.get((int) index.get());
        this.requests.remove((int) index.get());

        timedRequest.updateTime(Clock.currentTimeMillis());

        this.teleports.put(
                timedRequest.is(RequestType.TO) ? timedRequest.issuer.getUniqueId()
                        : timedRequest.target.getUniqueId(),
                timedRequest);

        return RequestStatus.ACCEPTED;
    }

    /**
     * 
     * @param player
     * @return true if player is waiting for teleport, false otherwise
     */
    @Override
    public boolean isWaitingForTeleport(final Player player) {
        return this.teleports.containsKey(player.getUniqueId());
    }

    /**
     * 
     * @param player
     * @return true if player was waiting for teleport and it was cancelled, false otherwise
     */
    @Override
    public boolean cancelTeleport(final Player player) {
        return this.teleports.remove(player.getUniqueId()) != null;
    }

    public static class TeleportChecker implements Runnable {
        final Teleport teleport;

        public TeleportChecker(final Teleport teleport) {
            super();
            this.teleport = teleport;
        }

        /**
         * @brief Check for player teleports: execute them and clean the data-structure when needed
         */
        @Override
        public void run() {
            if (this.teleport.teleports.isEmpty()) {
                return;
            }

            final var currentTime = Clock.currentTimeMillis();

            final var teleportsToExecute = new ArrayList<TeleportRequest>();

            final var teleportDelay = this.teleport.getConfig().getMillis("request.accept-delay");
            final var iterator = this.teleport.teleports.entrySet().iterator();
            while (iterator.hasNext()) {
                final var entry = iterator.next().getValue();
                if (entry.isExpired(teleportDelay, currentTime)) {
                    iterator.remove();
                    teleportsToExecute.add(entry);
                }
            }

            Bukkit.getScheduler().runTask(this.teleport.getPlugin(), () -> {
                teleportsToExecute.forEach((entry) -> {
                    switch (entry.type) {
                        case TO: {
                            this.teleport.teleport(entry.issuer, entry.target);
                            break;
                        }
                        case HERE: {
                            this.teleport.teleport(entry.target, entry.issuer);
                            break;
                        }
                    }
                });
            });
        }
    }
}
