package net.emc.emce.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.emc.emce.PlayerMessaging;
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
                PlayerMessaging.sendMessage("msg_nether_success", Formatting.GOLD, true, x/8, z/8);
                return 1;
            })
        ).executes(c -> {
            PlayerMessaging.sendMessage("msg_nether_err_args", Formatting.RED, true);
            return 1;
        })).executes(c -> {
            int x, z;

            if (client.player != null) {
                x = (int) client.player.getX();
                z = (int) client.player.getZ();

                PlayerMessaging.sendMessage("msg_nether_owncoords", Formatting.GRAY, true);
                PlayerMessaging.sendMessage("msg_nether_success", Formatting.GOLD, true, x/8, z/8);
            }
            else
                PlayerMessaging.sendMessage("msg_nether_err_null", Formatting.RED, true);

            return 1;
        }));
    }
}