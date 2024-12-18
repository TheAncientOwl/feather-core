/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file MessageTest.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @test_unit Message#0.1
 * @description Unit tests for Message
 */

package mc.owls.valley.net.feathercore.api.common.language;

import org.junit.jupiter.api.Test;

public class MessageTest {
    @Test
    @SuppressWarnings("unused")
    void dummyConstructor() {
        var Message = new Message(); // Message should contain only static fields
        var General = new Message.General(); // Message.General should contain only static fields
        var Reload = new Message.Reload(); // Message.Reload should contain only static fields
        var Language = new Message.Language(); // Message.Language should contain only static fields
        var PvPManager = new Message.PvPManager(); // Message.PvPManager should contain only static fields
        var Economy = new Message.Economy(); // Message.Economy should contain only static fields
        var LootChests = new Message.LootChests(); // Message.LootChests should contain only static fields
        var Teleport = new Message.Teleport(); // Message.Teleport should contain only static fields
    }
}
