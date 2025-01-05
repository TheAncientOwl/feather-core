/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file PlayerDamageListenerTest.java
 * @author Alexandru Delegeanu
 * @version 0.2
 * @test_unit PlayerDamageListener#0.4
 * @description Unit tests for PlayerDamageListener
 */

package dev.defaultybuf.feathercore.modules.pvp.manager.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import dev.defaultybuf.feathercore.modules.common.annotations.ActualModule;
import dev.defaultybuf.feathercore.modules.common.annotations.MockedModule;
import dev.defaultybuf.feathercore.modules.common.annotations.Resource;
import dev.defaultybuf.feathercore.modules.common.annotations.TestField;
import dev.defaultybuf.feathercore.modules.common.mockers.DependencyInjector.Module;
import dev.defaultybuf.feathercore.modules.common.mockers.FeatherListenerTest;
import dev.defaultybuf.feathercore.modules.common.utils.TempModule;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import dev.defaultybuf.feathercore.modules.pvp.manager.interfaces.IPvPManager;

class PlayerDamageListenerTest extends FeatherListenerTest<PlayerDamageListener> {
    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n  en: English";

    // @formatter:off
    private static final String EN_LANGUAGE_FILE_CONTENT =
            "general:\n" +
            "  command:\n" +
            "    no-permission: '&cYou do not have permission to execute this command.'\n" +
            "    invalid: '&cInvalid command usage.'\n" +
            "    players-only: '&cOnly players can execute this command.'\n" +
            "  not-player: '&c{0} is not a player.'\n" +
            "  not-valid-number: '&cNot valid number'\n" +
            "pvp:\n" +
            "  block-command: '&cYou cannot use this command during combat.'\n";
    // @formatter:on

    @Mock Player mockVictim;
    @Mock Player mockDamager;
    @Mock Projectile mockProjectile;
    @Mock EntityDamageByEntityEvent mockEvent;

    @MockedModule(of = Module.PlayersData) IPlayersData mockPlayersData;
    @MockedModule(of = Module.PvPManager) IPvPManager mockPvPManager;

    @ActualModule(
            of = Module.Language,
            resources = {
                    @Resource(path = "config.yml", content = LANGUAGE_CONFIG_CONTENT),
                    @Resource(path = "en.yml", content = EN_LANGUAGE_FILE_CONTENT)
            }) TempModule<LanguageManager> actualLanguage;

    @TestField PlayerModel playerModel;

    @Override
    protected Class<PlayerDamageListener> getListenerClass() {
        return PlayerDamageListener.class;
    }

    @Override
    protected void setUp() {
        playerModel = new PlayerModel();
        playerModel.language = "en";

        lenient().when(mockPlayersData.getPlayerModel(mockVictim)).thenReturn(playerModel);
        lenient().when(mockPlayersData.getPlayerModel(mockDamager)).thenReturn(playerModel);
        lenient().when(mockEvent.getEntity()).thenReturn(mockVictim);
        lenient().when(mockEvent.getDamager()).thenReturn(mockDamager);
        lenient().when(mockEvent.isCancelled()).thenReturn(false);
        lenient().when(mockEvent.getDamage()).thenReturn(10.0);
    }

    @Test
    void testOnPlayerDamage_EventCancelled() {
        when(mockEvent.isCancelled()).thenReturn(true);

        listenerInstance.onPlayerDamage(mockEvent);

        verify(mockPvPManager, never()).putPlayersInCombat(any(Player.class), any(Player.class));
    }

    @Test
    void testOnPlayerDamage_ZeroDamage() {
        when(mockEvent.getDamage()).thenReturn(0.0);

        listenerInstance.onPlayerDamage(mockEvent);

        verify(mockPvPManager, never()).putPlayersInCombat(any(Player.class), any(Player.class));
    }

    @Test
    void testOnPlayerDamage_VictimNotPlayer() {
        when(mockEvent.getEntity()).thenReturn(mock(Entity.class));

        listenerInstance.onPlayerDamage(mockEvent);

        verify(mockPvPManager, never()).putPlayersInCombat(any(Player.class), any(Player.class));
    }

    @Test
    void testOnPlayerDamage_DamagerIsPlayer() {
        listenerInstance.onPlayerDamage(mockEvent);

        verify(mockPvPManager).putPlayersInCombat(mockVictim, mockDamager);
    }

    @Test
    void testOnPlayerDamage_DamagerIsProjectile_ShooterIsPlayer() {
        when(mockEvent.getDamager()).thenReturn(mockProjectile);
        when(mockProjectile.getShooter()).thenReturn(mockDamager);

        listenerInstance.onPlayerDamage(mockEvent);

        verify(mockPvPManager).putPlayersInCombat(mockVictim, mockDamager);
    }

    @Test
    void testOnPlayerDamage_DamagerIsProjectile_ShooterNotPlayer() {
        when(mockEvent.getDamager()).thenReturn(mockProjectile);
        when(mockProjectile.getShooter()).thenReturn(mock(ProjectileSource.class));

        listenerInstance.onPlayerDamage(mockEvent);

        verify(mockPvPManager, never()).putPlayersInCombat(any(Player.class), any(Player.class));
    }

    @Test
    void testOnPlayerDamage_DamagerNotPlayerOrProjectile() {
        when(mockEvent.getDamager()).thenReturn(mock(Entity.class));

        listenerInstance.onPlayerDamage(mockEvent);

        verify(mockPvPManager, never()).putPlayersInCombat(any(Player.class), any(Player.class));
    }
}
