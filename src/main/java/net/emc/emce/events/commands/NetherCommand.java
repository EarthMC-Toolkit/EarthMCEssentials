package net.emc.emce.events.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.emc.emce.utils.Translation;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class NetherCommand implements ICommand {
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("nether").executes(ctx -> execNetherCurPos())
            .then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
            .then(ClientCommandManager.argument("z", IntegerArgumentType.integer()).executes(this::execNetherArgs))
            .executes(ctx -> {
                Messaging.send("msg_nether_err_args");
                return 1;
            }));
    }
    
    // Uses the players current position when converting to nether coords.
    public int execNetherCurPos() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            Messaging.send("msg_nether_err_null");
            return 1;
        }
        
        Messaging.sendPrefixed("msg_nether_owncoords");
        Messaging.sendPrefixed(Translation.of("msg_nether_success",
            player.getBlockX()/8,
            player.getBlockZ()/8
        ));
        
        return 1;
    }
    
    // Use the X and Z arguments given by the user when running the command.
    public int execNetherArgs(CommandContext<FabricClientCommandSource> ctx) {
        int x = IntegerArgumentType.getInteger(ctx, "x");
        int z = IntegerArgumentType.getInteger(ctx, "z");
        
        Messaging.send(Translation.of("msg_nether_success", x/8, z/8));
        
        return 1;
    }
}