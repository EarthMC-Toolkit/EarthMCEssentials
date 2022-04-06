
package net.emc.emce.events.commands;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.util.Formatting;
import net.emc.emce.utils.MsgUtils;

import static net.emc.emce.EarthMCEssentials.instance;

public class ToggleDebugCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("toggledebug").executes(c -> {
                if (instance().isDebugModeEnabled()) {
                    MsgUtils.sendPlayer("msg_debug_disabled", false, Formatting.AQUA, true);
                    instance().setDebugModeEnabled(false);
                } else {
                    MsgUtils.sendPlayer("msg_debug_enabled", false, Formatting.AQUA, true);
                    instance().setDebugModeEnabled(true);
                }

                return 1;
            }));
    }
}