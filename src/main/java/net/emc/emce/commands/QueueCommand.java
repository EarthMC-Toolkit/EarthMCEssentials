package net.emc.emce.commands;

import net.emc.emce.EarthMCEssentials;
import net.emc.emce.utils.MsgUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.util.Formatting;

public class QueueCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("queuesize").executes(source -> {
            if (EarthMCEssentials.instance().getServerData() == null)
                MsgUtils.sendPlayer("msg_queue_err", false, Formatting.RED, true);
            else
                MsgUtils.sendPlayer("msg_queue_success", false, Formatting.AQUA, true, EarthMCEssentials.instance().getServerData().getQueue());
            return 1;
        }));
    }
}
