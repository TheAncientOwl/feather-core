/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageChangeEvent.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @description Language change event data
 */

package mc.owls.valley.net.feathercore.modules.language.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public class LanguageChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    private final Player player;
    private final String language;
    private final IConfigFile translation;

    public LanguageChangeEvent(final Player player, final String language, final IConfigFile translation) {
        this.cancelled = false;

        this.player = player;
        this.language = language;
        this.translation = translation;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getLanguage() {
        return this.language;
    }

    public IConfigFile getTranslation() {
        return this.translation;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
