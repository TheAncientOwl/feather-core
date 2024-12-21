/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit LanguageCommand#0.9
 * @description Unit tests for LanguageCommand
 */

package mc.owls.valley.net.feathercore.modules.language.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import mc.owls.valley.net.feathercore.api.common.java.Pair;
import mc.owls.valley.net.feathercore.api.common.language.Message;
import mc.owls.valley.net.feathercore.api.common.minecraft.Placeholder;
import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.configuration.IConfigSection;
import mc.owls.valley.net.feathercore.modules.common.CommandTestMocker;
import mc.owls.valley.net.feathercore.modules.common.Modules;
import mc.owls.valley.net.feathercore.modules.common.TestUtils;
import mc.owls.valley.net.feathercore.modules.data.mongodb.api.models.PlayerModel;
import mc.owls.valley.net.feathercore.modules.data.players.components.PlayersData;
import mc.owls.valley.net.feathercore.modules.data.players.interfaces.IPlayersData;
import mc.owls.valley.net.feathercore.modules.language.components.LanguageManagerTest;
import mc.owls.valley.net.feathercore.modules.language.interfaces.ILanguage;

class LanguageCommandTest extends CommandTestMocker<LanguageCommand> {
    PlayersData mockPlayersData;
    Player mockPlayer;
    PlayerModel playerModel;
    World mockWorld;

    private static final String EN_LANGUAGE_FILE_CONTENT =
            "language:\n" +
                    "  usage: '&8[&6Usage&8] &e/language info/list/[language]'\n" +
                    "  change-success: '&8[&6Language&8] &eChanged successfully&8!'\n" +
                    "  unknown: '&8[&4Language&8] &cInvalid value&8!'\n" +
                    "  info: '&8[&6Language&8] &e{language}&8.'\n" +
                    "  list: '&8[&6Language&8] &7Available languages&8: &e{language}&8.'\n";

    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n en: English";

    ArgumentCaptor<String> messageCaptor;

    @Mock IConfigFile mockLanguageConfig;
    @Mock IConfigSection mockLanguagesConfigSection;

    @Override
    protected Class<LanguageCommand> getCommandClass() {
        return LanguageCommand.class;
    }

    @Override
    protected List<Pair<Class<?>, Object>> getOtherMockDependencies() {
        mockPlayersData = Modules.PLAYERS_DATA.Mock();

        return List.of(Pair.of(IPlayersData.class, mockPlayersData));
    }

    @BeforeEach
    void setUp() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        mockWorld = mock(World.class);
        mockPlayer = mock(Player.class);

        Mockito.lenient().when(mockPlayer.getLocation())
                .thenReturn(new Location(mockWorld, 0, 0, 0));

        playerModel = new PlayerModel(mockPlayer, 0, LanguageManagerTest.EN.shortName());
        verify(mockPlayer).getUniqueId();
        verify(mockPlayer).getName();
        verify(mockPlayer).getLocation();
        Mockito.lenient().when(mockPlayersData.getPlayerModel(mockPlayer)).thenReturn(playerModel);
        Mockito.lenient().when(mockPlayersData.getPlayerModel((OfflinePlayer) mockPlayer))
                .thenReturn(playerModel);

        Mockito.lenient().when(mockLanguage.getConfig()).thenReturn(mockLanguageConfig);
        Mockito.lenient().when(mockLanguageConfig.getConfigurationSection("languages"))
                .thenReturn(mockLanguagesConfigSection);

        messageCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void testHasPermission_PermissionINFO() {
        var commandData =
                new LanguageCommand.CommandData(LanguageCommand.CommandType.INFO,
                        LanguageManagerTest.EN.shortName());
        assertTrue(commandInstance.hasPermission(mockSender, commandData));

        verifyNoInteractions(mockSender);
        verifyNoInteractions(mockLanguage);
    }

    @Test
    void testHasPermission_PermissionLIST() {
        var commandData =
                new LanguageCommand.CommandData(LanguageCommand.CommandType.LIST,
                        LanguageManagerTest.EN.shortName());
        assertTrue(commandInstance.hasPermission(mockSender, commandData));

        verifyNoInteractions(mockSender);
        verifyNoInteractions(mockLanguage);
    }

    @Test
    void testHasPermission_PermissionCHANGE() {
        var commandData =
                new LanguageCommand.CommandData(LanguageCommand.CommandType.CHANGE,
                        LanguageManagerTest.EN.shortName());
        assertTrue(commandInstance.hasPermission(mockSender, commandData));

        verifyNoInteractions(mockSender);
        verifyNoInteractions(mockLanguage);
    }

    @Test
    void execute_INFO() {
        try ( // @formatter:off
            var languageConfigFile = Modules.LANGUAGE.makeTempConfig(LANGUAGE_CONFIG_CONTENT);
            var enTranslationFile = Modules.LANGUAGE.makeTempResource("en.yml", EN_LANGUAGE_FILE_CONTENT)
        ) { // @formatter:on
            var actualLanguage = Modules.LANGUAGE.Actual(mockJavaPlugin, dependenciesMap);
            dependenciesMap.put(ILanguage.class, actualLanguage);

            assertNotNull(actualLanguage.getConfig(), "Failed to load config file");

            var commandData = new LanguageCommand.CommandData(
                    LanguageCommand.CommandType.INFO, null);

            assertDoesNotThrow(() -> {
                commandInstance.execute(mockPlayer, commandData);
            });

            playerModel.language = LanguageManagerTest.FR.shortName();

            assertDoesNotThrow(() -> {
                commandInstance.execute(mockPlayer, commandData);
            });

            verify(mockPlayer, times(2)).sendMessage(messageCaptor.capture());

            assertEquals(2, messageCaptor.getAllValues().size());

            var rawMessage = actualLanguage.getTranslation("en").getString(Message.Language.INFO);
            assertEquals(
                    TestUtils.placeholderize(rawMessage, Pair.of(Placeholder.LANGUAGE, "English")),
                    messageCaptor.getAllValues().get(0));
            assertEquals(
                    TestUtils.placeholderize(rawMessage, Pair.of(Placeholder.LANGUAGE, "")),
                    messageCaptor.getAllValues().get(1));
        }
    }

}
