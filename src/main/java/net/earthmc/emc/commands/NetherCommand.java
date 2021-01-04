package net.earthmc.emc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.earthmc.emc.EMCMod;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class NetherCommand 
{
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher)
    {
        dispatcher.register(
        ArgumentBuilders.literal("nether").then(
            ArgumentBuilders.argument("x", IntegerArgumentType.integer()).then(
                ArgumentBuilders.argument("z", IntegerArgumentType.integer()).executes(c -> {

                int x = IntegerArgumentType.getInteger(c, "x");
                int z = IntegerArgumentType.getInteger(c, "z");
                c.getSource().sendFeedback(new TranslatableText("msg_nether_success", x/8, z/8).formatted(Formatting.GOLD));
                return Command.SINGLE_SUCCESS;

            })
        ).executes(c -> {
            c.getSource().sendFeedback(new TranslatableText("msg_nether_err_args"));
            return Command.SINGLE_SUCCESS;
        })).executes(c ->
        {
            int x;
            int z;

            if (EMCMod.client.player != null)
            {
                x = (int) EMCMod.client.player.getX();
                z = (int) EMCMod.client.player.getZ();

                c.getSource().sendFeedback(new TranslatableText("msg_nether_owncoords"));
                c.getSource().sendFeedback(new TranslatableText("msg_nether_success", x/8, z/8).formatted(Formatting.GOLD));
            }
            else {
                c.getSource().sendFeedback(new TranslatableText("msg_nether_err_null"));
            }

            return Command.SINGLE_SUCCESS;
        }));
    }
}