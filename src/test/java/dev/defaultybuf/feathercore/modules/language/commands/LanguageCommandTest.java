/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file LanguageCommandTest.java
 * @author Alexandru Delegeanu
 * @version 0.5
 * @test_unit LanguageCommand#0.9
 * @description Unit tests for LanguageCommand
 */

package dev.defaultybuf.feathercore.modules.language.commands;

import static dev.defaultybuf.feathercore.modules.common.Modules.injectAs;
import static dev.defaultybuf.feathercore.modules.common.Modules.withResources;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import dev.defaultybuf.feathercore.api.common.java.Pair;
import dev.defaultybuf.feathercore.api.common.language.Message;
import dev.defaultybuf.feathercore.api.common.minecraft.Placeholder;
import dev.defaultybuf.feathercore.api.configuration.IConfigFile;
import dev.defaultybuf.feathercore.api.configuration.IConfigSection;
import dev.defaultybuf.feathercore.modules.common.CommandTestMocker;
import dev.defaultybuf.feathercore.modules.common.Modules;
import dev.defaultybuf.feathercore.modules.common.Resource;
import dev.defaultybuf.feathercore.modules.common.TempModule;
import dev.defaultybuf.feathercore.modules.common.TestUtils;
import dev.defaultybuf.feathercore.modules.data.mongodb.api.models.PlayerModel;
import dev.defaultybuf.feathercore.modules.data.players.components.PlayersData;
import dev.defaultybuf.feathercore.modules.data.players.interfaces.IPlayersData;
import dev.defaultybuf.feathercore.modules.language.commands.LanguageCommand.CommandType;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManager;
import dev.defaultybuf.feathercore.modules.language.components.LanguageManagerTest;
import dev.defaultybuf.feathercore.modules.language.events.LanguageChangeEvent;
import dev.defaultybuf.feathercore.modules.language.interfaces.ILanguage;

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

    private static final String DE_LANGUAGE_FILE_CONTENT =
            "language:\n" +
                    "  usage: '&8[&6Verwendung&8] &e/language info/list/[sprache]'\n" +
                    "  change-success: '&8[&6Sprache&8] &eErfolgreich geändert&8!'\n" +
                    "  unknown: '&8[&4Sprache&8] &cUngültiger Wert&8!'\n" +
                    "  info: '&8[&6Sprache&8] &e{sprache}&8.'\n" +
                    "  list: '&8[&6Sprache&8] &7Verfügbare Sprachen&8: &e{sprache}&8.'\n";

    private static final String LANGUAGE_CONFIG_CONTENT = "languages:\n en: English\n de: Deutsch";

    ArgumentCaptor<String> messageCaptor;

    @Mock IConfigFile mockLanguageConfig;
    @Mock IConfigSection mockLanguagesConfigSection;

    TempModule<LanguageManager> actualLanguage;

    @Override
    protected Class<LanguageCommand> getCommandClass() {
        return LanguageCommand.class;
    }

    @Override
    protected List<Pair<Class<?>, Object>> getOtherMockDependencies() {
        mockPlayersData = Modules.PLAYERS_DATA.Mock();

        return List.of(Pair.of(IPlayersData.class, mockPlayersData));
    }

    @Override
    protected List<AutoCloseable> injectActualModules() {
        actualLanguage = Modules.LANGUAGE.Actual(mockJavaPlugin, dependenciesMap,
                injectAs(ILanguage.class), withResources(
                        Resource.of("config.yml", LANGUAGE_CONFIG_CONTENT),
                        Resource.of("en.yml", EN_LANGUAGE_FILE_CONTENT),
                        Resource.of("de.yml", DE_LANGUAGE_FILE_CONTENT)));

        return List.of(actualLanguage);
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

        var rawMessage =
                actualLanguage.module().getTranslation("en").getString(Message.Language.INFO);
        assertEquals(
                TestUtils.placeholderize(rawMessage, Pair.of(Placeholder.LANGUAGE, "English")),
                messageCaptor.getAllValues().get(0));
        assertEquals(
                TestUtils.placeholderize(rawMessage, Pair.of(Placeholder.LANGUAGE, "")),
                messageCaptor.getAllValues().get(1));
    }

    @Test
    void execute_LIST() {

        var commandData = new LanguageCommand.CommandData(
                LanguageCommand.CommandType.LIST, null);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockPlayer, commandData);
        });

        playerModel.language = LanguageManagerTest.FR.shortName();

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockPlayer, commandData);
        });

        verify(mockPlayer, times(2)).sendMessage(messageCaptor.capture());

        assertEquals(2, messageCaptor.getAllValues().size());

        var rawMessage =
                actualLanguage.module().getTranslation("en").getString(Message.Language.LIST);
        assertEquals(
                TestUtils.placeholderize(rawMessage,
                        Pair.of(Placeholder.LANGUAGE, "\n   en: English\n   de: Deutsch")),
                messageCaptor.getAllValues().get(0));
        assertEquals(
                TestUtils.placeholderize(rawMessage,
                        Pair.of(Placeholder.LANGUAGE, "\n   en: English\n   de: Deutsch")),
                messageCaptor.getAllValues().get(1));
    }

    @Test
    void execute_CHANGE() {
        PluginManager mockPluginManager = mock(PluginManager.class);
        when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

        var commandData = new LanguageCommand.CommandData(
                LanguageCommand.CommandType.CHANGE, "de");

        assertEquals("en", playerModel.language);

        assertDoesNotThrow(() -> {
            commandInstance.execute(mockPlayer, commandData);
        });

        assertEquals("de", playerModel.language, "Player language was not changed");

        verify(mockPlayer).sendMessage(messageCaptor.capture());
        verify(mockPlayersData).markPlayerModelForSave(playerModel);
        verify(mockPluginManager).callEvent(new LanguageChangeEvent(mockPlayer, "de",
                actualLanguage.module().getTranslation("de")));

        assertEquals(1, messageCaptor.getAllValues().size());

        var rawMessage =
                actualLanguage.module().getTranslation("de")
                        .getString(Message.Language.CHANGE_SUCCESS);
        assertEquals(TestUtils.placeholderize(rawMessage, Pair.of("", "")),
                messageCaptor.getValue());
    }

    @Test
    void execute_NULL() {
        var commandData = new LanguageCommand.CommandData(null, "de");

        assertEquals("en", playerModel.language);

        assertThrows(NullPointerException.class, () -> {
            commandInstance.execute(mockPlayer, commandData);
        });

        assertEquals("en", playerModel.language, "Player language should not be changed");
    }

    @ParameterizedTest
    @ValueSource(strings = {"info", "INFO", "iNfO", "iNFO", "inFO", "infO", "inFo"})
    void parse_INFO(String arg) {
        var args = new String[] {arg};

        assertDoesNotThrow(() -> {
            var commandData = commandInstance.parse(mockSender, args);

            assertEquals(CommandType.INFO, commandData.commandType(),
                    "Command type should be info");
            assertNull(commandData.language());
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"list", "LIST", "lIsT", "liST", "lisT", "liSt"})
    void parse_LIST(String arg) {
        var args = new String[] {arg};

        assertDoesNotThrow(() -> {
            var commandData = commandInstance.parse(mockSender, args);

            assertNotNull(commandData, "Command data should be parsed successfully");
            assertEquals(CommandType.LIST, commandData.commandType(),
                    "Command type should be list");
            assertNull(commandData.language());
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "de"})
    void parse_CHANGE_EXIST(String arg) {
        var args = new String[] {arg};

        assertDoesNotThrow(() -> {
            var commandData = commandInstance.parse(mockSender, args);

            assertNotNull(commandData, "Command data should be parsed successfully");
            assertEquals(CommandType.CHANGE, commandData.commandType(),
                    "Command type should be change");
            assertNotNull(commandData.language());
            assertEquals(arg, commandData.language());
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"fr", "ro"})
    void parse_CHANGE_NOT_EXIST(String arg) {
        var args = new String[] {arg};

        assertDoesNotThrow(() -> {
            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData, "Command data should be null");
            verify(mockSender).sendMessage(anyString());
        });
    }

    @ParameterizedTest
    @MethodSource("getInvalidArguments")
    void parse_InvalidArguments(String[] args) {
        assertDoesNotThrow(() -> {
            var commandData = commandInstance.parse(mockSender, args);

            assertNull(commandData, "Command data should be null");
            verify(mockSender).sendMessage(anyString());
        });
    }

    static Stream<Arguments> getInvalidArguments() {
        return Stream.of(
                Arguments.of((Object) new String[] {"info", "arg1"}),
                Arguments.of((Object) new String[] {"info", "de"}),
                Arguments.of((Object) new String[] {"list", "arg1"}),
                Arguments.of((Object) new String[] {"change", "arg1"}),
                Arguments.of((Object) new String[] {"change", "de"}),
                Arguments.of((Object) new String[] {"arg0", "arg1", "arg3"}));
    }

    @ParameterizedTest
    @MethodSource("getTabCompletions")
    void onTabComplete(String[] args, String[] expectedCompletions) {
        assertDoesNotThrow(() -> {
            var completions = commandInstance.onTabComplete(args);

            assertNotNull(completions, "Completions should not be null");
            assertArrayEquals(expectedCompletions, completions.toArray());
        });
    }

    static Stream<Arguments> getTabCompletions() {
        return Stream.of(
                Arguments.of((Object) new String[] {},
                        (Object) new String[] {"en", "de", "info", "list"}),
                Arguments.of((Object) new String[] {"e"}, (Object) new String[] {"en"}),
                Arguments.of((Object) new String[] {"en"}, (Object) new String[] {"en"}),
                Arguments.of((Object) new String[] {"d"}, (Object) new String[] {"de"}),
                Arguments.of((Object) new String[] {"i"}, (Object) new String[] {"info"}),
                Arguments.of((Object) new String[] {"in"}, (Object) new String[] {"info"}),
                Arguments.of((Object) new String[] {"inf"}, (Object) new String[] {"info"}),
                Arguments.of((Object) new String[] {"info"}, (Object) new String[] {"info"}),
                Arguments.of((Object) new String[] {"l"}, (Object) new String[] {"list"}),
                Arguments.of((Object) new String[] {"li"}, (Object) new String[] {"list"}),
                Arguments.of((Object) new String[] {"lis"}, (Object) new String[] {"list"}),
                Arguments.of((Object) new String[] {"list"}, (Object) new String[] {"list"}));
    }

}
