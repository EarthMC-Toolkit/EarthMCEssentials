package net.emc.emce.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.emc.emce.utils.MsgUtils;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.queue;

public class QueueCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("queuesize").executes(source -> {
            if (queue == null)
                MsgUtils.SendPlayer("msg_queue_err", false, Formatting.RED, true);
            else
                MsgUtils.SendPlayer("msg_queue_success", false, Formatting.AQUA, true, queue);

            return 1;
        }));
    }
}
