package net.earthmc.emc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.earthmc.emc.EMCMod;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class QueueCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("queuesize").executes(source -> {
            if (EMCMod.queue == null)
                source.getSource().sendFeedback(new TranslatableText("msg_queue_err"));
            else
                source.getSource().sendFeedback(new TranslatableText("msg_queue_success", EMCMod.queue).formatted(Formatting.GOLD));

            return Command.SINGLE_SUCCESS;
        }));
    }
}
