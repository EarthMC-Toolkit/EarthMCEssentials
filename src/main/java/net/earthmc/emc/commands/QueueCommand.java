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
        dispatcher.register(ArgumentBuilders.literal("queue").executes(source ->
        {
            if (EMCMod.queue == null)
                source.getSource().sendFeedback(new TranslatableText("EMCE > Couldn't fetch queue size, there may be an issue with the API." + EMCMod.queue).formatted(Formatting.byName("RED")));
            else
                source.getSource().sendFeedback(new TranslatableText("EMCE > Current queue size: " + EMCMod.queue).formatted(Formatting.byName("AQUA")));

            return Command.SINGLE_SUCCESS;
        }));
    }
}
