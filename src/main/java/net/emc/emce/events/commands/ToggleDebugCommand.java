
package net.emc.emce.events.commands;

import net.emc.emce.object.Translation;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.emc.emce.utils.Messaging;

import static net.emc.emce.EarthMCEssentials.instance;

public class ToggleDebugCommand {
    public void register() {
        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("toggledebug").executes(c -> {
                if (instance().isDebugModeEnabled()) {
                    Messaging.sendMessage(Translation.of("msg_debug_disabled"));
                    instance().setDebugModeEnabled(false);
                } else {
                    Messaging.sendMessage(Translation.of("msg_debug_enabled"));
                    instance().setDebugModeEnabled(true);
                }

                return 1;
            })
        );
    }
}