/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file Teleport.java
 * @author Alexandru Delegeanu
 * @version 0.3
 * @description Module responsible for managing teleports
 */

package mc.owls.valley.net.feathercore.modules.teleport.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import mc.owls.valley.net.feathercore.api.common.java.JavaExt;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exceptions.FeatherSetupException;

public class Teleport extends FeatherModule {
    public static enum RequestType {
        TO,
        HERE
    }

    private static final class TeleportRequest {
        private final Player issuer;
        private final Player target;
        private final RequestType type;
        private long time = 0;

        public TeleportRequest(final Player issuer, final Player target, final RequestType type, final long time) {
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

    @SuppressWarnings("unused")
    private BukkitTask teleportCheckTask = null;
    private JavaPlugin plugin = null;
    private List<TeleportRequest> requests = null;
    private Map<UUID, TeleportRequest> teleports = null;

    public Teleport(final String name, final Supplier<IConfigFile> configSupplier) {
        super(name, configSupplier);
    }

    @Override
    protected void onModuleEnable(final IFeatherCoreProvider core) throws FeatherSetupException {
        this.requests = new ArrayList<>();
        this.teleports = new HashMap<>();
        this.plugin = core.getPlugin();

        this.teleportCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(core.getPlugin(),
                new TeleportChecker(this), 0, this.config.getTicks("request.check-interval"));
    }

    @Override
    protected void onModuleDisable() {
    }

    public static void teleport(final Player who, final Player to) {
        who.teleport(to);
    }

    public static void teleport(final Player who, final Location where) {
        who.teleport(where);
    }

    public static void teleport(final Player who, final double x, final double y, final double z, final World world) {
        final var whoLocation = who.getLocation();
        who.teleport(new Location(world, x, y, z, whoLocation.getYaw(), whoLocation.getPitch()));
    }

    public static void teleport(final Player who, final double x, final double y, final double z) {
        final var whoLocation = who.getLocation();
        who.teleport(new Location(whoLocation.getWorld(), x, y, z, whoLocation.getYaw(), whoLocation.getPitch()));
    }

    public static enum RequestStatus {
        ALREADY_REQUESTED,
        REQUESTED,
        NO_SUCH_REQUEST,
        CANCELLED,
        ACCEPTED
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
    public RequestStatus request(final Player issuer, final Player target, final RequestType type) {
        if (JavaExt.contains(this.requests, (req) -> {
            return req.equals(issuer, target, type);
        })) {
            return RequestStatus.ALREADY_REQUESTED;
        }

        this.requests.add(new TeleportRequest(issuer, target, type, System.currentTimeMillis()));

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
    public RequestStatus cancelRequest(final Player issuer, final Player target, final RequestType type) {
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
    public RequestStatus acceptRequest(final Player issuer, final Player target, final RequestType type) {
        final var index = JavaExt.findIndex(this.requests, (req) -> {
            return req.equals(issuer, target, type);
        });

        if (!index.isPresent()) {
            return RequestStatus.NO_SUCH_REQUEST;
        }

        final var timedRequest = this.requests.get((int) index.get());
        this.requests.remove((int) index.get());

        timedRequest.updateTime(System.currentTimeMillis());

        this.teleports.put(
                timedRequest.is(RequestType.TO) ? timedRequest.issuer.getUniqueId() : timedRequest.target.getUniqueId(),
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
    public RequestStatus acceptRequest(final Player issuer, final Player target) {
        final var index = JavaExt.findLastIndex(this.requests, (req) -> {
            return req.equals(issuer, target);
        });

        if (!index.isPresent()) {
            return RequestStatus.NO_SUCH_REQUEST;
        }

        final var timedRequest = this.requests.get((int) index.get());
        this.requests.remove((int) index.get());

        timedRequest.updateTime(System.currentTimeMillis());

        this.teleports.put(
                timedRequest.is(RequestType.TO) ? timedRequest.issuer.getUniqueId() : timedRequest.target.getUniqueId(),
                timedRequest);

        return RequestStatus.ACCEPTED;
    }

    /**
     * 
     * @param player
     * @return true if player is waiting for teleport, false otherwise
     */
    public boolean isWaitingForTeleport(final Player player) {
        return this.teleports.containsKey(player.getUniqueId());
    }

    /**
     * 
     * @param player
     * @return true if player was waiting for teleport and it was cancelled, false
     *         otherwise
     */
    public boolean cancelTeleport(final Player player) {
        return this.teleports.remove(player.getUniqueId()) != null;
    }

    private static class TeleportChecker implements Runnable {
        final Teleport teleport;

        public TeleportChecker(final Teleport teleport) {
            super();
            this.teleport = teleport;
        }

        /**
         * @brief Check for player teleports: execute them and clean the data-structure
         *        when needed
         */
        @Override
        public void run() {
            if (this.teleport.teleports.isEmpty()) {
                return;
            }

            final var currentTime = System.currentTimeMillis();

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

            Bukkit.getScheduler().runTask(this.teleport.plugin, () -> {
                teleportsToExecute.forEach((entry) -> {
                    switch (entry.type) {
                        case TO: {
                            Teleport.teleport(entry.issuer, entry.target);
                            break;
                        }
                        case HERE: {
                            Teleport.teleport(entry.target, entry.issuer);
                            break;
                        }
                    }
                });
            });
        }

    }

}
