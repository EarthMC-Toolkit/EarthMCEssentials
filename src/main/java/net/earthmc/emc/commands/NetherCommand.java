package net.earthmc.emc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.earthmc.emc.EMCMod;
import net.minecraft.text.TranslatableText;

public class NetherCommand 
{
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher)
    {
        dispatcher.register(
        ArgumentBuilders.literal("nether").then(
            ArgumentBuilders.argument("x", IntegerArgumentType.integer()).then(
                ArgumentBuilders.argument("y", IntegerArgumentType.integer()).executes(c -> {

                System.out.println("X is " + IntegerArgumentType.getInteger(c, "x"));
                System.out.println("Y is " + IntegerArgumentType.getInteger(c, "y"));
                return Command.SINGLE_SUCCESS;

            })
        ).executes(c -> {
            System.out.println("X is " + IntegerArgumentType.getInteger(c, "x"));
            return Command.SINGLE_SUCCESS;
        }))
        .executes(c -> {
            int x = (int) EMCMod.client.player.getX();
            int z = (int) EMCMod.client.player.getZ();
            c.getSource().sendFeedback(new TranslatableText("EMCE > No coordinates specified, using your own instead.")); //TODO: figure out a way to use formatting codes for colors.
            c.getSource().sendFeedback(new TranslatableText("EMCE > Nether coordinates for " + x + ", " + z + ": " + x / 8 + ", " + z / 8));
            return Command.SINGLE_SUCCESS;
        }));
    }
}