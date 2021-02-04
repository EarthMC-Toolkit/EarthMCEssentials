package net.emc.emce.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.emc.emce.PlayerMessaging;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.queue;

public class QueueCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("queuesize").executes(source -> {
            if (queue == null)
                PlayerMessaging.sendMessage("msg_queue_err", Formatting.RED, true);
            else
                PlayerMessaging.sendMessage("msg_queue_success", Formatting.AQUA, true, queue);

            return 1;
        }));
    }
}
