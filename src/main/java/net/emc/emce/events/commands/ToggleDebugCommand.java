
package net.emc.emce.events.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.emc.emce.utils.Translation;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.emc.emce.EarthMCEssentials.instance;

public class ToggleDebugCommand {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            ClientCommandManager.literal("toggledebug").executes(c -> {
                if (instance().isDebugModeEnabled()) {
                    Messaging.send(Translation.of("msg_debug_disabled"));
                    instance().setDebugModeEnabled(false);
                } else {
                    Messaging.send(Translation.of("msg_debug_enabled"));
                    instance().setDebugModeEnabled(true);
                }

                return 1;
            })
        );
    }
}