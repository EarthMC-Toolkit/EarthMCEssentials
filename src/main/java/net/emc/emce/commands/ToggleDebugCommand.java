
package net.emc.emce.commands;

import net.emc.emce.EarthMCEssentials;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.util.Formatting;

import net.emc.emce.utils.MsgUtils;

public class ToggleDebugCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("toggledebug").executes(c -> {
                if (EarthMCEssentials.isDebugModeEnabled()) {
                    MsgUtils.sendPlayer("msg_debug_disabled", false, Formatting.AQUA, true);
                    EarthMCEssentials.setDebugModeEnabled(false);
                } else {
                    MsgUtils.sendPlayer("msg_debug_enabled", false, Formatting.AQUA, true);
                    EarthMCEssentials.setDebugModeEnabled(true);
                }

                return 1;
            }));
    }
}