package net.emc.emce.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.queue;

public class QueueCommand
{
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher)
    {
        dispatcher.register(ArgumentBuilders.literal("queuesize").executes(source ->
        {
            if (queue >= 0) source.getSource().sendFeedback(new TranslatableText("msg_queue_success", queue).formatted(Formatting.GOLD));
            else source.getSource().sendFeedback(new TranslatableText("msg_queue_err"));

            return 1;
        }));
    }
}
