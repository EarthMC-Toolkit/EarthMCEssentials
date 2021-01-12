package net.emc.emce.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import static net.emc.emce.EMCE.client;

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

                return 1;
            })
        ).executes(c -> {
            c.getSource().sendFeedback(new TranslatableText("msg_nether_err_args"));
            return 1;
        })).executes(c ->
        {
            int x;
            int z;

            if (client.player != null)
            {
                x = (int) client.player.getX();
                z = (int) client.player.getZ();

                c.getSource().sendFeedback(new TranslatableText("msg_nether_owncoords"));
                c.getSource().sendFeedback(new TranslatableText("msg_nether_success", x/8, z/8).formatted(Formatting.GOLD));

                return 1;
            }
            else {
                c.getSource().sendFeedback(new TranslatableText("msg_nether_err_null"));

                return -1;
            }
        }));
    }
}