package net.emc.emce.commands;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.debugModeEnabled;

import net.emc.emce.utils.MsgUtils;

public class ToggleDebugCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("toggledebug").executes(c -> {
                if (debugModeEnabled) {
                    MsgUtils.sendPlayer("msg_debug_disabled", false, Formatting.AQUA, true);
                    debugModeEnabled = false;
                } else {
                    MsgUtils.sendPlayer("msg_debug_enabled", false, Formatting.AQUA, true);
                    debugModeEnabled = true;
                }

                return 1;
            }));
    }
}
